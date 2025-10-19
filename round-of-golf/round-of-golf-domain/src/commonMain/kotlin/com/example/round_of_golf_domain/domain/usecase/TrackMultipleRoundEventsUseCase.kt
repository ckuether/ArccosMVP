package com.example.round_of_golf_domain.domain.usecase

import com.example.shared.data.model.event.RoundOfGolfEvent
import com.example.shared.data.repository.RoundOfGolfEventRepository

/**
 * Use case for tracking multiple round of golf events
 * Single Responsibility: Store multiple events to the event repository in a batch operation
 */
class TrackMultipleRoundEventsUseCase(
    private val eventRepository: RoundOfGolfEventRepository
) {
    
    /**
     * Track multiple events at once
     */
    suspend fun execute(
        events: List<RoundOfGolfEvent>,
        roundId: Long,
        playerId: Long,
        holeNumber: Int? = null
    ) {
        eventRepository.storeEvents(
            events = events,
            roundId = roundId,
            playerId = playerId,
            holeNumber = holeNumber
        )
    }
}