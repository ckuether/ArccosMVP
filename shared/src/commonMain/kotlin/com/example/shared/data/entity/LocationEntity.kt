package com.example.shared.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shared.data.model.Location

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
) {
    fun toLocation(): Location {
        return Location(
            lat = latitude,
            long = longitude
        )
    }
}

fun Location.toEntity(timestamp: Long): LocationEntity {
    return LocationEntity(
        latitude = lat,
        longitude = long,
        timestamp = timestamp
    )
}