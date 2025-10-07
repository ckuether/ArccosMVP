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
import com.example.shared.data.model.Location
import com.example.shared.domain.usecase.CalculateBearingUseCase
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CValue
import platform.CoreLocation.CLLocationCoordinate2D
import cocoapods.GoogleMaps.*
import kotlinx.cinterop.useContents
import platform.darwin.NSObject
import org.koin.compose.koinInject
import com.example.core_ui.components.createGolfMapMarker

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Composable
actual fun MapView(
    modifier: Modifier,
    currentHole: Hole?,
    hasLocationPermission: Boolean,
    onMapClick: ((MapLocation) -> Unit)?
) {
    val calculateBearingUseCase = remember { CalculateBearingUseCase() }
    val clickHandlerState = remember { mutableStateOf<((MapLocation) -> Unit)?>(null) }
    val mapViewRef = remember { mutableStateOf<GMSMapView?>(null) }
    val markersRef = remember { mutableStateOf<List<GMSMarker>>(emptyList()) }

    // Handle camera updates when hole changes
    LaunchedEffect(currentHole) {
        mapViewRef.value?.let { mapView ->
            NSLog("GoogleMaps: LaunchedEffect updating camera position")
            when {
                currentHole != null -> {
                    val startLat = currentHole.teeLocation.lat
                    val startLng = currentHole.teeLocation.long
                    val endLat = currentHole.flagLocation.lat
                    val endLng = currentHole.flagLocation.long

                    val centerLat = (startLat + endLat) / 2
                    val centerLng = (startLng + endLng) / 2

                    // Calculate bearing for orientation
                    val bearing = calculateBearingUseCase(currentHole.teeLocation, currentHole.flagLocation)

                    // Create camera with bearing
                    val camera = GMSCameraPosition.cameraWithLatitude(
                        centerLat,
                        centerLng,
                        16.0f,
                        bearing.toDouble(),
                        0.0
                    )

                    mapView.animateToCameraPosition(camera)
                    
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
    
    UIKitView(
        modifier = modifier,
        interactive = true,
        factory = {
            // Determine initial camera position
            val initialCamera = when {
                currentHole != null -> {
                    val centerLat = (currentHole.teeLocation.lat + currentHole.flagLocation.lat) / 2
                    val centerLng = (currentHole.teeLocation.long + currentHole.flagLocation.long) / 2
                    GMSCameraPosition.cameraWithLatitude(centerLat, centerLng, 16.0f)
                }
                else -> {
                    // Default to Denver, CO
                    GMSCameraPosition.cameraWithLatitude(39.7392, -104.9903, 10.0f)
                }
            }

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
                    NSLog("GoogleMaps: didTapAtCoordinate called - map was tapped!")

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

            mapView.setDelegate(mapDelegate)
            mapViewRef.value = mapView
            
            // Set initial click handler
            clickHandlerState.value = onMapClick
            
            NSLog("GoogleMaps: Map view delegate set and configured, initial click handler set")
            mapView
        },
        update = { mapView ->
            // Update click handler and ensure it's properly set
            clickHandlerState.value = onMapClick
            NSLog("GoogleMaps: Click handler updated, onMapClick is ${if (onMapClick != null) "not null" else "null"}")
        }
    )
}