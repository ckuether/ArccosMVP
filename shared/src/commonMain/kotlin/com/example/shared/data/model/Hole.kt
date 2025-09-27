package com.example.shared.data.model

import com.example.shared.data.event.Location
import kotlinx.serialization.Serializable

@Serializable
data class Hole(
    val id: Int,
    val startLocation: Location,
    val endLocation: Location
)