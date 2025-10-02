package com.example.shared.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.shared.data.entity.InPlayEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InPlayEventDao {
    
    @Query("SELECT * FROM in_play_events ORDER BY timestamp DESC")
    fun getAllEvents(): Flow<List<InPlayEventEntity>>
    
    @Query("SELECT * FROM in_play_events WHERE eventType = :eventType ORDER BY timestamp DESC")
    fun getEventsByType(eventType: String): Flow<List<InPlayEventEntity>>
    
    @Query("SELECT * FROM in_play_events WHERE roundID = :roundID ORDER BY timestamp DESC")
    fun getEventsByRound(roundID: Long): Flow<List<InPlayEventEntity>>
    
    @Query("SELECT * FROM in_play_events WHERE roundID = :roundID AND eventType = :eventType ORDER BY timestamp DESC")
    fun getEventsByRoundAndType(roundID: Long, eventType: String): Flow<List<InPlayEventEntity>>
    
    @Query("SELECT * FROM in_play_events WHERE eventId = :eventId")
    suspend fun getEventById(eventId: String): InPlayEventEntity?
    
    @Query("SELECT * FROM in_play_events WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getEventsByTimeRange(startTime: Long, endTime: Long): Flow<List<InPlayEventEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: InPlayEventEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<InPlayEventEntity>)
    
    @Update
    suspend fun updateEvent(event: InPlayEventEntity)
    
    @Delete
    suspend fun deleteEvent(event: InPlayEventEntity)
    
    @Query("DELETE FROM in_play_events")
    suspend fun deleteAllEvents()
    
    @Query("DELETE FROM in_play_events WHERE eventType = :eventType")
    suspend fun deleteEventsByType(eventType: String)
    
    @Query("SELECT COUNT(*) FROM in_play_events")
    suspend fun getEventCount(): Int
}