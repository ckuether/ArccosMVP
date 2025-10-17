package com.example.round_of_golf_domain.domain.usecase

import com.example.shared.data.model.event.RoundOfGolfEvent
import com.example.shared.data.repository.RoundOfGolfEventRepository

/**
 * Use case for calculating round statistics
 * Single Responsibility: Analyze events and compute round statistics
 */
class GetRoundStatisticsUseCase(
    private val eventRepository: RoundOfGolfEventRepository
) {
    
    /**
     * Get comprehensive round statistics
     */
    suspend fun getRoundStatistics(roundId: Long): RoundStatistics {
        val eventCount = eventRepository.getEventCountForRound(roundId)
        val allEvents = eventRepository.getEventsForRoundSnapshot(roundId)
        
        val shotCount = allEvents.count { it is RoundOfGolfEvent.ShotTracked }
        val locationUpdates = allEvents.count { it is RoundOfGolfEvent.LocationUpdated }
        val holeChanges = allEvents.count { 
            it is RoundOfGolfEvent.NextHole || it is RoundOfGolfEvent.PreviousHole 
        }
        
        return RoundStatistics(
            totalEvents = eventCount,
            shotCount = shotCount,
            locationUpdates = locationUpdates,
            holeChanges = holeChanges,
            isCompleted = allEvents.any { it is RoundOfGolfEvent.FinishRound }
        )
    }
}

/**
 * Data class for round statistics
 */
data class RoundStatistics(
    val totalEvents: Int,
    val shotCount: Int,
    val locationUpdates: Int,
    val holeChanges: Int,
    val isCompleted: Boolean
)