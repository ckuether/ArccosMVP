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
    
    @Query("SELECT * FROM score_cards WHERE roundInProgress = :inProgress ORDER BY roundId DESC")
    fun getScoreCardsByStatus(inProgress: Boolean): Flow<List<ScoreCardEntity>>
    
    @Query("SELECT * FROM score_cards WHERE roundInProgress = 0 ORDER BY roundId DESC")
    fun getCompletedRounds(): Flow<List<ScoreCardEntity>>
    
    @Query("SELECT * FROM score_cards WHERE roundInProgress = 1 ORDER BY roundId DESC")
    fun getInProgressRounds(): Flow<List<ScoreCardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScoreCard(scoreCard: ScoreCardEntity)
    
    @Delete
    suspend fun deleteScoreCard(scoreCard: ScoreCardEntity)
}