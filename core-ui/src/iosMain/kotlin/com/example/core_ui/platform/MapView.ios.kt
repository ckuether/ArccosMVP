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
import kotlinx.cinterop.cValue
import platform.CoreLocation.CLLocationCoordinate2D
import cocoapods.GoogleMaps.*
import kotlinx.cinterop.useContents
import platform.darwin.NSObject
import com.example.shared.usecase.CalculateMapCameraPositionUseCase
import com.example.shared.data.model.Location
import platform.UIKit.UIColor

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
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

    // Get the actual device scale factor for points-to-pixels conversion
    val deviceScale = remember { platform.UIKit.UIScreen.mainScreen.scale.toInt() }
    val calculateCameraPositionUseCase: CalculateMapCameraPositionUseCase = koinInject()
    val clickHandlerState = remember { mutableStateOf<((MapLocation) -> Unit)?>(null) }
    val mapViewRef = remember { mutableStateOf<GMSMapView?>(null) }
    val markersRef = remember { mutableStateOf<List<GMSMarker>>(emptyList()) }
    val targetMarkerRef = remember { mutableStateOf<GMSMarker?>(null) }
    val polylinesRef = remember { mutableStateOf<List<GMSPolyline>>(emptyList()) }
    val delegateRef = remember { mutableStateOf<GMSMapViewDelegateProtocol?>(null) }
    val cameraControllerRef = remember { mutableStateOf<MapCameraController?>(null) }
    val currentCameraPosition = remember { mutableStateOf<MapCameraPosition?>(null) }
    val mapSizeReported = remember { mutableStateOf(false) }

    // Track camera position changes
    LaunchedEffect(currentCameraPosition.value) {
        currentCameraPosition.value?.let { position ->
            onCameraPositionChanged?.invoke(position)
        }
    }

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
                        
                        // Clear existing polylines only (markers are now handled by Compose components)
                        polylinesRef.value.forEach { polyline ->
                            polyline.map = null
                        }
                    }
                }
            }
        }
    }
    
    // Update polylines when target location changes
    LaunchedEffect(targetLocation) {
        mapViewRef.value?.let { mapView ->
            if (currentHole != null && targetLocation != null) {
                // Clear existing polylines
                polylinesRef.value.forEach { polyline ->
                    polyline.map = null
                }
                
                val newPolylines = mutableListOf<GMSPolyline>()
                
                // Polyline from tee to target
                val teeToTargetPath = GMSMutablePath()
                teeToTargetPath.addCoordinate(cValue<CLLocationCoordinate2D> {
                    latitude = currentHole.teeLocation.lat
                    longitude = currentHole.teeLocation.long
                })
                teeToTargetPath.addCoordinate(cValue<CLLocationCoordinate2D> {
                    latitude = targetLocation.lat
                    longitude = targetLocation.long
                })
                
                val teeToTargetPolyline = GMSPolyline()
                teeToTargetPolyline.path = teeToTargetPath
                teeToTargetPolyline.strokeWidth = 1.0
                teeToTargetPolyline.strokeColor = UIColor.whiteColor
                teeToTargetPolyline.map = mapView
                newPolylines.add(teeToTargetPolyline)
                
                // Polyline from target to hole
                val targetToHolePath = GMSMutablePath()
                targetToHolePath.addCoordinate(cValue<CLLocationCoordinate2D> {
                    latitude = targetLocation.lat
                    longitude = targetLocation.long
                })
                targetToHolePath.addCoordinate(cValue<CLLocationCoordinate2D> {
                    latitude = currentHole.flagLocation.lat
                    longitude = currentHole.flagLocation.long
                })
                
                val targetToHolePolyline = GMSPolyline()
                targetToHolePolyline.path = targetToHolePath
                targetToHolePolyline.strokeWidth = 1.0
                targetToHolePolyline.strokeColor = UIColor.whiteColor
                targetToHolePolyline.map = mapView
                newPolylines.add(targetToHolePolyline)
                
                polylinesRef.value = newPolylines
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

                // Marker dragging is now handled by Compose components

                override fun mapView(
                    mapView: GMSMapView,
                    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
                    didChangeCameraPosition: GMSCameraPosition
                ) {
                    // Track camera position changes
                    didChangeCameraPosition.target.useContents {
                        val position = MapCameraPosition(
                            latitude = this.latitude,
                            longitude = this.longitude,
                            zoom = didChangeCameraPosition.zoom
                        )
                        currentCameraPosition.value = position
                        NSLog("GoogleMaps: Camera position updated - lat=${this.latitude}, lng=${this.longitude}, zoom=${didChangeCameraPosition.zoom}")
                    }
                    
                    // Report map size once when camera moves (indicating map is ready)
                    if (!mapSizeReported.value) {
                        val bounds = mapView.bounds
                        val width = bounds.useContents { size.width.toInt() } * deviceScale
                        val height = bounds.useContents { size.height.toInt() } * deviceScale
                        if (width > 0 && height > 0) {
                            onMapSizeChanged?.invoke(width, height)
                            mapSizeReported.value = true
                            NSLog("GoogleMaps: Map size reported from camera delegate: ${width}x${height}")
                        }
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

            // Trigger initial camera position callback (similar to Android's onMapLoaded)
            mapView.camera.target.useContents {
                val initialPosition = MapCameraPosition(
                    latitude = this.latitude,
                    longitude = this.longitude,
                    zoom = mapView.camera.zoom
                )
                currentCameraPosition.value = initialPosition
                NSLog("GoogleMaps: Initial camera position set - lat=${this.latitude}, lng=${this.longitude}, zoom=${mapView.camera.zoom}")
            }

            // Trigger onMapReady callback with the map instance
            onMapReady?.invoke(mapView)

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