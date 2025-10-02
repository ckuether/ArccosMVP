package com.example.location.platform

import com.example.shared.data.model.event.InPlayEvent
import kotlinx.coroutines.flow.Flow

interface BackgroundLocationService {
    fun startBackgroundLocationTracking(intervalMs: Long): Flow<InPlayEvent.LocationUpdated>
    fun stopBackgroundLocationTracking()
    val isBackgroundTrackingActive: Flow<Boolean>
}