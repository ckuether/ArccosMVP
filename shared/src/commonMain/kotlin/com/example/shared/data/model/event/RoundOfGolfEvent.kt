package com.example.shared.data.model.event

import com.example.shared.data.model.Location
import com.example.shared.platform.getCurrentTimeMillis
import kotlinx.serialization.Serializable

@Serializable
sealed interface RoundOfGolfEvent {

    @Serializable
    data class LocationUpdated(
        val location: Location,
        val timestamp: Long = getCurrentTimeMillis()
    ): RoundOfGolfEvent

    @Serializable
    data class ShotTracked(
        val holeNumber: Int
        //TODO: Add Club
//        val club: Club
//        val location: Location
    ): RoundOfGolfEvent

    object NextHole: RoundOfGolfEvent

    object PreviousHole: RoundOfGolfEvent

    object FinishRound: RoundOfGolfEvent

}