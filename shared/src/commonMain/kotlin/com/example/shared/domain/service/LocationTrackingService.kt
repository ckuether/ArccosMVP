package com.example.shared.domain.service

import com.example.shared.data.event.InPlayEvent
import kotlinx.coroutines.flow.Flow

interface LocationTrackingService {
    fun startLocationTracking(): Flow<InPlayEvent.LocationUpdated>
    suspend fun stopLocationTracking()
    val isTracking: Flow<Boolean>
}