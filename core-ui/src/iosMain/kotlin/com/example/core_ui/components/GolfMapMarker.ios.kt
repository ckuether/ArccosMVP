package com.example.core_ui.components

import com.example.shared.data.model.Location
import com.example.core_ui.platform.MarkerType
import com.example.core_ui.platform.DrawableProvider
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.cValue
import platform.CoreLocation.CLLocationCoordinate2D
import cocoapods.GoogleMaps.*
import platform.UIKit.UIImage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import androidx.compose.runtime.Composable

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
object GolfMapMarkerFactory : KoinComponent {
    private val drawableProvider: DrawableProvider by inject()
    
    @OptIn(ExperimentalForeignApi::class)
    fun createMarker(type: MarkerType, location: Location): GMSMarker {
        val golfBallMarker = drawableProvider.getGolfBallMarker()
        val golfFlagMarker = drawableProvider.getGolfFlagMarker()
        val targetCircleMarker = drawableProvider.getTargetCircleMarker()

        val marker = GMSMarker()
        marker.position = cValue<CLLocationCoordinate2D> {
            latitude = location.lat
            longitude = location.long
        }
        
        when (type) {
            MarkerType.GOLF_BALL -> {
                golfBallMarker?.let { marker.icon = it as? UIImage }
                marker.snippet = "⛳ Tee Area"
            }
            MarkerType.GOLF_FLAG -> {
                golfFlagMarker?.let { marker.icon = it as? UIImage }
                marker.snippet = "🏌️ Pin/Hole"
            }
            MarkerType.TARGET_CIRCLE -> {
                targetCircleMarker?.let { marker.icon = it as? UIImage }
                marker.snippet = "🎯 Target Shot"
            }
            MarkerType.DEFAULT -> {
                marker.snippet = null
            }
        }
        
        return marker
    }
}

// iOS doesn't use Composable markers, so this is a no-op
@Composable
actual fun GolfMapMarker(
    type: MarkerType,
    location: Location
) {
    // No-op for iOS as markers are handled imperatively
}

@OptIn(ExperimentalForeignApi::class)
actual fun createGolfMapMarker(
    type: MarkerType,
    location: Location
): Any {
    return GolfMapMarkerFactory.createMarker(type, location)
}