package com.example.shared.usecase

import com.example.shared.data.model.event.RoundOfGolfEvent
import com.example.shared.data.repository.RoundOfGolfEventRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Use case for replaying a round of golf based on stored events
 * Provides real-time replay functionality with configurable speed
 */
class ReplayRoundOfGolfUseCase(
    private val eventRepository: RoundOfGolfEventRepository
) {
    
    /**
     * Replay a round at normal speed (real-time)
     */
    fun replayRound(roundId: Long): Flow<RoundOfGolfEvent> = flow {
        val events = eventRepository.getEventsForRoundSnapshot(roundId)
        
        if (events.isEmpty()) return@flow
        
        val startTime = events.first().timestamp
        
        for (event in events) {
            val eventTime = event.timestamp
            
            // Calculate delay based on original timing
            val delayTime = eventTime - startTime
            if (delayTime > 0) {
                delay(delayTime)
            }
            
            emit(event)
        }
    }
    
    /**
     * Replay a round at accelerated speed
     * @param speedMultiplier - 2.0 = 2x speed, 0.5 = half speed
     */
    fun replayRoundWithSpeed(roundId: Long, speedMultiplier: Double = 1.0): Flow<RoundOfGolfEvent> = flow {
        val events = eventRepository.getEventsForRoundSnapshot(roundId)
        
        if (events.isEmpty()) return@flow
        
        var lastTimestamp = 0L
        
        for (event in events) {
            val currentTimestamp = event.timestamp
            
            // Calculate delay between events
            if (lastTimestamp > 0) {
                val originalDelay = currentTimestamp - lastTimestamp
                val adjustedDelay = (originalDelay / speedMultiplier).toLong()
                if (adjustedDelay > 0) {
                    delay(adjustedDelay)
                }
            }
            
            lastTimestamp = currentTimestamp
            emit(event)
        }
    }
    
    /**
     * Get all events for a round without replay timing
     */
    suspend fun getAllRoundEvents(roundId: Long): List<RoundOfGolfEvent> {
        return eventRepository.getEventsForRoundSnapshot(roundId)
    }
    
    /**
     * Replay only hole navigation events
     */
    fun replayHoleNavigation(roundId: Long): Flow<RoundOfGolfEvent> = flow {
        val events = eventRepository.getEventsForRoundSnapshot(roundId)
        
        events.filter { event ->
            event is RoundOfGolfEvent.NextHole || 
            event is RoundOfGolfEvent.PreviousHole ||
            event is RoundOfGolfEvent.FinishRound
        }.forEach { event ->
            emit(event)
            delay(500) // Small delay between hole changes for visualization
        }
    }
    
    /**
     * Replay only shot tracking events
     */
    fun replayShotTracking(roundId: Long): Flow<RoundOfGolfEvent> = flow {
        val events = eventRepository.getEventsForRoundSnapshot(roundId)
        
        events.filterIsInstance<RoundOfGolfEvent.ShotTracked>().forEach { event ->
            emit(event)
            delay(1000) // 1 second between shots for visualization
        }
    }
    
    /**
     * Get round summary for replay
     */
    suspend fun getRoundReplaySummary(roundId: Long): RoundReplaySummary {
        val events = eventRepository.getEventsForRoundSnapshot(roundId)
        
        val startTime = events.firstOrNull()?.timestamp ?: 0L
        val endTime = events.lastOrNull()?.timestamp ?: 0L
        
        val shotEvents = events.filterIsInstance<RoundOfGolfEvent.ShotTracked>()
        val holeChanges = events.count { 
            it is RoundOfGolfEvent.NextHole || it is RoundOfGolfEvent.PreviousHole 
        }
        
        return RoundReplaySummary(
            roundId = roundId,
            totalEvents = events.size,
            shotCount = shotEvents.size,
            duration = endTime - startTime,
            startTime = startTime,
            endTime = endTime,
            holeChanges = holeChanges,
            isCompleted = events.any { it is RoundOfGolfEvent.FinishRound }
        )
    }
}

/**
 * Summary data for round replay
 */
data class RoundReplaySummary(
    val roundId: Long,
    val totalEvents: Int,
    val shotCount: Int,
    val duration: Long, // milliseconds
    val startTime: Long,
    val endTime: Long,
    val holeChanges: Int,
    val isCompleted: Boolean
)