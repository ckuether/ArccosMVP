package com.example.core_ui.projection

import com.example.shared.data.model.Location
import cocoapods.GoogleMaps.GMSMapView
import kotlinx.cinterop.CValue
import kotlinx.cinterop.cValue
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGPoint
import platform.CoreLocation.CLLocationCoordinate2D
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual class CalculateMapPositionFromScreenUseCase {
    actual operator fun invoke(
        screenX: Int,
        screenY: Int,
        mapInstance: Any
    ): Location? {
        return try {
            val mapView = mapInstance as GMSMapView

            // Get device scale factor for points-to-pixels conversion
            val deviceScale = platform.UIKit.UIScreen.mainScreen.scale.toInt()

            val screenPoint: CValue<CGPoint> = cValue {
                x = screenX.toDouble() / deviceScale
                y = screenY.toDouble() / deviceScale
            }

            val coordinate: CValue<CLLocationCoordinate2D> = mapView.projection.coordinateForPoint(screenPoint)
            coordinate.useContents {
                Location(lat = this.latitude, long = this.longitude)
            }
        } catch (e: Exception) {
            println("DEBUG CalculateMapPositionFromScreenUseCase: Error converting screen to map position: ${e.message}")
            null
        }
    }
}