package com.example.shared.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shared.data.model.Location
import com.example.shared.platform.getCurrentTimeMillis

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey
    val timestamp: Long = getCurrentTimeMillis(),
    val roundId: Long,
    val lat: Double,
    val long: Double
)

fun LocationEntity.toLocation(): Location {
    return Location(
        lat = lat,
        long = long
    )
}

fun Location.toEntity(roundId: Long): LocationEntity {
    return LocationEntity(
        roundId = roundId,
        lat = lat,
        long = long
    )
}