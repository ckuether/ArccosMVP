package com.example.location.domain.usecase

import com.example.shared.data.dao.InPlayEventDao
import com.example.shared.data.entity.toInPlayEvent
import com.example.shared.data.model.event.InPlayEvent
import com.example.shared.data.model.Location
import com.example.shared.platform.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetLocationEventsUseCase(
    private val inPlayEventDao: InPlayEventDao,
    private val logger: Logger
) {
    companion object {
        private const val TAG = "GetLocationEventsUseCase"
    }
    
    operator fun invoke(): Flow<List<LocationItem>> {
        return inPlayEventDao.getEventsByType("LocationUpdated")
            .map { entities ->
                entities.mapNotNull { entity ->
                    try {
                        val event = entity.toInPlayEvent() as? InPlayEvent.LocationUpdated
                        event?.let { 
                            LocationItem(
                                location = it.location,
                                timestamp = it.timestamp
                            )
                        }
                    } catch (e: Exception) {
                        logger.error(TAG, "Failed to convert entity to LocationItem", e)
                        null
                    }
                }
            }
    }
}

data class LocationItem(
    val location: Location,
    val timestamp: Long // Epoch milliseconds
)