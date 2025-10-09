package com.example.core_ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.geometry.Offset
import com.example.shared.data.model.Location
import com.example.core_ui.platform.MarkerType
import com.example.core_ui.platform.DrawableProvider
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState
import org.koin.compose.koinInject

@Composable
actual fun GolfMapMarker(
    type: MarkerType,
    location: Location,
    onLocationChanged: ((Location) -> Unit)?
) {
    val drawableProvider: DrawableProvider = koinInject()
    
    val golfBallBitmap = drawableProvider.getGolfBallMarker() as? BitmapDescriptor
    val golfFlagBitmap = drawableProvider.getGolfFlagMarker() as? BitmapDescriptor
    val targetCircleBitmap = drawableProvider.getTargetCircleMarker() as? BitmapDescriptor

    // Use rememberMarkerState for draggable markers to track position changes
    val markerState = rememberMarkerState(
        key = "${type.name}_marker", // Fixed key that doesn't change with location
        position = LatLng(location.lat, location.long)
    )
    
    // Update marker position when location prop changes
    LaunchedEffect(location) {
        markerState.position = LatLng(location.lat, location.long)
    }
    
    // Monitor marker position changes for draggable markers (only when user drags)
    if (type == MarkerType.TARGET_CIRCLE && onLocationChanged != null) {
        LaunchedEffect(markerState.position) {
            val newLocation = Location(
                lat = markerState.position.latitude,
                long = markerState.position.longitude
            )
            onLocationChanged(newLocation)
        }
    }

    Marker(
        state = markerState,
        icon = when (type) {
            MarkerType.GOLF_BALL -> {
                golfBallBitmap ?: BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
            }
            MarkerType.GOLF_FLAG -> {
                golfFlagBitmap ?: BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            }
            MarkerType.TARGET_CIRCLE -> {
                targetCircleBitmap ?: BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
            }
            MarkerType.DEFAULT -> {
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            }
        },
        title = when (type) {
            MarkerType.GOLF_BALL -> "⛳ Tee Area"
            MarkerType.GOLF_FLAG -> "🏌️ Pin/Hole"
            else -> null
        },
        snippet = when (type) {
            MarkerType.GOLF_BALL -> "Tee location"
            MarkerType.GOLF_FLAG -> "Pin location"
            else -> null
        },
        anchor = if (type == MarkerType.TARGET_CIRCLE) {
            Offset(0.5f, 0.5f) // Center anchor for target circle
        } else {
            Offset(0.5f, 1.0f) // Default bottom-center anchor for other markers
        },
        draggable = type == MarkerType.TARGET_CIRCLE
    )
}

// Android doesn't need this function but we need to implement it for expect/actual
actual fun createGolfMapMarker(
    type: MarkerType,
    location: Location
): Any {
    // On Android, markers are handled through Compose, so this returns Unit
    return Unit
}