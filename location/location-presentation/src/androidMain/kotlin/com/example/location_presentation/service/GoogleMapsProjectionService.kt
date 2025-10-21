package com.example.location_presentation.service

import android.graphics.Point
import com.example.location_domain.domain.model.ScreenPoint
import com.example.location_domain.domain.service.MapProjectionService
import com.example.shared.data.model.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

/**
 * Android-specific implementation of MapProjectionService using Google Maps SDK.
 * This is an infrastructure service that handles Google Maps-specific projection operations.
 */
open class GoogleMapsProjectionService : MapProjectionService {
    
    override fun screenToMapCoordinates(
        screenX: Int,
        screenY: Int,
        mapInstance: Any
    ): Location? {
        return try {
            val googleMap = mapInstance as GoogleMap
            val projection = googleMap.projection
            
            val screenPoint = Point(screenX, screenY)
            val latLng: LatLng = projection.fromScreenLocation(screenPoint)
            
            Location(lat = latLng.latitude, long = latLng.longitude)
        } catch (e: Exception) {
            println("DEBUG GoogleMapsProjectionService: Error converting screen to map position: ${e.message}")
            null
        }
    }
    
    override fun mapToScreenCoordinates(
        location: Location,
        mapInstance: Any
    ): ScreenPoint? {
        return try {
            val googleMap = mapInstance as? GoogleMap ?: return null
            val latLng = LatLng(location.lat, location.long)
            val projection = googleMap.projection
            val screenPoint: Point = projection.toScreenLocation(latLng)
            
            ScreenPoint(screenPoint.x, screenPoint.y)
        } catch (e: Exception) {
            println("DEBUG GoogleMapsProjectionService: Error converting map to screen position: ${e.message}")
            null
        }
    }
}