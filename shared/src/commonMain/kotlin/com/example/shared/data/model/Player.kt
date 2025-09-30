package com.example.shared.data.model

import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class Player(
    val id: Long = Random.nextLong(1000000L, 9999999L),
    val name: String,
)