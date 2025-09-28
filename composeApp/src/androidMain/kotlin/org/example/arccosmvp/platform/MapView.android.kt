package org.example.arccosmvp.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import org.example.arccosmvp.utils.AndroidDrawableHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
actual fun MapView(
    modifier: Modifier,
    locations: List<MapLocation>,
    centerLocation: MapLocation?,
    initialBounds: Pair<MapLocation, MapLocation>?,
    onMapClick: ((MapLocation) -> Unit)?
) {
    val cameraPositionState = rememberCameraPositionState()
    
    // Create custom golf ball marker bitmap
    val golfBallBitmap = AndroidDrawableHelper.createGolfBallMarker()
    
    // Create custom golf flag marker bitmap
    val golfFlagBitmap = AndroidDrawableHelper.createGolfFlagMarker()
    
    // Default to Denver, CO if no center location provided
    val defaultLocation = LatLng(39.7392, -104.9903)
    
    LaunchedEffect(centerLocation, locations, initialBounds) {
        when {
            initialBounds != null -> {
                // Use initial bounds (highest priority)
                val bounds = LatLngBounds.builder().apply {
                    include(LatLng(initialBounds.first.latitude, initialBounds.first.longitude))
                    include(LatLng(initialBounds.second.latitude, initialBounds.second.longitude))
                }.build()
                
                try {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngBounds(bounds, 200) // 200dp padding
                    )
                } catch (e: Exception) {
                    // Fallback to center between the two points
                    val centerLat = (initialBounds.first.latitude + initialBounds.second.latitude) / 2
                    val centerLng = (initialBounds.first.longitude + initialBounds.second.longitude) / 2
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(LatLng(centerLat, centerLng), 15f)
                    )
                }
            }
            centerLocation != null -> {
                val latLng = LatLng(centerLocation.latitude, centerLocation.longitude)
                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
            locations.isNotEmpty() -> {
                // Fit all locations in view
                val bounds = LatLngBounds.builder().apply {
                    locations.forEach { location ->
                        include(LatLng(location.latitude, location.longitude))
                    }
                }.build()
                
                try {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngBounds(bounds, 100)
                    )
                } catch (e: Exception) {
                    // Fallback to first location if bounds calculation fails
                    val firstLocation = locations.first()
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(firstLocation.latitude, firstLocation.longitude), 
                            15f
                        )
                    )
                }
            }
            else -> {
                // Default location
                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))
            }
        }
    }
    
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            mapType = MapType.HYBRID,
            isMyLocationEnabled = true
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            compassEnabled = true,
            myLocationButtonEnabled = true
        ),
        onMapClick = { latLng ->
            onMapClick?.invoke(
                MapLocation(
                    latitude = latLng.latitude,
                    longitude = latLng.longitude
                )
            )
        }
    ) {
        // Add markers for each location
        locations.forEach { location ->
            Marker(
                state = MarkerState(
                    position = LatLng(location.latitude, location.longitude)
                ),
                title = location.title ?: "Location",
                icon = when (location.markerType) {
                    MarkerType.GOLF_BALL -> {
                        // Use custom golf ball bitmap if available, fallback to orange marker
                        golfBallBitmap ?: BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                    }
                    MarkerType.GOLF_FLAG -> {
                        // Use custom golf flag bitmap if available, fallback to green marker
                        golfFlagBitmap ?: BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                    }
                    MarkerType.DEFAULT -> {
                        // Default red marker for regular location points
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    }
                },
                snippet = when (location.markerType) {
                    MarkerType.GOLF_BALL -> "⛳ Tee Area"
                    MarkerType.GOLF_FLAG -> "🏌️ Pin/Hole"
                    MarkerType.DEFAULT -> null
                }
            )
        }
    }
}