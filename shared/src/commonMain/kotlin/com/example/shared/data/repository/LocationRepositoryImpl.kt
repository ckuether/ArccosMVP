package com.example.shared.data.repository

import com.example.shared.domain.model.LocationResult
import com.example.shared.domain.repository.LocationRepository
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
    
    override suspend fun hasLocationPermission(): Boolean {
        return locationProvider.hasLocationPermission()
    }
    
    override suspend fun isLocationEnabled(): Boolean {
        return locationProvider.isLocationEnabled()
    }
}

expect interface LocationProvider {
    suspend fun getCurrentLocation(): LocationResult
    suspend fun hasLocationPermission(): Boolean
    suspend fun isLocationEnabled(): Boolean
}