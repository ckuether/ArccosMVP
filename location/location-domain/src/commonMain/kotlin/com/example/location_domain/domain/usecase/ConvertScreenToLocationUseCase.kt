package com.example.location_domain.domain.usecase

import com.example.location_domain.domain.service.MapProjectionService
import com.example.shared.data.model.Location

/**
 * Use Case for converting screen coordinates to geographic location.
 * Contains business logic for coordinate conversion with validation.
 */
class ConvertScreenToLocationUseCase(
    private val mapProjectionService: MapProjectionService
) {
    
    /**
     * Converts screen coordinates to geographic location with business validation
     * @param screenX Screen X coordinate (must be positive)
     * @param screenY Screen Y coordinate (must be positive)  
     * @param mapInstance Platform-specific map instance
     * @return ConversionResult containing location or error
     */
    fun execute(
        screenX: Int,
        screenY: Int,
        mapInstance: Any
    ): ConversionResult {
        // Business rule: Screen coordinates must be valid
        if (screenX < 0 || screenY < 0) {
            return ConversionResult.Error("Invalid screen coordinates: x=$screenX, y=$screenY")
        }
        
        return try {
            val location = mapProjectionService.screenToMapCoordinates(screenX, screenY, mapInstance)
            
            if (location != null) {
                // Business rule: Validate geographic coordinates are within valid ranges
                if (isValidLocation(location)) {
                    ConversionResult.Success(location)
                } else {
                    ConversionResult.Error("Invalid geographic coordinates: lat=${location.lat}, long=${location.long}")
                }
            } else {
                ConversionResult.Error("Failed to convert screen coordinates to location")
            }
        } catch (e: Exception) {
            ConversionResult.Error("Conversion error: ${e.message}")
        }
    }
    
    private fun isValidLocation(location: Location): Boolean {
        return location.lat in -90.0..90.0 && 
               location.long in -180.0..180.0
    }
}

sealed class ConversionResult {
    data class Success(val location: Location) : ConversionResult()
    data class Error(val message: String) : ConversionResult()
}