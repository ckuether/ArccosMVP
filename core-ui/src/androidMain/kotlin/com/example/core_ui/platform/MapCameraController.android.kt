package com.example.core_ui.platform

import com.example.shared.data.model.Hole
import com.example.shared.usecase.CameraPosition
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
    
    actual suspend fun applyHoleCameraPosition(hole: Hole, cameraPosition: CameraPosition) {
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
                    com.google.android.gms.maps.model.CameraPosition.Builder()
                        .target(LatLng(cameraPosition.centerLat, cameraPosition.centerLng))
                        .zoom(currentZoom)
                        .bearing(cameraPosition.bearing)
                        .build()
                )
            )
        } catch (e: Exception) {
            // Fallback to center between the two points with bearing
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    com.google.android.gms.maps.model.CameraPosition.Builder()
                        .target(LatLng(cameraPosition.centerLat, cameraPosition.centerLng))
                        .zoom(15f)
                        .bearing(cameraPosition.bearing)
                        .build()
                )
            )
        }
    }
}