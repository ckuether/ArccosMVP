package com.example.core_ui.components

import androidx.compose.runtime.Composable
import com.example.shared.data.model.Location
import com.example.core_ui.platform.MarkerType
import com.example.core_ui.platform.DrawableProvider
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.example.core_ui.utils.BitmapUtils
import org.koin.compose.koinInject

@Composable
actual fun GolfMapMarker(
    type: MarkerType,
    location: Location
) {
    val drawableProvider: DrawableProvider = koinInject()
    
    val golfBallBitmap = drawableProvider.getGolfBallMarker() as? BitmapDescriptor
    val golfFlagBitmap = drawableProvider.getGolfFlagMarker() as? BitmapDescriptor

    Marker(
        state = MarkerState(
            position = LatLng(location.lat, location.long)
        ),
        icon = when (type) {
            MarkerType.GOLF_BALL -> {
                golfBallBitmap ?: BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
            }
            MarkerType.GOLF_FLAG -> {
                golfFlagBitmap ?: BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            }
            MarkerType.TARGET_CIRCLE -> {
                try {
                    BitmapUtils.createTargetCircleBitmap()
                } catch (e: Exception) {
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                }
            }
            MarkerType.DEFAULT -> {
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            }
        },
        snippet = when (type) {
            MarkerType.GOLF_BALL -> "⛳ Tee Area"
            MarkerType.GOLF_FLAG -> "🏌️ Pin/Hole"
            MarkerType.TARGET_CIRCLE -> "🎯 Target Shot"
            MarkerType.DEFAULT -> null
        }
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