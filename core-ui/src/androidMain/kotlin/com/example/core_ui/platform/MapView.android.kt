package com.example.core_ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.shared.data.model.Hole
import com.example.shared.data.model.Location
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.LatLng
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
    
    // State for draggable target location
    var targetLocation by remember { mutableStateOf<Location?>(currentHole?.initialTarget) }
    
    // Initialize target location from currentHole
    LaunchedEffect(currentHole?.initialTarget) {
        targetLocation = currentHole?.initialTarget
    }

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

        // Use the updated target location (which can be dragged)
        targetLocation?.let { location ->
            GolfMapMarker(
                type = MarkerType.TARGET_CIRCLE,
                location = location,
                onLocationChanged = { newLocation ->
                    targetLocation = newLocation
                    onMapClick?.invoke(
                        MapLocation(
                            latitude = newLocation.lat,
                            longitude = newLocation.long
                        )
                    )
                }
            )
        }
        
        // Draw line from tee to target location
        if (currentHole?.teeLocation != null && targetLocation != null) {
            Polyline(
                points = listOf(
                    LatLng(currentHole.teeLocation.lat, currentHole.teeLocation.long),
                    LatLng(targetLocation!!.lat, targetLocation!!.long)
                ),
                color = Color.White,
                width = 2.dp.value
            )
        }
        
        // Draw line from target to hole
        if (currentHole?.flagLocation != null && targetLocation != null) {
            Polyline(
                points = listOf(
                    LatLng(targetLocation!!.lat, targetLocation!!.long),
                    LatLng(currentHole.flagLocation.lat, currentHole.flagLocation.long)
                ),
                color = Color.White,
                width = 2.dp.value
            )
        }
    }
}

