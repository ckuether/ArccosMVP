package com.example.round_of_golf_domain.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.round_of_golf_domain.data.model.EventType
import com.example.round_of_golf_domain.data.model.RoundOfGolfEvent
import com.example.shared.platform.getCurrentTimeMillis
import kotlinx.serialization.json.Json

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
    val timestamp: Long = getCurrentTimeMillis(),
    val roundId: Long,
    val eventType: String, // See EventType constants
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