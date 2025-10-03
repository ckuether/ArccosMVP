package com.example.location.domain.usecase

import com.example.shared.data.dao.LocationDao
import com.example.shared.data.entity.toEntity
import com.example.shared.data.model.Location
import com.example.shared.platform.Logger

class SaveLocationEventUseCase(
    private val locationDao: LocationDao,
    private val logger: Logger
) {
    companion object {
        private const val TAG = "SaveLocationEventUseCase"
    }
    
    suspend operator fun invoke(location: Location, roundId: Long): Result<Unit> {
        return try {
            locationDao.insertLocation(location.toEntity(roundId))
            logger.debug(TAG, "Location saved to database successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            logger.error(TAG, "Failed to save location to database", e)
            Result.failure(e)
        }
    }
}