package com.example.location.domain.repository

import com.example.location.domain.model.LocationResult
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun getCurrentLocation(): LocationResult
    fun startLocationUpdates(intervalMs: Long): Flow<LocationResult>
    suspend fun stopLocationUpdates()
    suspend fun isLocationEnabled(): Boolean
}