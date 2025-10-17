package com.example.round_of_golf_domain.domain.usecase

import com.example.shared.data.entity.EventType
import com.example.shared.data.model.event.RoundOfGolfEvent
import com.example.shared.data.repository.RoundOfGolfEventRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving round of golf events
 * Single Responsibility: Query and filter events from the repository
 */
class GetRoundEventsUseCase(
    private val eventRepository: RoundOfGolfEventRepository
) {
    
    /**
     * Get all events for round replay
     */
    fun getEventsForReplay(roundId: Long): Flow<List<RoundOfGolfEvent>> {
        return eventRepository.getEventsForRound(roundId)
    }
    
    /**
     * Get events for a specific hole
     */
    fun getHoleEvents(roundId: Long, holeNumber: Int): Flow<List<RoundOfGolfEvent>> {
        return eventRepository.getEventsForHole(roundId, holeNumber)
    }
    
    /**
     * Get shot tracking events only
     */
    fun getShotEvents(roundId: Long): Flow<List<RoundOfGolfEvent>> {
        return eventRepository.getEventsByType(roundId, EventType.SHOT_TRACKED)
    }
    
    /**
     * Get location tracking events only
     */
    fun getLocationEvents(roundId: Long): Flow<List<RoundOfGolfEvent>> {
        return eventRepository.getEventsByType(roundId, EventType.LOCATION_UPDATED)
    }

    /**
     * Get events within a time window
     */
    fun getEventsInTimeRange(
        roundId: Long,
        startTime: Long,
        endTime: Long
    ): Flow<List<RoundOfGolfEvent>> {
        return eventRepository.getEventsByTimeRange(roundId, startTime, endTime)
    }
}