package com.example.core_ui.projection

import com.example.shared.data.model.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import android.graphics.Point

actual class CalculateMapPositionFromScreenUseCase {
    actual operator fun invoke(
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
            println("DEBUG CalculateMapPositionFromScreenUseCase: Error converting screen to map position: ${e.message}")
            null
        }
    }
}