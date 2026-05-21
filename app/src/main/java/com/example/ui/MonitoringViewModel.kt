package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Esp32ApiService
import com.example.data.SensorData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PumpHistoryLog(
    val date: String,
    val time: String,
    val title: String,
    val description: String,
    val isManual: Boolean
)

sealed interface MonitoringUiState {
    data object Loading : MonitoringUiState
    data class Success(val sensorData: SensorData) : MonitoringUiState
    data class Error(val message: String, val lastData: SensorData? = null) : MonitoringUiState
}

class MonitoringViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<MonitoringUiState>(MonitoringUiState.Loading)
    val uiState: StateFlow<MonitoringUiState> = _uiState.asStateFlow()

    private val _ipAddress = MutableStateFlow("192.168.4.1") // Default ESP32 AP IP
    val ipAddress: StateFlow<String> = _ipAddress.asStateFlow()

    private val _historyLogs = MutableStateFlow<List<PumpHistoryLog>>(
        listOf(
            PumpHistoryLog("Hari ini", "08:00", "Penyiraman Terjadwal", "Pompa menyala otomatis", false),
            PumpHistoryLog("Kemarin", "17:00", "Penyiraman Terjadwal", "Pompa menyala otomatis", false),
            PumpHistoryLog("Kemarin", "08:00", "Penyiraman Terjadwal", "Pompa menyala otomatis", false),
            PumpHistoryLog("2 Hari Lalu", "11:30", "Penyiraman Manual", "Pompa diaktifkan oleh pengguna", true)
        )
    )
    val historyLogs: StateFlow<List<PumpHistoryLog>> = _historyLogs.asStateFlow()

    private var apiService: Esp32ApiService? = null
    
    // Fallback Mock Data for UI demonstration without real ESP32
    private var mockData = SensorData(temperature = 28.5f, humidity = 64.0f, pumpState = false)

    init {
        updateApiService(_ipAddress.value)
        startPolling()
    }

    fun setIpAddress(ip: String) {
        _ipAddress.value = ip
        updateApiService(ip)
    }

    private fun updateApiService(ip: String) {
        if (ip.isBlank()) return
        try {
            val client = OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .build()

            val baseUrl = if (ip.startsWith("http")) ip else "http://$ip/"
            
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

            apiService = retrofit.create(Esp32ApiService::class.java)
        } catch (e: Exception) {
            _uiState.update { MonitoringUiState.Error("Invalid URL format", mockData) }
        }
    }

    private fun startPolling() {
        viewModelScope.launch {
            while (isActive) {
                fetchData()
                delay(3000) // Poll every 3 seconds
            }
        }
    }

    fun fetchData() {
        viewModelScope.launch {
            try {
                apiService?.let {
                    val data = it.getStatus()
                    _uiState.update { MonitoringUiState.Success(data) }
                } ?: run {
                    // Provide mock data if no service (e.g. testing)
                    _uiState.update { MonitoringUiState.Success(mockData) }
                }
            } catch (e: Exception) {
                // If network fails, show error but retain mock data so UI looks good in AI Studio preview
                 _uiState.update { MonitoringUiState.Error("Tidak dapat terhubung ke ESP32. Menampilkan simulasi:", mockData) }
            }
        }
    }

    fun togglePump(turnOn: Boolean) {
        viewModelScope.launch {
            val stateInt = if (turnOn) 1 else 0
            
            // Optimistic UI update / Mock Mode update
            mockData = mockData.copy(pumpState = turnOn)
            val currentState = _uiState.value
            if (currentState is MonitoringUiState.Success) {
                 _uiState.update { MonitoringUiState.Success(currentState.sensorData.copy(pumpState = turnOn)) }
            } else if (currentState is MonitoringUiState.Error) {
                 _uiState.update { currentState.copy(lastData = currentState.lastData?.copy(pumpState = turnOn)) }
            }

            if (turnOn) {
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val timeStr = timeFormat.format(Date())
                _historyLogs.update { logs ->
                    listOf(PumpHistoryLog("Hari ini", timeStr, "Penyiraman Manual", "Pompa diaktifkan oleh pengguna", true)) + logs
                }
            }

            try {
                apiService?.let {
                    val response = it.togglePump(stateInt)
                    _uiState.update { state -> MonitoringUiState.Success(response) }
                }
            } catch (e: Exception) {
                // Failed to call ESP32, fallback remains as we updated optimistic UI.
                val errData = if (currentState is MonitoringUiState.Success) currentState.sensorData else mockData
                _uiState.update { MonitoringUiState.Error("Gagal menekan tombol. Menampilkan simulasi:", errData) }
            }
        }
    }
}
