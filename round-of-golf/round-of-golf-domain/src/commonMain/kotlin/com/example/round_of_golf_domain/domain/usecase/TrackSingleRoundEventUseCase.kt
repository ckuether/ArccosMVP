package com.example.round_of_golf_domain.domain.usecase

import com.example.shared.data.model.event.RoundOfGolfEvent
import com.example.shared.data.repository.RoundOfGolfEventRepository

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
        holeNumber: Int? = null
    ) {
        eventRepository.storeEvent(
            event = event,
            roundId = roundId,
            playerId = playerId,
            holeNumber = holeNumber
        )
    }
}