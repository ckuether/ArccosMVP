package com.example.shared.data.repository

import com.example.shared.data.dao.RoundOfGolfEventDao
import com.example.shared.data.entity.toEntity
import com.example.shared.data.entity.toEvent
import com.example.shared.data.model.event.RoundOfGolfEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class RoundOfGolfEventRepositoryImpl(
    private val eventDao: RoundOfGolfEventDao
) : RoundOfGolfEventRepository {
    
    override suspend fun storeEvent(
        event: RoundOfGolfEvent,
        roundId: Long,
        playerId: Long,
        holeNumber: Int?
    ) {
        val eventEntity = event.toEntity(
            id = UUID.randomUUID().toString(),
            roundId = roundId,
            playerId = playerId,
            holeNumber = holeNumber
        )
        eventDao.insertEvent(eventEntity)
    }
    
    override suspend fun storeEvents(
        events: List<RoundOfGolfEvent>,
        roundId: Long,
        playerId: Long,
        holeNumber: Int?
    ) {
        val eventEntities = events.map { event ->
            event.toEntity(
                id = UUID.randomUUID().toString(),
                roundId = roundId,
                playerId = playerId,
                holeNumber = holeNumber
            )
        }
        eventDao.insertEvents(eventEntities)
    }
    
    override fun getEventsForRound(roundId: Long): Flow<List<RoundOfGolfEvent>> {
        return eventDao.getEventsForRound(roundId).map { entities ->
            entities.map { it.toEvent() }
        }
    }
    
    override fun getEventsForHole(roundId: Long, holeNumber: Int): Flow<List<RoundOfGolfEvent>> {
        return eventDao.getEventsForHole(roundId, holeNumber).map { entities ->
            entities.map { it.toEvent() }
        }
    }
    
    override fun getEventsByType(roundId: Long, eventType: String): Flow<List<RoundOfGolfEvent>> {
        return eventDao.getEventsByType(roundId, eventType).map { entities ->
            entities.map { it.toEvent() }
        }
    }
    
    override fun getEventsByTimeRange(
        roundId: Long,
        startTime: Long,
        endTime: Long
    ): Flow<List<RoundOfGolfEvent>> {
        return eventDao.getEventsByTimeRange(roundId, startTime, endTime).map { entities ->
            entities.map { it.toEvent() }
        }
    }
    
    override suspend fun getEventsForRoundSnapshot(roundId: Long): List<RoundOfGolfEvent> {
        return eventDao.getEventsForRoundSuspend(roundId).map { it.toEvent() }
    }
    
    override suspend fun deleteEventsForRound(roundId: Long) {
        eventDao.deleteEventsForRound(roundId)
    }
    
    override suspend fun getEventCountForRound(roundId: Long): Int {
        return eventDao.getEventCountForRound(roundId)
    }
    
    override fun getAllRoundIds(): Flow<List<Long>> {
        return eventDao.getAllRoundIds()
    }
}