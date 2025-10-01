package com.example.shared.data.model

import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class ScoreCard(
    val roundId: Long = Random.nextLong(1000000L, 9999999L),
    val courseId: Long,
    val playerId: Long,
    val scorecard: Map<Int, Int?> = mapOf()
)