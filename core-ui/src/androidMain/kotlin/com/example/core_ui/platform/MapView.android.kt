package com.example.core_ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.shared.data.model.Hole
import com.example.shared.data.model.Location
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.shared.usecase.CalculateMapCameraPositionUseCase
import androidx.compose.ui.layout.onSizeChanged
import org.koin.compose.koinInject

@Composable
actual fun MapView(
    modifier: Modifier,
    currentHole: Hole?,
    targetLocation: Location?,
    hasLocationPermission: Boolean,
    onMapClick: ((MapLocation) -> Unit)?,
    onTargetLocationChanged: ((Location) -> Unit)?,
    onMapSizeChanged: ((width: Int, height: Int) -> Unit)?,
    onCameraPositionChanged: ((MapCameraPosition) -> Unit)?,
    onMapReady: ((Any) -> Unit)?
) {
    val cameraPositionState = rememberCameraPositionState()
    
    // Inject use case for camera position calculation
    val calculateCameraPositionUseCase: CalculateMapCameraPositionUseCase = koinInject()
    
    // Create camera controller
    val cameraController = remember { MapCameraController(cameraPositionState) }

    // Track camera position changes
    LaunchedEffect(cameraPositionState.position) {
        onCameraPositionChanged?.invoke(
            MapCameraPosition(
                latitude = cameraPositionState.position.target.latitude,
                longitude = cameraPositionState.position.target.longitude,
                zoom = cameraPositionState.position.zoom
            )
        )
    }

    // Handle currentHole updates with highest priority
    LaunchedEffect(currentHole) {
        currentHole?.let { hole ->
            println("DEBUG MapView: Setting camera for hole: ${hole.id}")
            // Calculate camera position using shared use case
            val cameraPosition = calculateCameraPositionUseCase(hole)
            println("DEBUG MapView: Calculated camera position: $cameraPosition")
            
            // Apply camera positioning using platform-specific controller
            cameraController.applyHoleCameraPosition(hole, cameraPosition)
            println("DEBUG MapView: Camera position applied")
        }
    }
    
    GoogleMap(
        modifier = modifier
            .onSizeChanged { size -> 
                onMapSizeChanged?.invoke(size.width, size.height)
            },
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
        },
        onMapLoaded = {
            // Trigger initial camera position callback
            onCameraPositionChanged?.invoke(
                MapCameraPosition(
                    latitude = cameraPositionState.position.target.latitude,
                    longitude = cameraPositionState.position.target.longitude,
                    zoom = cameraPositionState.position.zoom
                )
            )
        }
    ) {
        // Use MapEffect to access the GoogleMap instance
        MapEffect { googleMap ->
            println("DEBUG MapView: GoogleMap instance available: $googleMap")
            onMapReady?.invoke(googleMap)
        }
        // Markers and polylines are now rendered using Compose components with screen projection in RoundOfGolf
    }
}

