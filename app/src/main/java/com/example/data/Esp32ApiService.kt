package com.example.data

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

data class SensorData(
    val temperature: Float = 0f,
    val humidity: Float = 0f,
    val pumpState: Boolean = false
)

interface Esp32ApiService {
    @GET("/")
    suspend fun getStatus(): SensorData

    @POST("/pump")
    suspend fun togglePump(@Query("state") state: Int): SensorData
}
