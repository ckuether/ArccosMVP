package com.example.location_domain.domain.usecase

import com.example.location_domain.domain.repository.LocationManager
import com.example.location_domain.domain.model.LocationResult
import com.example.shared.data.model.RoundOfGolfEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackLocationUseCase(
    private val locationManager: LocationManager,
    private val checkLocationPermission: CheckLocationPermissionUseCase
) {
    suspend fun execute(intervalMs: Long = 5000): Flow<RoundOfGolfEvent.LocationUpdated> {
        if (!checkLocationPermission()) {
            throw LocationPermissionException("Location permission is required")
        }
        
        if (!locationManager.isLocationEnabled()) {
            throw LocationDisabledException("Location services are disabled")
        }
        
        return locationManager.startLocationUpdates(intervalMs)
            .map { result ->
                when (result) {
                    is LocationResult.Success -> RoundOfGolfEvent.LocationUpdated(location = result.location)
                    is LocationResult.Error -> throw LocationException(result.message)
                    is LocationResult.PermissionDenied -> throw LocationPermissionException("Location permission denied")
                    is LocationResult.LocationDisabled -> throw LocationDisabledException("Location services disabled")
                }
            }
    }
    
    suspend fun stop() {
        locationManager.stopLocationUpdates()
    }
}

open class LocationException(message: String) : Exception(message)
class LocationPermissionException(message: String) : LocationException(message)
class LocationDisabledException(message: String) : LocationException(message)