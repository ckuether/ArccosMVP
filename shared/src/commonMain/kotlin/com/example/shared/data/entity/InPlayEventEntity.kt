package com.example.shared.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shared.event.InPlayEvent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi

@Entity(tableName = "in_play_events")
data class InPlayEventEntity(
    @PrimaryKey
    val eventId: String,
    val timestamp: Long,
    val eventType: String, // "LocationUpdated" or "ShotTracked"
    val json: String // Serialized event data
) {
    @OptIn(ExperimentalUuidApi::class)
    fun toInPlayEvent(): InPlayEvent {
        return when (eventType) {
            "LocationUpdated" -> Json.decodeFromString<InPlayEvent.LocationUpdated>(json)
            "ShotTracked" -> Json.decodeFromString<InPlayEvent.ShotTracked>(json)
            else -> throw IllegalArgumentException("Unknown event type: $eventType")
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
fun InPlayEvent.toEntity(): InPlayEventEntity {
    return InPlayEventEntity(
        eventId = eventID.toString(),
        timestamp = timestamp,
        eventType = when (this) {
            is InPlayEvent.LocationUpdated -> "LocationUpdated"
            is InPlayEvent.ShotTracked -> "ShotTracked"
        },
        json = Json.encodeToString(this)
    )
}