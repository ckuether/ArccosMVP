package com.example.location.domain.usecase

import com.example.location.domain.model.LocationResult
import com.example.location.domain.repository.LocationRepository

class GetLocationUseCase(
    private val locationRepository: LocationRepository,
    private val checkLocationPermission: CheckLocationPermissionUseCase
) {
    suspend operator fun invoke(): LocationResult {
        return if (checkLocationPermission()) {
            if (locationRepository.isLocationEnabled()) {
                locationRepository.getCurrentLocation()
            } else {
                LocationResult.LocationDisabled
            }
        } else {
            LocationResult.PermissionDenied
        }
    }
}