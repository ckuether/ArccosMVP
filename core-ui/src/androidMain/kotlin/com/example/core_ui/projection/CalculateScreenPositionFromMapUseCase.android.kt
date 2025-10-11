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
        println("DEBUG CalculateScreenPosition: Called with location: lat=${location.lat}, lng=${location.long}")
        val googleMap = mapInstance as? GoogleMap ?: run {
            println("DEBUG CalculateScreenPosition: mapInstance is not GoogleMap: $mapInstance")
            return null
        }
        return try {
            val latLng = LatLng(location.lat, location.long)
            val projection = googleMap.projection
            println("DEBUG CalculateScreenPosition: Map center: ${googleMap.cameraPosition.target}, zoom: ${googleMap.cameraPosition.zoom}")
            val screenPoint: Point = projection.toScreenLocation(latLng)
            val result = ScreenPosition(screenPoint.x, screenPoint.y)
            println("DEBUG CalculateScreenPosition: LatLng($latLng) -> ScreenPoint($screenPoint) -> Result($result)")
            result
        } catch (e: Exception) {
            println("DEBUG CalculateScreenPosition: Exception: ${e.message}")
            null
        }
    }
}