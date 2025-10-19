package com.example.location_domain.domain.service

import com.example.shared.data.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationTrackingService {
    suspend fun startLocationTracking(): Flow<Location>
    suspend fun stopLocationTracking()
    val isTracking: Flow<Boolean>
}