package com.example.shared.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.shared.data.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    
    @Query("SELECT * FROM locations ORDER BY timestamp DESC")
    fun getAllLocations(): Flow<List<LocationEntity>>
    
    @Query("SELECT * FROM locations WHERE id = :id")
    suspend fun getLocationById(id: Long): LocationEntity?
    
    @Query("SELECT * FROM locations WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getLocationsByTimeRange(startTime: Long, endTime: Long): Flow<List<LocationEntity>>
    
    @Query("SELECT * FROM locations ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentLocations(limit: Int): Flow<List<LocationEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocations(locations: List<LocationEntity>)
    
    @Update
    suspend fun updateLocation(location: LocationEntity)
    
    @Delete
    suspend fun deleteLocation(location: LocationEntity)
    
    @Query("DELETE FROM locations")
    suspend fun deleteAllLocations()
    
    @Query("DELETE FROM locations WHERE timestamp < :beforeTime")
    suspend fun deleteLocationsOlderThan(beforeTime: Long)
    
    @Query("SELECT COUNT(*) FROM locations")
    suspend fun getLocationCount(): Int
    
    @Query("SELECT * FROM locations ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastLocation(): LocationEntity?
}