package com.example.location.domain.usecase

import com.example.location.domain.service.LocationTrackingService
import com.example.shared.data.model.event.RoundOfGolfEvent
import kotlinx.coroutines.flow.Flow

class StartLocationTrackingUseCase(
    private val locationTrackingService: LocationTrackingService
) {
    suspend operator fun invoke(): Flow<RoundOfGolfEvent.LocationUpdated> {
        return locationTrackingService.startLocationTracking()
    }
}