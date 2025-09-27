package com.example.shared.data.event

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
sealed class InPlayEvent @OptIn(ExperimentalUuidApi::class) constructor(
    val eventID: Uuid,
    val timestamp: Long,
){
    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    @Serializable
    @SerialName("location_updated")
    data class LocationUpdated(
        val location: Location
    ): InPlayEvent(timestamp = Clock.System.now().toEpochMilliseconds(), eventID = Uuid.random())

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    @Serializable
    @SerialName("shot_tracked")
    data class ShotTracked(
        val holeNumber: Int
    ): InPlayEvent(timestamp = Clock.System.now().toEpochMilliseconds(), eventID = Uuid.random())
}

@Serializable
data class Location(
    val lat: Double,
    val long: Double
)