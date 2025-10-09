package com.example.core_ui.projection

import com.example.shared.data.model.Location
import GoogleMaps.*
import platform.CoreGraphics.*

actual class CalculateScreenPositionFromMapUseCase {
    
    /**
     * Uses Google Maps iOS SDK projection to convert lat/lng to screen coordinates
     * This is the most accurate method as it uses the same projection as the map itself
     */
    actual operator fun invoke(
        location: Location,
        mapInstance: Any
    ): ScreenPosition? {
        val mapView = mapInstance as? GMSMapView ?: return null
        
        return try {
            val coordinate = CLLocationCoordinate2DMake(location.lat, location.long)
            val screenPoint = mapView.projection.pointForCoordinate(coordinate)
            
            ScreenPosition(screenPoint.x.toInt(), screenPoint.y.toInt())
        } catch (e: Exception) {
            null
        }
    }
}