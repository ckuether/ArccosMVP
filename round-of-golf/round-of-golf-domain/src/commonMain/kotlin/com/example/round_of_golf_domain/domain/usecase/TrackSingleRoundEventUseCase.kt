package com.example.round_of_golf_domain.domain.usecase

import com.example.round_of_golf_domain.data.model.RoundOfGolfEvent
import com.example.round_of_golf_domain.data.repository.RoundOfGolfEventRepository

/**
 * Use case for tracking a single round of golf event
 * Single Responsibility: Store one event to the event repository
 */
class TrackSingleRoundEventUseCase(
    private val eventRepository: RoundOfGolfEventRepository
) {
    
    /**
     * Track a single event during a round
     */
    suspend fun execute(
        event: RoundOfGolfEvent,
        roundId: Long,
        playerId: Long,
        holeNumber: Int?,
    ) {
        eventRepository.insertEvent(
            event = event,
            roundId = roundId,
            playerId = playerId,
            holeNumber = holeNumber
        )
    }
}