package com.example.core_ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.shared.data.model.Hole
import com.example.shared.domain.usecase.CalculateBearingUseCase
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.example.core_ui.utils.BitmapUtils
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import org.koin.compose.koinInject

@Composable
actual fun MapView(
    modifier: Modifier,
    userLocations: List<MapLocation>,
    centerLocation: MapLocation?,
    initialBounds: Pair<MapLocation, MapLocation>?,
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
    
    // Handle initialBounds updates (only when no currentHole)
    LaunchedEffect(initialBounds) {
        if (currentHole == null && initialBounds != null) {
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
    }
    
    // Handle centerLocation updates (only when no currentHole or initialBounds)
    LaunchedEffect(centerLocation) {
        if (currentHole == null && initialBounds == null && centerLocation != null) {
            val latLng = LatLng(centerLocation.latitude, centerLocation.longitude)
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
    }
    
    // Handle userLocations updates (only when no higher priority data)
    LaunchedEffect(userLocations) {
        //DO Nothing for now
    }
    
    // Handle default location (when no data is provided)
    LaunchedEffect(Unit) {
        if (currentHole == null && initialBounds == null && centerLocation == null && userLocations.isEmpty()) {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))
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
        // Add markers for each location
        userLocations.forEach { location ->
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
                    MarkerType.TARGET_CIRCLE -> {
                        // Create target circle bitmap when GoogleMap is ready
                        try {
                            BitmapUtils.createTargetCircleBitmap()
                        } catch (e: Exception) {
                            // Fallback to default marker if bitmap creation fails
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                        }
                    }
                    MarkerType.DEFAULT -> {
                        // Default red marker for regular location points
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    }
                },
                snippet = when (location.markerType) {
                    MarkerType.GOLF_BALL -> "⛳ Tee Area"
                    MarkerType.GOLF_FLAG -> "🏌️ Pin/Hole"
                    MarkerType.TARGET_CIRCLE -> "🎯 Target Shot"
                    MarkerType.DEFAULT -> null
                }
            )
        }
    }
}