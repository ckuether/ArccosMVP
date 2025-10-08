package com.example.core_ui.platform

import com.example.shared.data.model.Hole
import com.example.shared.usecase.CameraPosition
import cocoapods.GoogleMaps.*
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2DMake

/**
 * iOS implementation of MapCameraController using Google Maps iOS SDK.
 * Mimics Android's bounds-based zoom calculation.
 */
@OptIn(ExperimentalForeignApi::class)
actual class MapCameraController(
    private val mapView: GMSMapView
) {

    companion object {
        private const val MAP_PADDING_TOP = 64.0
        private const val MAP_PADDING = 32.0
    }
    
    actual suspend fun applyHoleCameraPosition(hole: Hole, cameraPosition: CameraPosition) {
        try {
            // Create bounds similar to Android's LatLngBounds
            val teeLoc = CLLocationCoordinate2DMake(hole.teeLocation.lat, hole.teeLocation.long)
            val flagLoc = CLLocationCoordinate2DMake(hole.flagLocation.lat, hole.flagLocation.long)
            val bounds = GMSCoordinateBounds(teeLoc, flagLoc)

            val insets = platform.UIKit.UIEdgeInsetsMake(MAP_PADDING_TOP, MAP_PADDING, MAP_PADDING, MAP_PADDING)
            val boundsCamera = mapView.cameraForBounds(bounds, insets)
            
            if (boundsCamera != null) {
                // Create final camera with bounds zoom but add bearing rotation
                val finalCamera = GMSCameraPosition.cameraWithLatitude(
                    cameraPosition.centerLat,
                    cameraPosition.centerLng,
                    boundsCamera.zoom,
                    cameraPosition.bearing.toDouble(),
                    0.0
                )
                
                mapView.animateToCameraPosition(finalCamera)
            } else {
                // Fallback if bounds calculation fails
                val fallbackCamera = GMSCameraPosition.cameraWithLatitude(
                    cameraPosition.centerLat,
                    cameraPosition.centerLng,
                    16.0f,
                    cameraPosition.bearing.toDouble(),
                    0.0
                )
                
                mapView.animateToCameraPosition(fallbackCamera)
            }
            
        } catch (e: Exception) {
            // Fallback to fixed zoom with bearing
            val camera = GMSCameraPosition.cameraWithLatitude(
                cameraPosition.centerLat,
                cameraPosition.centerLng,
                15.0f,
                cameraPosition.bearing.toDouble(),
                0.0
            )
            
            mapView.animateToCameraPosition(camera)
        }
    }
}