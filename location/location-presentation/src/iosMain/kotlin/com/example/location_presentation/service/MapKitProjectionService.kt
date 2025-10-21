package com.example.location_presentation.service

import cocoapods.GoogleMaps.GMSMapView
import com.example.location_domain.domain.model.ScreenPoint
import com.example.location_domain.domain.service.MapProjectionService
import com.example.shared.data.model.Location
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGPoint
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.CoreLocation.CLLocationCoordinate2D
import platform.UIKit.UIScreen

/**
 * iOS-specific implementation of MapProjectionService using MapKit.
 * This is an infrastructure service that handles MapKit-specific projection operations.
 */
open class MapKitProjectionService : MapProjectionService {
    
    @OptIn(ExperimentalForeignApi::class)
    override fun screenToMapCoordinates(
        screenX: Int,
        screenY: Int,
        mapInstance: Any
    ): Location? {
        return try {
            val mapView = mapInstance as GMSMapView

            // Get device scale factor for points-to-pixels conversion
            val deviceScale = UIScreen.mainScreen.scale.toInt()

            val screenPoint: CValue<CGPoint> = cValue {
                x = screenX.toDouble() / deviceScale
                y = screenY.toDouble() / deviceScale
            }

            val coordinate: CValue<CLLocationCoordinate2D> = mapView.projection.coordinateForPoint(screenPoint)
            coordinate.useContents {
                Location(lat = this.latitude, long = this.longitude)
            }
        } catch (e: Exception) {
            println("DEBUG MapKitProjectionService: Error converting screen to map position: ${e.message}")
            null
        }
    }
    
    @OptIn(ExperimentalForeignApi::class)
    override fun mapToScreenCoordinates(
        location: Location,
        mapInstance: Any
    ): ScreenPoint? {
        return try {
            val mapView = mapInstance as? GMSMapView ?: return null

            val coordinate = CLLocationCoordinate2DMake(location.lat, location.long)

            val screenPoint: CValue<CGPoint> = mapView.projection.pointForCoordinate(coordinate)

            val x = screenPoint.useContents { x }
            val y = screenPoint.useContents { y }

            // Get the scale factor to convert from points to pixels
            val scale = mapView.layer.contentsScale

            // Get device scale factor for points-to-pixels conversion
            val deviceScale = UIScreen.mainScreen.scale.toInt()

            // Convert from points to pixels by multiplying by scale
            val pixelX = (x * scale).toInt() * deviceScale
            val pixelY = (y * scale).toInt() * deviceScale
            
            ScreenPoint(pixelX, pixelY)
        } catch (e: Exception) {
            println("DEBUG MapKitProjectionService: Error converting map to screen position: ${e.message}")
            null
        }
    }
}