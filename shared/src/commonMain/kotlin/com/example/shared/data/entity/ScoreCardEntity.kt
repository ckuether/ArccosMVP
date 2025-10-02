package com.example.shared.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shared.data.model.ScoreCard
import kotlinx.serialization.json.Json

@Entity(tableName = "score_cards")
data class ScoreCardEntity(
    @PrimaryKey
    val roundId: Long,
    val courseId: Long,
    val playerId: Long,
    val scorecardJson: String, // Serialized scorecard map
    val roundInProgress: Boolean = true,
    val createdTimestamp: Long,
    val lastUpdatedTimestamp: Long
)

fun ScoreCardEntity.toScoreCard(): ScoreCard {
    val scorecardMap = Json.decodeFromString<Map<Int, Int?>>(scorecardJson)
    return ScoreCard(
        roundId = roundId,
        courseId = courseId,
        playerId = playerId,
        scorecard = scorecardMap,
        roundInProgress = roundInProgress,
        createdTimestamp = createdTimestamp,
        lastUpdatedTimestamp = lastUpdatedTimestamp
    )
}

fun ScoreCard.toEntity(): ScoreCardEntity {
    return ScoreCardEntity(
        roundId = roundId,
        courseId = courseId,
        playerId = playerId,
        scorecardJson = Json.encodeToString(scorecard),
        roundInProgress = roundInProgress,
        createdTimestamp = createdTimestamp,
        lastUpdatedTimestamp = lastUpdatedTimestamp
    )
}