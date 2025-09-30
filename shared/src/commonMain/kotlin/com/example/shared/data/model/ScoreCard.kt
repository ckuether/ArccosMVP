package com.example.shared.data.model

import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class ScoreCard(
    val roundId: Long = Random.nextLong(1000000L, 9999999L),
    val player: Player,
    val holeParMap: Map<Int, Int> = mapOf(),
    val scorecard: Map<Int, Int?> = mapOf()
)