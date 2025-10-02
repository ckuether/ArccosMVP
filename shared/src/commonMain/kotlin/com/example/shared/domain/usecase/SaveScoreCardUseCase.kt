package com.example.shared.domain.usecase

import com.example.shared.data.dao.ScoreCardDao
import com.example.shared.data.entity.toEntity
import com.example.shared.data.model.ScoreCard
import com.example.shared.platform.Logger

class SaveScoreCardUseCase(
    private val scoreCardDao: ScoreCardDao,
    private val logger: Logger
) {
    companion object {
        private const val TAG = "SaveScoreCardUseCase"
    }
    
    suspend operator fun invoke(scoreCard: ScoreCard): Result<Unit> {
        return try {
            scoreCardDao.insertScoreCard(scoreCard.toEntity())
            logger.debug(TAG, "ScoreCard saved to database successfully for round ${scoreCard.roundId}")
            Result.success(Unit)
        } catch (e: Exception) {
            logger.error(TAG, "Failed to save scorecard to database", e)
            Result.failure(e)
        }
    }
}