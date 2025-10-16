package com.example.location_domain.domain.service

import com.example.shared.data.model.event.RoundOfGolfEvent
import kotlinx.coroutines.flow.Flow

interface LocationTrackingService {
    suspend fun startLocationTracking(): Flow<RoundOfGolfEvent.LocationUpdated>
    suspend fun stopLocationTracking()
    val isTracking: Flow<Boolean>
}