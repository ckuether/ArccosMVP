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
    fun createMarker(
        type: MarkerType, 
        location: Location,
        onLocationChanged: ((Location) -> Unit)? = null
    ): GMSMarker {
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
                marker.title = "⛳ Tee Area"
                marker.snippet = "Tee location"
                marker.draggable = false
            }
            MarkerType.GOLF_FLAG -> {
                golfFlagMarker?.let { marker.icon = it as? UIImage }
                marker.title = "🏌️ Pin/Hole"
                marker.snippet = "Pin location"
                marker.draggable = false
            }
            MarkerType.TARGET_CIRCLE -> {
                targetCircleMarker?.let { marker.icon = it as? UIImage }
                marker.draggable = true
                marker.groundAnchor = cValue { x = 0.5; y = 0.5 } // Center anchor
                
                // Store callback in marker's userData (we'll use this in the map delegate)
                // Note: This would need to be handled in the map view controller
            }
            MarkerType.DEFAULT -> {
                marker.title = null
                marker.snippet = null
                marker.draggable = false
            }
        }
        
        return marker
    }
}

// iOS doesn't use Composable markers, so this is a no-op
@Composable
actual fun GolfMapMarker(
    type: MarkerType,
    location: Location,
    onLocationChanged: ((Location) -> Unit)?
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

// Additional function for creating draggable markers with callbacks
@OptIn(ExperimentalForeignApi::class)
fun createDraggableGolfMapMarker(
    type: MarkerType,
    location: Location,
    onLocationChanged: ((Location) -> Unit)? = null
): GMSMarker {
    return GolfMapMarkerFactory.createMarker(type, location, onLocationChanged)
}