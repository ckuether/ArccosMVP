package com.example.location_domain.domain.usecase

import com.example.location_domain.domain.service.LocationTrackingService
import com.example.shared.data.model.RoundOfGolfEvent
import kotlinx.coroutines.flow.Flow

class StartLocationTrackingUseCase(
    private val locationTrackingService: LocationTrackingService
) {
    suspend operator fun invoke(): Flow<RoundOfGolfEvent.LocationUpdated> {
        return locationTrackingService.startLocationTracking()
    }
}