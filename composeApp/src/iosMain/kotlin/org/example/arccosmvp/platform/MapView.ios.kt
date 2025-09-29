package org.example.arccosmvp.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import com.example.shared.data.model.Hole
import com.example.shared.domain.usecase.CalculateBearingUseCase
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKCoordinateRegionMake
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKCoordinateSpanMake
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation
import platform.MapKit.MKMapCamera

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapView(
    modifier: Modifier,
    locations: List<MapLocation>,
    centerLocation: MapLocation?,
    initialBounds: Pair<MapLocation, MapLocation>?,
    currentHole: Hole?,
    onMapClick: ((MapLocation) -> Unit)?
) {
    // Create use case for bearing calculation
    val calculateBearingUseCase = remember { CalculateBearingUseCase() }
    UIKitView(
        modifier = modifier,
        factory = {
            val mapView = MKMapView()
            
            // Configure map settings
            mapView.setShowsUserLocation(true)
            mapView.setZoomEnabled(true)
            mapView.setScrollEnabled(true)
            mapView.setRotateEnabled(true)
            
            mapView
        },
        update = { mapView ->
            // Clear existing annotations
            mapView.removeAnnotations(mapView.annotations)
            
            // Add new annotations for locations
            locations.forEach { location ->
                val annotation = MKPointAnnotation()
                annotation.setCoordinate(
                    CLLocationCoordinate2DMake(
                        location.latitude,
                        location.longitude
                    )
                )
                annotation.setTitle(location.title ?: "Location")
                
                // Set subtitle for golf markers
                annotation.setSubtitle(when (location.markerType) {
                    MarkerType.GOLF_BALL -> "⛳ Tee Area"
                    MarkerType.GOLF_FLAG -> "🏌️ Pin/Hole"
                    MarkerType.DEFAULT -> null
                })
                
                mapView.addAnnotation(annotation)
            }
            
            // Set map region
            when {
                currentHole != null -> {
                    // Use hole bounds with bearing calculation like Android
                    val startLat = currentHole.teeLocation.lat
                    val startLng = currentHole.teeLocation.long
                    val endLat = currentHole.flagLocation.lat
                    val endLng = currentHole.flagLocation.long
                    
                    val minLat = minOf(startLat, endLat)
                    val maxLat = maxOf(startLat, endLat)
                    val minLng = minOf(startLng, endLng)
                    val maxLng = maxOf(startLng, endLng)
                    
                    val centerLat = (minLat + maxLat) / 2
                    val centerLng = (minLng + maxLng) / 2
                    // Much tighter zoom - only add small padding and cap maximum zoom out
                    val latDelta = minOf(maxOf((maxLat - minLat) * 1.05, 0.002), 0.005) 
                    val lngDelta = minOf(maxOf((maxLng - minLng) * 1.05, 0.002), 0.005)
                    
                    // Calculate bearing for orientation
                    val bearing = calculateBearingUseCase(currentHole.teeLocation, currentHole.flagLocation)
                    
                    // First set the region
                    val center = CLLocationCoordinate2DMake(centerLat, centerLng)
                    val span = MKCoordinateSpanMake(latDelta, lngDelta)
                    val region = MKCoordinateRegionMake(center, span)
                    mapView.setRegion(region, true)
                    
                    // Then set the camera with bearing (rotation)
                    val camera = MKMapCamera()
                    camera.setCenterCoordinate(center)
                    camera.setHeading(bearing) // Set the bearing/rotation
                    // Calculate altitude for the zoom level - approximate conversion
                    val altitude = (latDelta * 111000.0 * 2.0) // Convert lat delta to meters and set altitude
                    camera.setAltitude(altitude)
                    mapView.setCamera(camera, true)
                }
                initialBounds != null -> {
                    // Use initial bounds (highest priority)
                    val startLat = initialBounds.first.latitude
                    val startLng = initialBounds.first.longitude
                    val endLat = initialBounds.second.latitude
                    val endLng = initialBounds.second.longitude
                    
                    val minLat = minOf(startLat, endLat)
                    val maxLat = maxOf(startLat, endLat)
                    val minLng = minOf(startLng, endLng)
                    val maxLng = maxOf(startLng, endLng)
                    
                    val centerLat = (minLat + maxLat) / 2
                    val centerLng = (minLng + maxLng) / 2
                    // Much tighter zoom - only add small padding and cap maximum zoom out
                    val latDelta = minOf(maxOf((maxLat - minLat) * 1.05, 0.002), 0.005) 
                    val lngDelta = minOf(maxOf((maxLng - minLng) * 1.05, 0.002), 0.005)
                    
                    val center = CLLocationCoordinate2DMake(centerLat, centerLng)
                    val span = MKCoordinateSpanMake(latDelta, lngDelta)
                    val region = MKCoordinateRegionMake(center, span)
                    mapView.setRegion(region, true)
                }
                centerLocation != null -> {
                    val coordinate = CLLocationCoordinate2DMake(
                        centerLocation.latitude,
                        centerLocation.longitude
                    )
                    val region = MKCoordinateRegionMakeWithDistance(
                        coordinate,
                        1000.0, // 1km span
                        1000.0
                    )
                    mapView.setRegion(region, true)
                }
                locations.isNotEmpty() -> {
                    // Calculate bounds for all locations
                    val firstLocation = locations.first()
                    var minLat = firstLocation.latitude
                    var maxLat = firstLocation.latitude
                    var minLng = firstLocation.longitude
                    var maxLng = firstLocation.longitude
                    
                    locations.forEach { location ->
                        minLat = minOf(minLat, location.latitude)
                        maxLat = maxOf(maxLat, location.latitude)
                        minLng = minOf(minLng, location.longitude)
                        maxLng = maxOf(maxLng, location.longitude)
                    }
                    
                    val centerLat = (minLat + maxLat) / 2
                    val centerLng = (minLng + maxLng) / 2
                    // Much tighter zoom - only add small padding and cap maximum zoom out
                    val latDelta = minOf(maxOf((maxLat - minLat) * 1.05, 0.002), 0.005) 
                    val lngDelta = minOf(maxOf((maxLng - minLng) * 1.05, 0.002), 0.005)
                    
                    val center = CLLocationCoordinate2DMake(centerLat, centerLng)
                    val span = MKCoordinateSpanMake(latDelta, lngDelta)
                    val region = MKCoordinateRegionMake(center, span)
                    mapView.setRegion(region, true)
                }
                else -> {
                    // Default to Denver, CO
                    val coordinate = CLLocationCoordinate2DMake(39.7392, -104.9903)
                    val region = MKCoordinateRegionMakeWithDistance(
                        coordinate,
                        10000.0, // 10km span
                        10000.0
                    )
                    mapView.setRegion(region, true)
                }
            }
        }
    )
}