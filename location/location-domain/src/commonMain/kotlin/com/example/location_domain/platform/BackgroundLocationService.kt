package com.example.location_domain.platform

import com.example.shared.data.model.event.RoundOfGolfEvent
import kotlinx.coroutines.flow.Flow

interface BackgroundLocationService {
    fun startBackgroundLocationTracking(intervalMs: Long): Flow<RoundOfGolfEvent.LocationUpdated>
    fun stopBackgroundLocationTracking()
    val isBackgroundTrackingActive: Flow<Boolean>
}