package com.example.round_of_golf_domain.domain.usecase

import com.example.round_of_golf_domain.data.repository.RoundOfGolfEventRepository

/**
 * Use case for deleting all events for a round of golf
 * Single Responsibility: Delete events from the event repository for a specific round
 */
class DeleteRoundEventsUseCase(
    private val eventRepository: RoundOfGolfEventRepository
) {
    
    /**
     * Delete all events for a round
     */
    suspend fun execute(roundId: Long) {
        eventRepository.deleteEventsForRound(roundId)
    }
}