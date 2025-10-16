package com.example.shared.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.shared.data.model.event.RoundOfGolfEvent
import com.example.shared.platform.getCurrentTimeMillis
import kotlinx.serialization.json.Json

object EventType {
    const val LOCATION_UPDATED = "LocationUpdated"
    const val SHOT_TRACKED = "ShotTracked"
    const val NEXT_HOLE = "NextHole"
    const val PREVIOUS_HOLE = "PreviousHole"
    const val FINISH_ROUND = "FinishRound"
}

@Entity(
    tableName = "round_of_golf_events",
    indices = [
        Index(value = ["roundId", "timestamp"]),
        Index(value = ["roundId", "eventType"]),
        Index(value = ["roundId", "holeNumber"])
    ]
)
data class RoundOfGolfEventEntity(
    @PrimaryKey 
    val id: Long,
    val roundId: Long,
    val eventType: String, // See EventType constants
    val timestamp: Long = getCurrentTimeMillis(),
    val eventData: String, // JSON serialized event data
    val holeNumber: Int? = null, // For quick filtering by hole
    val playerId: Long
)

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

fun RoundOfGolfEvent.toEntity(
    id: Long,
    roundId: Long,
    playerId: Long,
    holeNumber: Int? = null
): RoundOfGolfEventEntity {
    val eventType = when (this) {
        is RoundOfGolfEvent.LocationUpdated -> EventType.LOCATION_UPDATED
        is RoundOfGolfEvent.ShotTracked -> EventType.SHOT_TRACKED
        is RoundOfGolfEvent.NextHole -> EventType.NEXT_HOLE
        is RoundOfGolfEvent.PreviousHole -> EventType.PREVIOUS_HOLE
        is RoundOfGolfEvent.FinishRound -> EventType.FINISH_ROUND
    }
    
    val eventData = when (this) {
        is RoundOfGolfEvent.LocationUpdated -> Json.encodeToString(this)
        is RoundOfGolfEvent.ShotTracked -> Json.encodeToString(this)
        is RoundOfGolfEvent.NextHole -> Json.encodeToString(this)
        is RoundOfGolfEvent.PreviousHole -> Json.encodeToString(this)
        is RoundOfGolfEvent.FinishRound -> Json.encodeToString(this)
    }
    
    val timestamp = this.timestamp
    
    return RoundOfGolfEventEntity(
        id = id,
        roundId = roundId,
        eventType = eventType,
        timestamp = timestamp,
        eventData = eventData,
        holeNumber = holeNumber,
        playerId = playerId
    )
}