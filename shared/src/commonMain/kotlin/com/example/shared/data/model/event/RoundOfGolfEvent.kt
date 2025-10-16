package com.example.shared.data.model.event

import com.example.shared.data.model.Location
import com.example.shared.platform.getCurrentTimeMillis
import kotlinx.serialization.Serializable

@Serializable
sealed interface RoundOfGolfEvent {
    val timestamp: Long

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