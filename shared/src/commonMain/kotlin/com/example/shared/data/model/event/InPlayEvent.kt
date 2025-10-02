package com.example.shared.data.model.event

import com.example.shared.data.model.Location
import com.example.shared.platform.getCurrentTimeMillis
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
sealed class InPlayEvent @OptIn(ExperimentalUuidApi::class) constructor(
    val eventID: Uuid = Uuid.random(),
    val timestamp: Long = getCurrentTimeMillis(),
){
    @Serializable
    @SerialName("location_updated")
    data class LocationUpdated(
        val location: Location
    ): InPlayEvent()

    @Serializable
    @SerialName("shot_tracked")
    data class ShotTracked(
        val holeNumber: Int
    ): InPlayEvent()
}