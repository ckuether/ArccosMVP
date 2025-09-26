package com.example.location.data.repository

import com.example.location.domain.repository.LocationRepository
import com.example.location.domain.model.LocationResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocationRepositoryImpl(
    private val locationProvider: LocationProvider
) : LocationRepository {
    
    private var isUpdating = false
    
    override suspend fun getCurrentLocation(): LocationResult {
        return locationProvider.getCurrentLocation()
    }
    
    override fun startLocationUpdates(intervalMs: Long): Flow<LocationResult> = flow {
        if (isUpdating) return@flow
        
        isUpdating = true
        
        try {
            while (isUpdating) {
                val result = locationProvider.getCurrentLocation()
                emit(result)
                delay(intervalMs)
            }
        } finally {
            isUpdating = false
        }
    }
    
    override suspend fun stopLocationUpdates() {
        isUpdating = false
    }
    
    override suspend fun isLocationEnabled(): Boolean {
        // This needs platform-specific implementation
        // For now, assume it's always enabled
        return true
    }
}

interface LocationProvider {
    suspend fun getCurrentLocation(): LocationResult
}