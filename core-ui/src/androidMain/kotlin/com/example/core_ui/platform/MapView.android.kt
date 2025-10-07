package com.example.core_ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.shared.data.model.Hole
import com.example.shared.domain.usecase.CalculateBearingUseCase
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.core_ui.components.GolfMapMarker
import org.koin.compose.koinInject

@Composable
actual fun MapView(
    modifier: Modifier,
    currentHole: Hole?,
    hasLocationPermission: Boolean,
    onMapClick: ((MapLocation) -> Unit)?
) {
    val cameraPositionState = rememberCameraPositionState()
    
    // Create use case for bearing calculation
    val calculateBearingUseCase = remember { CalculateBearingUseCase() }
    
    // Inject drawable provider
    val drawableProvider: DrawableProvider = koinInject()
    
    // Get custom markers from the drawable provider
    val golfBallBitmap = drawableProvider.getGolfBallMarker() as? BitmapDescriptor
    val golfFlagBitmap = drawableProvider.getGolfFlagMarker() as? BitmapDescriptor
    
    // Target circle bitmap will be created lazily when needed
    
    // Default to Denver, CO if no center location provided
    val defaultLocation = LatLng(39.7392, -104.9903)

    // Handle currentHole updates with highest priority
    LaunchedEffect(currentHole) {
        currentHole?.let { hole ->
            val teeLatLng = LatLng(hole.teeLocation.lat, hole.teeLocation.long)
            val flagLatLng = LatLng(hole.flagLocation.lat, hole.flagLocation.long)
            val bounds = LatLngBounds.builder().apply {
                include(teeLatLng)
                include(flagLatLng)
            }.build()

            // Calculate bearing for orientation
            val bearing = calculateBearingUseCase(hole.teeLocation, hole.flagLocation).toFloat()

            try {
                // First set bounds with original zoom logic
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(bounds, 200) // 200dp padding
                )

                // Then add rotation with current zoom level
                val currentZoom = cameraPositionState.position.zoom
                val centerLat = (hole.teeLocation.lat + hole.flagLocation.lat) / 2
                val centerLng = (hole.teeLocation.long + hole.flagLocation.long) / 2

                cameraPositionState.animate(
                    CameraUpdateFactory.newCameraPosition(
                        com.google.android.gms.maps.model.CameraPosition.Builder()
                            .target(LatLng(centerLat, centerLng))
                            .zoom(currentZoom)
                            .bearing(bearing) // Rotate map so tee-to-flag is vertical
                            .build()
                    )
                )
            } catch (e: Exception) {
                // Fallback to center between the two points with bearing
                val centerLat = (hole.teeLocation.lat + hole.flagLocation.lat) / 2
                val centerLng = (hole.teeLocation.long + hole.flagLocation.long) / 2
                cameraPositionState.animate(
                    CameraUpdateFactory.newCameraPosition(
                        com.google.android.gms.maps.model.CameraPosition.Builder()
                            .target(LatLng(centerLat, centerLng))
                            .zoom(15f)
                            .bearing(bearing)
                            .build()
                    )
                )
            }
        }
    }
    
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            mapType = MapType.HYBRID,
            isMyLocationEnabled = hasLocationPermission
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            compassEnabled = true,
            myLocationButtonEnabled = false
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
        currentHole?.teeLocation?.let {
            GolfMapMarker(MarkerType.GOLF_BALL, it)
        }

        currentHole?.flagLocation?.let {
            GolfMapMarker(MarkerType.GOLF_FLAG, it)
        }
    }
}

