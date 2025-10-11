package com.example.shared.data.model

import kotlinx.serialization.Serializable
import kotlin.math.*

@Serializable
data class Location(
    val lat: Double,
    val long: Double
)

fun Location.distanceToInYards(other: Location): Int {
    val earthRadiusMeters = 6371000.0
    val lat1Rad = this.lat * PI / 180.0
    val lat2Rad = other.lat * PI / 180.0
    val deltaLatRad = (other.lat - this.lat) * PI / 180.0
    val deltaLonRad = (other.long - this.long) * PI / 180.0
    
    val a = sin(deltaLatRad / 2).pow(2) + 
            cos(lat1Rad) * cos(lat2Rad) * sin(deltaLonRad / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    
    val distanceMeters = earthRadiusMeters * c
    return (distanceMeters * 1.09361).toInt() // Convert meters to yards
}

fun Location.midPoint(location: Location): Location {
    val midLat = (this.lat + location.lat) / 2
    val midLng = (this.long + location.long) / 2
    return Location(midLat, midLng)
}