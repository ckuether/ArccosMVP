package com.example.shared.domain.repository

import com.example.shared.domain.model.LocationResult
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun getCurrentLocation(): LocationResult
    fun startLocationUpdates(intervalMs: Long): Flow<LocationResult>
    suspend fun stopLocationUpdates()
    suspend fun hasLocationPermission(): Boolean
    suspend fun isLocationEnabled(): Boolean
}