package com.example.location.domain.usecase

import com.example.shared.data.dao.LocationDao
import com.example.shared.platform.Logger

class ClearLocationEventsUseCase(
    private val locationDao: LocationDao,
    private val logger: Logger
) {
    companion object {
        private const val TAG = "ClearLocationEventsUseCase"
    }
    
    suspend operator fun invoke(): Result<Unit> {
        return try {
            locationDao.deleteAllLocations()
            logger.info(TAG, "All location events cleared from database")
            Result.success(Unit)
        } catch (e: Exception) {
            logger.error(TAG, "Failed to clear location events", e)
            Result.failure(e)
        }
    }
}