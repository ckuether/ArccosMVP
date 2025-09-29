package com.example.shared.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Hole(
    val id: Int,
    val startLocation: Location,
    val endLocation: Location,
    val par: Int
)