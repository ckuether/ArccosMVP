package com.example.core_ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.shared.data.model.Hole
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.core_ui.components.GolfMapMarker
import com.example.shared.usecase.CalculateMapCameraPositionUseCase
import org.koin.compose.koinInject

@Composable
actual fun MapView(
    modifier: Modifier,
    currentHole: Hole?,
    hasLocationPermission: Boolean,
    onMapClick: ((MapLocation) -> Unit)?
) {
    val cameraPositionState = rememberCameraPositionState()
    
    // Inject use case for camera position calculation
    val calculateCameraPositionUseCase: CalculateMapCameraPositionUseCase = koinInject()
    
    // Create camera controller
    val cameraController = remember { MapCameraController(cameraPositionState) }

    // Target circle bitmap will be created lazily when needed

    // Handle currentHole updates with highest priority
    LaunchedEffect(currentHole) {
        currentHole?.let { hole ->
            // Calculate camera position using shared use case
            val cameraPosition = calculateCameraPositionUseCase(hole)
            
            // Apply camera positioning using platform-specific controller
            cameraController.applyHoleCameraPosition(hole, cameraPosition)
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

        currentHole?.initialTarget?.let {
            GolfMapMarker(MarkerType.TARGET_CIRCLE, it)
        }
    }
}

