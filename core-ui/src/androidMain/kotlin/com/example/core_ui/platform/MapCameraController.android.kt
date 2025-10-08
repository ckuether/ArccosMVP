package com.example.core_ui.platform

import com.example.shared.data.model.Hole
import com.example.shared.usecase.MapCameraPosition
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState

/**
 * Android implementation of MapCameraController using Google Maps Android API.
 */
actual class MapCameraController(
    private val cameraPositionState: CameraPositionState
) {
    
    actual suspend fun applyHoleCameraPosition(hole: Hole, mapCameraPosition: MapCameraPosition) {
        val teeLatLng = LatLng(hole.teeLocation.lat, hole.teeLocation.long)
        val flagLatLng = LatLng(hole.flagLocation.lat, hole.flagLocation.long)
        val bounds = LatLngBounds.builder().apply {
            include(teeLatLng)
            include(flagLatLng)
        }.build()

        try {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngBounds(bounds, 150)
            )

            // Then add rotation with current zoom level
            val currentZoom = cameraPositionState.position.zoom

            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder()
                        .target(LatLng(mapCameraPosition.centerLat, mapCameraPosition.centerLng))
                        .zoom(currentZoom)
                        .bearing(mapCameraPosition.bearing)
                        .build()
                )
            )
        } catch (e: Exception) {
            // Fallback to center between the two points with bearing
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder()
                        .target(LatLng(mapCameraPosition.centerLat, mapCameraPosition.centerLng))
                        .zoom(15f)
                        .bearing(mapCameraPosition.bearing)
                        .build()
                )
            )
        }
    }
}