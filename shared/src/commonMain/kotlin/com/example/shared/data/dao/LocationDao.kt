package com.example.shared.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shared.data.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity)
    
    @Query("SELECT * FROM locations WHERE roundId = :roundId ORDER BY timestamp ASC")
    fun getLocationsForRound(roundId: Long): Flow<List<LocationEntity>>
    
    @Query("SELECT * FROM locations ORDER BY timestamp ASC")
    fun getAllLocations(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM locations ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestLocation(): LocationEntity?
    
    @Query("DELETE FROM locations")
    suspend fun deleteAllLocations()
}