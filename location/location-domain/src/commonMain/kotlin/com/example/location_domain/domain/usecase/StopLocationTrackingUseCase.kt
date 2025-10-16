package com.example.location_domain.domain.usecase

import com.example.location_domain.domain.service.LocationTrackingService

class StopLocationTrackingUseCase(
    private val locationTrackingService: LocationTrackingService
) {
    suspend operator fun invoke() {
        locationTrackingService.stopLocationTracking()
    }
}