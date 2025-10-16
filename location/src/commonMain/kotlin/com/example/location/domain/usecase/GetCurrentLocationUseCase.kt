package com.example.location.domain.usecase

import com.example.location.domain.model.LocationResult
import com.example.location.domain.repository.LocationManager

class GetCurrentLocationUseCase(
    private val locationManager: LocationManager,
    private val checkLocationPermission: CheckLocationPermissionUseCase
) {
    suspend operator fun invoke(): LocationResult {
        return if (checkLocationPermission()) {
            if (locationManager.isLocationEnabled()) {
                locationManager.getCurrentLocation()
            } else {
                LocationResult.LocationDisabled
            }
        } else {
            LocationResult.PermissionDenied
        }
    }
}