package com.example.location.domain.usecase

import com.example.shared.data.dao.LocationDao
import com.example.shared.data.entity.toLocation
import com.example.shared.data.model.Location
import com.example.shared.platform.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetLocationEventsUseCase(
    private val locationDao: LocationDao,
    private val logger: Logger
) {
    companion object {
        private const val TAG = "GetLocationEventsUseCase"
    }
    
    operator fun invoke(roundId: Long): Flow<List<LocationItem>> {
        return locationDao.getLocationsForRound(roundId)
            .map { entities ->
                entities.map { entity ->
                    LocationItem(
                        location = entity.toLocation(),
                        timestamp = entity.timestamp
                    )
                }
            }
    }
    
    operator fun invoke(): Flow<List<LocationItem>> {
        return locationDao.getAllLocations()
            .map { entities ->
                entities.map { entity ->
                    LocationItem(
                        location = entity.toLocation(),
                        timestamp = entity.timestamp
                    )
                }
            }
    }
}

data class LocationItem(
    val location: Location,
    val timestamp: Long // Epoch milliseconds
)