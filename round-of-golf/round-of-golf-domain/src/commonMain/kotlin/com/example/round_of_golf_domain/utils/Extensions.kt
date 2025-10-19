package com.example.round_of_golf_domain.utils

import com.example.round_of_golf_domain.data.entity.RoundOfGolfEventEntity
import com.example.shared.data.model.EventType
import com.example.shared.data.model.RoundOfGolfEvent
import kotlinx.serialization.json.Json

fun RoundOfGolfEvent.toEntity(
    roundId: Long,
    playerId: Long,
    holeNumber: Int? = null
): RoundOfGolfEventEntity {
    val eventData = Json.encodeToString(this)
    
    return RoundOfGolfEventEntity(
        timestamp = this.timestamp,
        roundId = roundId,
        eventType = eventType,
        eventData = eventData,
        holeNumber = holeNumber,
        playerId = playerId
    )
}

// Extension functions for conversion
fun RoundOfGolfEventEntity.toEvent(): RoundOfGolfEvent {
    return when (eventType) {
        EventType.LOCATION_UPDATED -> Json.decodeFromString<RoundOfGolfEvent.LocationUpdated>(eventData)
        EventType.SHOT_TRACKED -> Json.decodeFromString<RoundOfGolfEvent.ShotTracked>(eventData)
        EventType.NEXT_HOLE -> Json.decodeFromString<RoundOfGolfEvent.NextHole>(eventData)
        EventType.PREVIOUS_HOLE -> Json.decodeFromString<RoundOfGolfEvent.PreviousHole>(eventData)
        EventType.FINISH_ROUND -> Json.decodeFromString<RoundOfGolfEvent.FinishRound>(eventData)
        else -> throw IllegalArgumentException("Unknown event type: $eventType")
    }
}