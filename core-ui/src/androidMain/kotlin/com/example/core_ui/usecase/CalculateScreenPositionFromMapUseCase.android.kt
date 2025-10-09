package com.example.core_ui.usecase

import android.graphics.Point
import com.example.core_ui.projection.ScreenPosition
import com.example.shared.data.model.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

actual class CalculateScreenPositionFromMapUseCase {
    
    /**
     * Uses Google Maps Android SDK projection to convert lat/lng to screen coordinates
     * This is the most accurate method as it uses the same projection as the map itself
     */
    actual operator fun invoke(
        location: Location,
        mapInstance: Any
    ): ScreenPosition? {
        val googleMap = mapInstance as? GoogleMap ?: return null
        
        return try {
            val latLng = LatLng(location.lat, location.long)
            val projection = googleMap.projection
            val screenPoint: Point = projection.toScreenLocation(latLng)
            
            ScreenPosition(screenPoint.x, screenPoint.y)
        } catch (e: Exception) {
            null
        }
    }
}