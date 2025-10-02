package com.example.shared.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shared.data.entity.ScoreCardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreCardDao {
    
    @Query("SELECT * FROM score_cards ORDER BY roundId DESC")
    fun getAllScoreCards(): Flow<List<ScoreCardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScoreCard(scoreCard: ScoreCardEntity)
    
    @Delete
    suspend fun deleteScoreCard(scoreCard: ScoreCardEntity)
}