package com.example.round_of_golf_domain.data.model

import com.example.round_of_golf_domain.data.entity.RoundOfGolfEventEntity
import com.example.shared.data.model.Location
import com.example.shared.platform.getCurrentTimeMillis
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
sealed interface RoundOfGolfEvent {
    val timestamp: Long

    val eventType
        get() = when (this) {
            is LocationUpdated -> EventType.LOCATION_UPDATED
            is ShotTracked -> EventType.SHOT_TRACKED
            is NextHole -> EventType.NEXT_HOLE
            is PreviousHole -> EventType.PREVIOUS_HOLE
            is FinishRound -> EventType.FINISH_ROUND
        }

    @Serializable
    data class LocationUpdated(
        override val timestamp: Long = getCurrentTimeMillis(),
        val location: Location,
    ): RoundOfGolfEvent

    @Serializable
    data class ShotTracked(
        override val timestamp: Long = getCurrentTimeMillis(),
        val holeNumber: Int,
        //TODO: Add Club
//        val club: Club
//        val location: Location
    ): RoundOfGolfEvent

    @Serializable
    data class NextHole(
        override val timestamp: Long = getCurrentTimeMillis()
    ): RoundOfGolfEvent

    @Serializable
    data class PreviousHole(
        override val timestamp: Long = getCurrentTimeMillis()
    ): RoundOfGolfEvent

    @Serializable
    data class FinishRound(
        override val timestamp: Long = getCurrentTimeMillis()
    ): RoundOfGolfEvent
}

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

object EventType {
    const val LOCATION_UPDATED = "LocationUpdated"
    const val SHOT_TRACKED = "ShotTracked"
    const val NEXT_HOLE = "NextHole"
    const val PREVIOUS_HOLE = "PreviousHole"
    const val FINISH_ROUND = "FinishRound"
}