package com.example.location_domain.domain.service

import com.example.location_domain.domain.model.ScreenPoint
import com.example.shared.data.model.Location

/**
 * Domain service interface for map coordinate projection operations.
 * This abstraction allows the domain layer to work with map projections
 * without depending on specific map implementations (Google Maps, Apple Maps, etc.)
 */
interface MapProjectionService {
    
    /**
     * Converts screen coordinates to geographic coordinates
     * @param screenX X coordinate on screen
     * @param screenY Y coordinate on screen
     * @param mapInstance Platform-specific map instance
     * @return Geographic location or null if conversion fails
     */
    fun screenToMapCoordinates(
        screenX: Int, 
        screenY: Int, 
        mapInstance: Any
    ): Location?
    
    /**
     * Converts geographic coordinates to screen coordinates
     * @param location Geographic location
     * @param mapInstance Platform-specific map instance
     * @return Screen point or null if conversion fails
     */
    fun mapToScreenCoordinates(
        location: Location, 
        mapInstance: Any
    ): ScreenPoint?
}