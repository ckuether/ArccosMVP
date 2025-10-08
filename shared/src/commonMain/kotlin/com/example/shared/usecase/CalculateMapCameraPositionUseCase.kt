package com.example.shared.usecase

import com.example.shared.data.model.Location
import com.example.shared.data.model.Hole
import com.example.shared.domain.usecase.CalculateBearingUseCase

/**
 * Use case for calculating map camera position based on golf hole data.
 * Returns platform-agnostic camera positioning data that can be used by platform-specific map implementations.
 */
class CalculateMapCameraPositionUseCase(
    private val calculateBearingUseCase: CalculateBearingUseCase
) {
    
    /**
     * Calculates the optimal camera position for displaying a golf hole.
     * 
     * @param hole The golf hole containing tee and flag locations
     * @param defaultZoom The default zoom level to use
     * @return CameraPosition containing center coordinates, zoom, and bearing
     */
    operator fun invoke(hole: Hole, defaultZoom: Float = 16.0f): CameraPosition {
        val centerLat = (hole.teeLocation.lat + hole.flagLocation.lat) / 2
        val centerLng = (hole.teeLocation.long + hole.flagLocation.long) / 2
        val bearing = calculateBearingUseCase(hole.teeLocation, hole.flagLocation)
        
        return CameraPosition(
            centerLat = centerLat,
            centerLng = centerLng,
            zoom = defaultZoom,
            bearing = bearing.toFloat()
        )
    }
    
    /**
     * Calculates camera position for two specific locations.
     * 
     * @param startLocation The starting location (e.g., tee)
     * @param endLocation The ending location (e.g., flag)  
     * @param defaultZoom The default zoom level to use
     * @return CameraPosition containing center coordinates, zoom, and bearing
     */
    operator fun invoke(
        startLocation: Location,
        endLocation: Location,
        defaultZoom: Float = 16.0f
    ): CameraPosition {
        val centerLat = (startLocation.lat + endLocation.lat) / 2
        val centerLng = (startLocation.long + endLocation.long) / 2
        val bearing = calculateBearingUseCase(startLocation, endLocation)
        
        return CameraPosition(
            centerLat = centerLat,
            centerLng = centerLng,
            zoom = defaultZoom,
            bearing = bearing.toFloat()
        )
    }
}

/**
 * Platform-agnostic camera position data.
 */
data class CameraPosition(
    val centerLat: Double,
    val centerLng: Double,
    val zoom: Float,
    val bearing: Float
)