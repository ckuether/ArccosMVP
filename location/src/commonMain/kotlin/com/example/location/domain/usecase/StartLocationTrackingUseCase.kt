package com.example.location.domain.usecase

import com.example.location.domain.service.LocationTrackingService
import com.example.shared.data.model.event.InPlayEvent
import kotlinx.coroutines.flow.Flow

class StartLocationTrackingUseCase(
    private val locationTrackingService: LocationTrackingService
) {
    suspend operator fun invoke(): Flow<InPlayEvent.LocationUpdated> {
        return locationTrackingService.startLocationTracking()
    }
}