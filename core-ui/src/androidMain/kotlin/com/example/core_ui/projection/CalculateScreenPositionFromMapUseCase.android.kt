package com.example.core_ui.projection

import com.example.shared.data.model.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import android.graphics.Point

actual class CalculateScreenPositionFromMapUseCase {
    
    /**
     * Uses Google Maps Android SDK projection to convert lat/lng to screen coordinates
     * This is the most accurate method as it uses the same projection as the map itself
     */
    actual operator fun invoke(
        location: Location,
        mapInstance: Any
    ): ScreenPosition? {
        val googleMap = mapInstance as? GoogleMap ?: run {
            return null
        }
        return try {
            val latLng = LatLng(location.lat, location.long)
            val projection = googleMap.projection
            val screenPoint: Point = projection.toScreenLocation(latLng)
            val result = ScreenPosition(screenPoint.x, screenPoint.y)
            result
        } catch (e: Exception) {
            println("DEBUG CalculateScreenPosition: Exception: ${e.message}")
            null
        }
    }
}