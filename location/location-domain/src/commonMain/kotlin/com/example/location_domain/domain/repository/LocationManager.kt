package com.example.location_domain.domain.repository

import com.example.location_domain.domain.model.LocationResult
import kotlinx.coroutines.flow.Flow

interface LocationManager {
    suspend fun getCurrentLocation(): LocationResult
    fun startLocationUpdates(intervalMs: Long): Flow<LocationResult>
    suspend fun stopLocationUpdates()
    suspend fun isLocationEnabled(): Boolean
}