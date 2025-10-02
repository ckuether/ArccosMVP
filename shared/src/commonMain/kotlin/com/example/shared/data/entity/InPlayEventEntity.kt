package com.example.shared.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shared.data.model.event.InPlayEvent
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi

@Entity(tableName = "in_play_events")
data class InPlayEventEntity(
    @PrimaryKey
    val eventId: String,
    val timestamp: Long,
    val roundID: Long,
    val eventType: String, // "LocationUpdated" or "ShotTracked"
    val json: String // Serialized event data
)

@OptIn(ExperimentalUuidApi::class)
fun InPlayEventEntity.toInPlayEvent(): InPlayEvent {
    return when (eventType) {
        "LocationUpdated" -> Json.decodeFromString<InPlayEvent.LocationUpdated>(json)
        "ShotTracked" -> Json.decodeFromString<InPlayEvent.ShotTracked>(json)
        else -> throw IllegalArgumentException("Unknown event type: $eventType")
    }
}

@OptIn(ExperimentalUuidApi::class)
fun InPlayEvent.LocationUpdated.toEntity(roundID: Long): InPlayEventEntity {
    return InPlayEventEntity(
        eventId = eventID.toString(),
        timestamp = timestamp,
        roundID = roundID,
        eventType = "LocationUpdated",
        json = Json.encodeToString(this)
    )
}

@OptIn(ExperimentalUuidApi::class)
fun InPlayEvent.ShotTracked.toEntity(roundID: Long): InPlayEventEntity {
    return InPlayEventEntity(
        eventId = eventID.toString(),
        timestamp = timestamp,
        roundID = roundID,
        eventType = "ShotTracked", 
        json = Json.encodeToString(this)
    )
}