package com.example.location.domain.usecase

import com.example.location.domain.service.LocationTrackingService

class StopLocationTrackingUseCase(
    private val locationTrackingService: LocationTrackingService
) {
    suspend operator fun invoke() {
        locationTrackingService.stopLocationTracking()
    }
}