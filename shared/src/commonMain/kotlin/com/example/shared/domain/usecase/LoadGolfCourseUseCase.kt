package com.example.shared.domain.usecase

import com.example.shared.data.repository.GolfCourseRepository
import com.example.shared.data.model.GolfCourse
import com.example.shared.platform.Logger

class LoadGolfCourseUseCase(
    private val golfCourseRepository: GolfCourseRepository,
    private val logger: Logger
) {
    companion object {
        private const val TAG = "LoadGolfCourseUseCase"
    }
    
    suspend operator fun invoke(): Result<GolfCourse?> {
        return try {
            val course = golfCourseRepository.loadGolfCourse()
            logger.debug(TAG, "Golf course loaded: ${course?.name}")
            Result.success(course)
        } catch (e: Exception) {
            logger.error(TAG, "Failed to load golf course", e)
            Result.failure(e)
        }
    }
}