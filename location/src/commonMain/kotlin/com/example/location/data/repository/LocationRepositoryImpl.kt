package com.example.location.data.repository

import com.example.location.domain.repository.LocationRepository
import com.example.location.domain.model.LocationResult
import com.example.shared.data.entity.toEntity
import com.example.shared.data.model.Location
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class LocationRepositoryImpl(
    private val locationProvider: LocationProvider,
    private val locationDao: LocationDao
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
    
    override suspend fun saveLocation(location: Location, timestamp: Long) {
        locationDao.insertLocation(location.toEntity(timestamp))
    }
    
    override fun getStoredLocations(): Flow<List<Location>> {
        return locationDao.getAllLocations().map { entities ->
            entities.map { it.toLocation() }
        }
    }
    
    override suspend fun clearStoredLocations() {
        locationDao.deleteAllLocations()
    }
    
    override suspend fun getLocationCount(): Int {
        return locationDao.getLocationCount()
    }
}

interface LocationProvider {
    suspend fun getCurrentLocation(): LocationResult
}