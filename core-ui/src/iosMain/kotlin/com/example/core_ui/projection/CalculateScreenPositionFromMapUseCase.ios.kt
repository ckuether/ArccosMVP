package com.example.core_ui.projection

import com.example.shared.data.model.Location
import cocoapods.GoogleMaps.*
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.*
import platform.CoreLocation.CLLocationCoordinate2DMake

actual class CalculateScreenPositionFromMapUseCase {
    
    /**
     * Uses Google Maps iOS SDK projection to convert lat/lng to screen coordinates
     * This is the most accurate method as it uses the same projection as the map itself
     */
    @OptIn(ExperimentalForeignApi::class)
    actual operator fun invoke(
        location: Location,
        mapInstance: Any
    ): ScreenPosition? {
        println("DEBUG CalculateScreenPosition: Called with location: lat=${location.lat}, lng=${location.long}")
        val mapView = mapInstance as? GMSMapView ?: run {
            println("DEBUG CalculateScreenPosition: mapInstance is not GMSMapView: $mapInstance")
            return null
        }
        
        return try {
            val coordinate = CLLocationCoordinate2DMake(location.lat, location.long)
            
            // Get camera info for debugging
            val camera = mapView.camera
            camera.target.useContents {
                println("DEBUG CalculateScreenPosition: Map center: lat=${this.latitude}, lng=${this.longitude}, zoom=${camera.zoom}")
            }
            
            // Get actual map view bounds for debugging
            val bounds = mapView.bounds
            bounds.useContents {
                println("DEBUG CalculateScreenPosition: Map bounds: width=${this.size.width}, height=${this.size.height}")
            }
            
            val screenPoint: CValue<CGPoint> = mapView.projection.pointForCoordinate(coordinate)

            val x = screenPoint.useContents { x }
            val y = screenPoint.useContents { y }

            // Get the scale factor to convert from points to pixels
            val scale = mapView.layer.contentsScale

            // Get device scale factor for points-to-pixels conversion
            val deviceScale = platform.UIKit.UIScreen.mainScreen.scale.toInt()

            // Convert from points to pixels by multiplying by scale
            val pixelX = (x * scale).toInt() * deviceScale
            val pixelY = (y * scale).toInt() * deviceScale

            val result = ScreenPosition(pixelX, pixelY)
            println("DEBUG CalculateScreenPosition: CLLocationCoordinate(${location.lat}, ${location.long}) -> CGPoint($x, $y) -> Scale($scale) -> Result($result)")
            
            result
        } catch (e: Exception) {
            println("DEBUG CalculateScreenPosition: Exception: ${e.message}")
            null
        }
    }
}