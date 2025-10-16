package com.example.shared.data.repository

import com.example.shared.data.model.event.RoundOfGolfEvent
import kotlinx.coroutines.flow.Flow

interface RoundOfGolfEventRepository {
    
    /**
     * Store a single event for a round
     */
    suspend fun storeEvent(
        event: RoundOfGolfEvent,
        roundId: Long,
        playerId: Long,
        holeNumber: Int? = null
    )
    
    /**
     * Store multiple events for a round
     */
    suspend fun storeEvents(
        events: List<RoundOfGolfEvent>,
        roundId: Long,
        playerId: Long,
        holeNumber: Int? = null
    )
    
    /**
     * Get all events for a round in chronological order
     * Essential for round replay functionality
     */
    fun getEventsForRound(roundId: Long): Flow<List<RoundOfGolfEvent>>
    
    /**
     * Get events for a specific hole
     */
    fun getEventsForHole(roundId: Long, holeNumber: Int): Flow<List<RoundOfGolfEvent>>
    
    /**
     * Get events of a specific type (e.g., only ShotTracked events)
     */
    fun getEventsByType(roundId: Long, eventType: String): Flow<List<RoundOfGolfEvent>>
    
    /**
     * Get events within a time range
     */
    fun getEventsByTimeRange(
        roundId: Long, 
        startTime: Long, 
        endTime: Long
    ): Flow<List<RoundOfGolfEvent>>
    
    /**
     * Get all events for a round as a single operation (for replay)
     */
    suspend fun getEventsForRoundSnapshot(roundId: Long): List<RoundOfGolfEvent>
    
    /**
     * Delete all events for a round
     */
    suspend fun deleteEventsForRound(roundId: Long)
    
    /**
     * Get count of events for a round
     */
    suspend fun getEventCountForRound(roundId: Long): Int
    
    /**
     * Get all round IDs that have events
     */
    fun getAllRoundIds(): Flow<List<Long>>
}