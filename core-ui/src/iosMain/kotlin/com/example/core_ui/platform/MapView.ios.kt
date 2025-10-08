package com.example.core_ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import platform.Foundation.NSLog
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import com.example.shared.data.model.Hole
import org.koin.compose.koinInject
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CValue
import platform.CoreLocation.CLLocationCoordinate2D
import cocoapods.GoogleMaps.*
import kotlinx.cinterop.useContents
import platform.darwin.NSObject
import com.example.core_ui.components.createGolfMapMarker
import com.example.shared.usecase.CalculateMapCameraPositionUseCase

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Composable
actual fun MapView(
    modifier: Modifier,
    currentHole: Hole?,
    hasLocationPermission: Boolean,
    onMapClick: ((MapLocation) -> Unit)?
) {
    val calculateCameraPositionUseCase: CalculateMapCameraPositionUseCase = koinInject()
    val clickHandlerState = remember { mutableStateOf<((MapLocation) -> Unit)?>(null) }
    val mapViewRef = remember { mutableStateOf<GMSMapView?>(null) }
    val markersRef = remember { mutableStateOf<List<GMSMarker>>(emptyList()) }
    val delegateRef = remember { mutableStateOf<GMSMapViewDelegateProtocol?>(null) }
    val cameraControllerRef = remember { mutableStateOf<MapCameraController?>(null) }

    // Handle camera updates when hole changes
    LaunchedEffect(currentHole) {
        mapViewRef.value?.let { mapView ->
            cameraControllerRef.value?.let { cameraController ->
                NSLog("GoogleMaps: LaunchedEffect updating camera position")
                when {
                    currentHole != null -> {
                        // Calculate camera position using shared use case
                        val cameraPosition = calculateCameraPositionUseCase(currentHole)
                        
                        // Apply camera positioning using platform-specific controller
                        cameraController.applyHoleCameraPosition(currentHole, cameraPosition)
                        
                        // Clear existing markers
                        markersRef.value.forEach { marker ->
                            marker.map = null
                        }
                        
                        // Add new markers
                        val newMarkers = mutableListOf<GMSMarker>()
                        
                        // Add tee marker
                        val teeMarker = createGolfMapMarker(MarkerType.GOLF_BALL, currentHole.teeLocation) as GMSMarker
                        teeMarker.map = mapView
                        newMarkers.add(teeMarker)
                        
                        // Add flag marker  
                        val flagMarker = createGolfMapMarker(MarkerType.GOLF_FLAG, currentHole.flagLocation) as GMSMarker
                        flagMarker.map = mapView
                        newMarkers.add(flagMarker)
                        
                        markersRef.value = newMarkers
                    }
                }
            }
        }
    }
    
    // Debug LaunchedEffect to track click handler changes
    LaunchedEffect(onMapClick) {
        NSLog("GoogleMaps: onMapClick parameter changed - is ${if (onMapClick != null) "not null" else "null"}")
        clickHandlerState.value = onMapClick
    }
    
    UIKitView(
        modifier = modifier,
        interactive = true,
        factory = {
            // Set default camera position (will be updated by camera controller if hole exists)
            val initialCamera = GMSCameraPosition.cameraWithLatitude(39.7392, -104.9903, 10.0f)

            // Create the map view
            val mapView = GMSMapView()
            mapView.setCamera(initialCamera)

            // Configure map settings
            mapView.setMapType(kGMSTypeHybrid)
            mapView.setMyLocationEnabled(hasLocationPermission)
            
            // Configure gestures - enable all and allow taps
            mapView.settings.setAllGesturesEnabled(true)
            
            NSLog("GoogleMaps: Map view configured with individual gestures")

            // Create and set delegate for map interactions
            val mapDelegate = object : NSObject(), GMSMapViewDelegateProtocol {
                
                override fun mapView(
                    mapView: GMSMapView,
                    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
                    didTapAtCoordinate: CValue<CLLocationCoordinate2D>
                ) {
                    NSLog("GoogleMaps: didTapAtCoordinate called - map was tapped! Delegate is still active.")

                    clickHandlerState.value?.let { clickHandler ->
                        didTapAtCoordinate.useContents {
                            val mapLocation = MapLocation(
                                latitude = this.latitude,
                                longitude = this.longitude
                            )
                            NSLog("GoogleMaps: Invoking click handler for lat=${mapLocation.latitude}, lng=${mapLocation.longitude}")
                            clickHandler(mapLocation)
                        }
                    } ?: run {
                        NSLog("GoogleMaps: No click handler available - onMapClick is null")
                    }
                }
            }

            // Store delegate reference to prevent deallocation
            delegateRef.value = mapDelegate
            mapView.setDelegate(mapDelegate)
            mapViewRef.value = mapView
            
            // Initialize camera controller
            cameraControllerRef.value = MapCameraController(mapView)
            
            // Set initial click handler
            clickHandlerState.value = onMapClick
            
            NSLog("GoogleMaps: Map view delegate set and configured, initial click handler set")
            mapView
        },
        update = { mapView ->
            // Update click handler and ensure it's properly set
            clickHandlerState.value = onMapClick
            NSLog("GoogleMaps: UIKitView update called, onMapClick is ${if (onMapClick != null) "not null" else "null"}")
            NSLog("GoogleMaps: Delegate is ${if (delegateRef.value != null) "still retained" else "null"}")
            NSLog("GoogleMaps: MapView delegate is ${if (mapView.delegate != null) "set" else "null"}")
        }
    )
}