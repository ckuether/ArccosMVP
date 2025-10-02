package com.example.location.domain.usecase

import com.example.shared.data.dao.InPlayEventDao
import com.example.shared.data.entity.toEntity
import com.example.shared.data.model.event.InPlayEvent
import com.example.shared.platform.Logger

class SaveLocationEventUseCase(
    private val inPlayEventDao: InPlayEventDao,
    private val logger: Logger
) {
    companion object {
        private const val TAG = "SaveLocationEventUseCase"
    }
    
    suspend operator fun invoke(locationEvent: InPlayEvent.LocationUpdated, roundID: Long): Result<Unit> {
        return try {
            inPlayEventDao.insertEvent(locationEvent.toEntity(roundID))
            logger.debug(TAG, "Location event saved to database successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            logger.error(TAG, "Failed to save location event to database", e)
            Result.failure(e)
        }
    }
}