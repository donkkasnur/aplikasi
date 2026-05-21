package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shower
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.rounded.DeviceThermostat
import androidx.compose.material.icons.rounded.Opacity
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.ui.PumpHistoryLog
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import com.example.data.SensorData
import com.example.ui.MonitoringUiState
import com.example.ui.MonitoringViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MonitoringApp()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitoringApp(viewModel: MonitoringViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentIp by viewModel.ipAddress.collectAsStateWithLifecycle()
    val historyLogs by viewModel.historyLogs.collectAsStateWithLifecycle()
    var currentTab by remember { mutableIntStateOf(0) }

    val sleekBackground = Color(0xFFF8F9FF)
    val sleekTextPrimary = Color(0xFF1A1C1E)
    val sleekPrimary = Color(0xFF0061A4)
    
    Scaffold(
        containerColor = sleekBackground,
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.img_app_logo_1779362095358),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Monitoring", fontWeight = FontWeight.Medium, color = sleekTextPrimary, fontSize = 20.sp)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = Color(0xFF44474E), modifier = Modifier.size(28.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = sleekBackground,
                    titleContentColor = sleekTextPrimary
                ),
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFEEEFF7),
                tonalElevation = 0.dp
            ) {
                val iconTint = Color(0xFF44474E)
                val selectedColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = sleekTextPrimary,
                    selectedTextColor = sleekTextPrimary,
                    indicatorColor = Color(0xFFD1E4FF)
                )
                val unselectedColors = NavigationBarItemDefaults.colors(
                    unselectedIconColor = iconTint, 
                    unselectedTextColor = iconTint
                )

                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                    label = { Text("Monitor", fontWeight = if (currentTab == 0) FontWeight.Bold else FontWeight.Medium) },
                    colors = if (currentTab == 0) selectedColors else unselectedColors
                )
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    icon = { Icon(Icons.Default.History, contentDescription = null) },
                    label = { Text("Riwayat", fontWeight = if (currentTab == 1) FontWeight.Bold else FontWeight.Medium) },
                    colors = if (currentTab == 1) selectedColors else unselectedColors
                )
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    icon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                    label = { Text("Jadwal", fontWeight = if (currentTab == 2) FontWeight.Bold else FontWeight.Medium) },
                    colors = if (currentTab == 2) selectedColors else unselectedColors
                )
                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = { currentTab = 3 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Sistem", fontWeight = if (currentTab == 3) FontWeight.Bold else FontWeight.Medium) },
                    colors = if (currentTab == 3) selectedColors else unselectedColors
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding).background(sleekBackground)) {
            if (currentTab == 0) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Connection Badge
                    Row(
                        modifier = Modifier
                            .background(Color(0xFFD1E4FF), shape = CircleShape)
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(8.dp).background(sleekPrimary, CircleShape))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ESP32 CONNECTED", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF001D36))
                    }

                    // Content
                    AnimatedContent(
                        targetState = uiState,
                        transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                        label = "ContentState"
                    ) { state ->
                        when (state) {
                            is MonitoringUiState.Loading -> {
                                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                            is MonitoringUiState.Success -> {
                                DashboardContent(
                                    sensorData = state.sensorData,
                                    onPumpToggle = { viewModel.togglePump(it) }
                                )
                            }
                            is MonitoringUiState.Error -> {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                                    ) {
                                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(state.message, color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    // Show fallback data if available
                                    state.lastData?.let {
                                        DashboardContent(
                                            sensorData = it,
                                            onPumpToggle = { turnOn -> viewModel.togglePump(turnOn) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (currentTab == 1) {
                HistoryContent(historyLogs = historyLogs)
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Fitur belum tersedia", color = sleekTextPrimary)
                }
            }
        }
    }
}

@Composable
fun HistoryContent(historyLogs: List<PumpHistoryLog>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "Riwayat Penyiraman", 
                fontSize = 22.sp, 
                fontWeight = FontWeight.Bold, 
                color = Color(0xFF1A1C1E),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        items(historyLogs) { log ->
            HistoryItemCard(log)
        }
        
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun HistoryItemCard(log: PumpHistoryLog) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(if (log.isManual) Color(0xFFFFDAD6) else Color(0xFFD1E4FF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shower, 
                    contentDescription = null, 
                    tint = if (log.isManual) Color(0xFFBA1A1A) else Color(0xFF0061A4),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(log.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A1C1E))
                Text(log.description, fontSize = 12.sp, color = Color(0xFF44474E), modifier = Modifier.padding(top = 2.dp))
                Row(modifier = Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFF0061A4))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${log.date} • ${log.time}", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF74777F))
                }
            }
        }
    }
}

@Composable
fun DashboardContent(sensorData: SensorData, onPumpToggle: (Boolean) -> Unit) {
    val sleekPrimary = Color(0xFF0061A4)
    val cardShadowColor = Color.Black.copy(alpha = 0.05f)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Temperature Card
            SensorCard(
                modifier = Modifier.weight(1f).aspectRatio(1f),
                title = "Suhu",
                value = sensorData.temperature.toString(),
                unit = "°C",
                subtitle = "Ideal Range: 25-30°C",
                icon = { 
                    Box(modifier = Modifier.background(Color(0xFFFFDAD6), RoundedCornerShape(16.dp)).padding(8.dp)) {
                        Icon(Icons.Rounded.DeviceThermostat, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color(0xFFBA1A1A))
                    }
                }
            )

            // Humidity Card
            SensorCard(
                modifier = Modifier.weight(1f).aspectRatio(1f),
                title = "Udara",
                value = sensorData.humidity.toInt().toString(),
                unit = "%",
                subtitle = "Kelembaban Optimal",
                icon = { 
                    Box(modifier = Modifier.background(Color(0xFFD1E4FF), RoundedCornerShape(16.dp)).padding(8.dp)) {
                        Icon(Icons.Rounded.Opacity, contentDescription = null, modifier = Modifier.size(24.dp), tint = sleekPrimary)
                    }
                }
            )
        }

        // Pump Control Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE1E2EC))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(text = "Kontrol Sanyo", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1C1E))
                        Row {
                            Text("Status Relay: ", fontSize = 14.sp, color = Color(0xFF44474E))
                            Text(if (sensorData.pumpState) "ON" else "READY", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = sleekPrimary)
                        }
                    }
                    Icon(imageVector = Icons.Rounded.WaterDrop, contentDescription = null, tint = sleekPrimary, modifier = Modifier.size(32.dp))
                }
                
                Button(
                    onClick = { onPumpToggle(!sensorData.pumpState) },
                    modifier = Modifier.fillMaxWidth().height(80.dp).testTag("pump_toggle_button"),
                    shape = RoundedCornerShape(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (sensorData.pumpState) Color(0xFFBA1A1A) else sleekPrimary
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Icon(Icons.Default.Shower, contentDescription = null, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (sensorData.pumpState) "MATIKAN POMPA" else "SIRAM SEKARANG",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
                
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Terakhir disiram: 2 jam yang lalu", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF44474E))
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Historical Glance
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Pemakaian Listrik Relay", fontSize = 12.sp, color = Color(0xFF74777F))
                    Text("1.2 kWh Hari Ini", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
                }
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.width(4.dp).height(16.dp).background(Color(0xFFD1E4FF), CircleShape))
                    Box(modifier = Modifier.width(4.dp).height(24.dp).background(Color(0xFFD1E4FF), CircleShape))
                    Box(modifier = Modifier.width(4.dp).height(12.dp).background(Color(0xFFD1E4FF), CircleShape))
                    Box(modifier = Modifier.width(4.dp).height(32.dp).background(sleekPrimary, CircleShape))
                }
            }
        }
    }
}

@Composable
fun SensorCard(modifier: Modifier = Modifier, title: String, value: String, unit: String, subtitle: String, icon: @Composable () -> Unit) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(
            modifier = Modifier.padding(20.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                icon()
                Text(text = title.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF44474E))
            }
            
            Column {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(text = value, fontSize = 40.sp, fontWeight = FontWeight.Light, color = Color(0xFF1A1C1E), lineHeight = 40.sp)
                    Text(text = unit, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A1C1E), modifier = Modifier.padding(bottom = 6.dp))
                }
                Text(text = subtitle, fontSize = 10.sp, color = Color(0xFF74777F), modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}
