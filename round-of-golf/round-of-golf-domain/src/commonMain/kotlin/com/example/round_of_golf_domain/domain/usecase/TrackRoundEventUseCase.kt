package com.example.round_of_golf_domain.domain.usecase

import com.example.shared.data.model.event.RoundOfGolfEvent
import com.example.shared.data.repository.RoundOfGolfEventRepository

/**
 * Use case for tracking/storing round of golf events
 * Single Responsibility: Store events to the event repository
 */
class TrackRoundEventUseCase(
    private val eventRepository: RoundOfGolfEventRepository
) {
    
    /**
     * Track a single event during a round
     */
    suspend fun trackEvent(
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
    
    /**
     * Track multiple events at once
     */
    suspend fun trackEvents(
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
    
    /**
     * Delete all events for a round
     */
    suspend fun deleteRoundEvents(roundId: Long) {
        eventRepository.deleteEventsForRound(roundId)
    }
}