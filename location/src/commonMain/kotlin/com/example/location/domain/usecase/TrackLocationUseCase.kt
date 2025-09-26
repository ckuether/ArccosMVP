package com.example.location.domain.usecase

import com.example.location.domain.repository.LocationRepository
import com.example.location.domain.model.LocationResult
import com.example.shared.event.InPlayEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackLocationUseCase(
    private val locationRepository: LocationRepository,
    private val checkLocationPermission: CheckLocationPermissionUseCase
) {
    suspend fun execute(intervalMs: Long = 5000): Flow<InPlayEvent.LocationUpdated> {
        if (!checkLocationPermission()) {
            throw LocationPermissionException("Location permission is required")
        }
        
        if (!locationRepository.isLocationEnabled()) {
            throw LocationDisabledException("Location services are disabled")
        }
        
        return locationRepository.startLocationUpdates(intervalMs)
            .map { result ->
                when (result) {
                    is LocationResult.Success -> InPlayEvent.LocationUpdated(result.location)
                    is LocationResult.Error -> throw LocationException(result.message)
                    is LocationResult.PermissionDenied -> throw LocationPermissionException("Location permission denied")
                    is LocationResult.LocationDisabled -> throw LocationDisabledException("Location services disabled")
                }
            }
    }
    
    suspend fun stop() {
        locationRepository.stopLocationUpdates()
    }
}

open class LocationException(message: String) : Exception(message)
class LocationPermissionException(message: String) : LocationException(message)
class LocationDisabledException(message: String) : LocationException(message)