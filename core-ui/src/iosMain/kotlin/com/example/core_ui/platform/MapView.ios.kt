package com.example.core_ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import platform.Foundation.NSLog
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import com.example.shared.data.model.Hole
import com.example.shared.domain.usecase.CalculateBearingUseCase
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CValue
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.CoreLocation.CLLocationCoordinate2D
import cocoapods.GoogleMaps.*
import kotlinx.cinterop.useContents
import platform.darwin.NSObject
import kotlinx.cinterop.ObjCSignatureOverride

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
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
    val calculateBearingUseCase = remember { CalculateBearingUseCase() }
    val clickHandlerState = remember { mutableStateOf<((MapLocation) -> Unit)?>(null) }
    val markersRef = remember { mutableStateOf<List<GMSMarker>>(emptyList()) }
    
    UIKitView(
        modifier = modifier,
        factory = {
            // Determine initial camera position
            val initialCamera = when {
                currentHole != null -> {
                    val centerLat = (currentHole.teeLocation.lat + currentHole.flagLocation.lat) / 2
                    val centerLng = (currentHole.teeLocation.long + currentHole.flagLocation.long) / 2
                    GMSCameraPosition.cameraWithLatitude(centerLat, centerLng, 16.0f)
                }
                initialBounds != null -> {
                    val centerLat = (initialBounds.first.latitude + initialBounds.second.latitude) / 2
                    val centerLng = (initialBounds.first.longitude + initialBounds.second.longitude) / 2
                    GMSCameraPosition.cameraWithLatitude(centerLat, centerLng, 16.0f)
                }
                centerLocation != null -> {
                    GMSCameraPosition.cameraWithLatitude(centerLocation.latitude, centerLocation.longitude, 15.0f)
                }
                userLocations.isNotEmpty() -> {
                    val firstLocation = userLocations.first()
                    GMSCameraPosition.cameraWithLatitude(firstLocation.latitude, firstLocation.longitude, 15.0f)
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
            mapView.setMyLocationEnabled(hasLocationPermission)
            mapView.settings.setZoomGestures(true)
            mapView.settings.setScrollGestures(true)
            mapView.settings.setRotateGestures(true)
            mapView.settings.setTiltGestures(true)

            // Create and set delegate for map interactions
            val mapDelegate = object : NSObject(), GMSMapViewDelegateProtocol {
                @ObjCSignatureOverride
                override fun mapView(mapView: GMSMapView, didTapAtCoordinate: CValue<CLLocationCoordinate2D>) {
                    NSLog("GoogleMaps: Map tapped")

                    clickHandlerState.value?.let { clickHandler ->
                        didTapAtCoordinate.useContents {
                            NSLog("GoogleMaps: Converting coordinate to MapLocation: lat=${this.latitude}, lng=${this.longitude}")
                            clickHandler(MapLocation(
                                latitude = this.latitude,
                                longitude = this.longitude
                            ))
                        }
                    } ?: run {
                        NSLog("GoogleMaps: No click handler available")
                    }
                }
            }

            mapView.setDelegate(mapDelegate)
            mapView
        },
        update = { mapView ->
            // Update click handler
            clickHandlerState.value = onMapClick

            // Clear existing markers
            markersRef.value.forEach { marker ->
                marker.setMap(null)
            }

            // Add new markers for user locations
            val newMarkers = userLocations.map { location ->
                val marker = GMSMarker()
                val coordinate = CLLocationCoordinate2DMake(location.latitude, location.longitude)
                marker.setPosition(coordinate)
                marker.setTitle(location.title ?: "Location")

                // Set snippet based on marker type
                marker.setSnippet(when (location.markerType) {
                    MarkerType.GOLF_BALL -> "� Tee Area"
                    MarkerType.GOLF_FLAG -> "<� Pin/Hole"
                    MarkerType.TARGET_CIRCLE -> "<� Target Shot"
                    MarkerType.DEFAULT -> null
                })

                marker.setMap(mapView)
                marker
            }

            markersRef.value = newMarkers

            // Update camera position based on priority
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
                }
                initialBounds != null -> {
                    val startCoord = CLLocationCoordinate2DMake(initialBounds.first.latitude, initialBounds.first.longitude)
                    val endCoord = CLLocationCoordinate2DMake(initialBounds.second.latitude, initialBounds.second.longitude)

                    val bounds = GMSCoordinateBounds()
                    bounds.includingCoordinate(startCoord)
                    bounds.includingCoordinate(endCoord)

                    val update = GMSCameraUpdate.fitBounds(bounds, 50.0) // 50 points padding
                    mapView.animateWithCameraUpdate(update)
                }
                centerLocation != null -> {
                    val camera = GMSCameraPosition.cameraWithLatitude(
                        centerLocation.latitude,
                        centerLocation.longitude,
                        15.0f
                    )
                    mapView.animateToCameraPosition(camera)
                }
                userLocations.isNotEmpty() -> {
                    if (userLocations.size == 1) {
                        val location = userLocations.first()
                        val camera = GMSCameraPosition.cameraWithLatitude(
                            location.latitude,
                            location.longitude,
                            15.0f
                        )
                        mapView.animateToCameraPosition(camera)
                    } else {
                        val bounds = GMSCoordinateBounds()
                        userLocations.forEach { location ->
                            bounds.includingCoordinate(CLLocationCoordinate2DMake(location.latitude, location.longitude))
                        }
                        val update = GMSCameraUpdate.fitBounds(bounds, 50.0)
                        mapView.animateWithCameraUpdate(update)
                    }
                }
                else -> {
                    // Default to Denver, CO
                    val camera = GMSCameraPosition.cameraWithLatitude(39.7392, -104.9903, 10.0f)
                    mapView.animateToCameraPosition(camera)
                }
            }
        }
    )
}