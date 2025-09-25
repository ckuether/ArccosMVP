package com.example.shared.domain.usecase

import com.example.shared.data.event.InPlayEvent
import com.example.shared.domain.repository.LocationRepository
import com.example.shared.domain.model.LocationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class TrackLocationUseCase(
    private val locationRepository: LocationRepository
) {
    suspend fun execute(intervalMs: Long = 5000): Flow<InPlayEvent.LocationUpdated> {
        if (!locationRepository.hasLocationPermission()) {
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

sealed class LocationException(message: String) : Exception(message)
class LocationPermissionException(message: String) : LocationException(message)
class LocationDisabledException(message: String) : LocationException(message)