package com.example.shared.event

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


sealed class InPlayEvent @OptIn(ExperimentalUuidApi::class) constructor(
    timestamp: Long,
    val eventID: Uuid
){
    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    data class LocationUpdated(
        val location: Location
    ): InPlayEvent(timestamp = Clock.System.now().toEpochMilliseconds(), eventID = Uuid.random())

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    data class ShotTracked(
        val holeNumber: Int
    ): InPlayEvent(timestamp = Clock.System.now().toEpochMilliseconds(), eventID = Uuid.random())
}

data class Location(
    val lat: Double,
    val long: Double
)