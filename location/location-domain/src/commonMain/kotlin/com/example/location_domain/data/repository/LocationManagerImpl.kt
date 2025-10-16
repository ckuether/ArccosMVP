package com.example.location_domain.data.repository

import com.example.location_domain.domain.repository.LocationManager
import com.example.location_domain.domain.model.LocationResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocationManagerImpl(
    private val locationProvider: LocationProvider
) : LocationManager {
    
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