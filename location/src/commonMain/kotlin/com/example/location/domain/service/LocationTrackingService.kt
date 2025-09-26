package com.example.location.domain.service

import com.example.shared.event.InPlayEvent
import kotlinx.coroutines.flow.Flow

interface LocationTrackingService {
    suspend fun startLocationTracking(): Flow<InPlayEvent.LocationUpdated>
    suspend fun stopLocationTracking()
    val isTracking: Flow<Boolean>
}