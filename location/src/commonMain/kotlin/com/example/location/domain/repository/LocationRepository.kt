package com.example.location.domain.repository

import com.example.location.domain.model.LocationResult
import com.example.shared.data.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun getCurrentLocation(): LocationResult
    fun startLocationUpdates(intervalMs: Long): Flow<LocationResult>
    suspend fun stopLocationUpdates()
    suspend fun isLocationEnabled(): Boolean
    
    // Database operations
    suspend fun saveLocation(location: Location, timestamp: Long)
    fun getStoredLocations(): Flow<List<Location>>
    suspend fun clearStoredLocations()
    suspend fun getLocationCount(): Int
}