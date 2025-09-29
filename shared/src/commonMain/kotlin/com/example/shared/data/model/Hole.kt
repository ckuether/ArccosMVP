package com.example.shared.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Hole(
    val id: Int,
    val teeLocation: Location,
    val flagLocation: Location,
    val par: Int
)