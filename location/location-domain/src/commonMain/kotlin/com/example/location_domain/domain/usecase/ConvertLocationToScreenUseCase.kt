package com.example.location_domain.domain.usecase

import com.example.location_domain.domain.model.ScreenPoint
import com.example.location_domain.domain.service.MapProjectionService
import com.example.shared.data.model.Location

/**
 * Use Case for converting geographic location to screen coordinates.
 * Contains business logic for coordinate conversion with validation.
 */
class ConvertLocationToScreenUseCase(
    private val mapProjectionService: MapProjectionService
) {
    
    /**
     * Converts geographic location to screen coordinates with business validation
     * @param location Geographic location to convert
     * @param mapInstance Platform-specific map instance
     * @return ScreenConversionResult containing screen point or error
     */
    fun execute(
        location: Location,
        mapInstance: Any
    ): ScreenConversionResult {
        // Business rule: Validate geographic coordinates are within valid ranges
        if (!isValidLocation(location)) {
            return ScreenConversionResult.Error("Invalid geographic coordinates: lat=${location.lat}, long=${location.long}")
        }
        
        return try {
            val screenPoint = mapProjectionService.mapToScreenCoordinates(location, mapInstance)
            
            if (screenPoint != null) {
                // Business rule: Screen coordinates should be positive
                if (screenPoint.x >= 0 && screenPoint.y >= 0) {
                    ScreenConversionResult.Success(screenPoint)
                } else {
                    ScreenConversionResult.Error("Invalid screen coordinates: x=${screenPoint.x}, y=${screenPoint.y}")
                }
            } else {
                ScreenConversionResult.Error("Failed to convert location to screen coordinates")
            }
        } catch (e: Exception) {
            ScreenConversionResult.Error("Conversion error: ${e.message}")
        }
    }
    
    private fun isValidLocation(location: Location): Boolean {
        return location.lat in -90.0..90.0 && 
               location.long in -180.0..180.0
    }
}

sealed class ScreenConversionResult {
    data class Success(val screenPoint: ScreenPoint) : ScreenConversionResult()
    data class Error(val message: String) : ScreenConversionResult()
}