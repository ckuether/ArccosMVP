package com.example.shared.data.model

/**
 * Platform-agnostic camera position data.
 */
data class MapCameraPosition(
    val centerLat: Double,
    val centerLng: Double,
    val zoom: Float,
    val bearing: Float
)