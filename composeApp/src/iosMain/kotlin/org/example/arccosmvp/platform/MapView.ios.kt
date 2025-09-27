package org.example.arccosmvp.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2D
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKAnnotationView
import platform.MapKit.MKCoordinateRegion
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation
import platform.UIKit.UIView

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapView(
    modifier: Modifier,
    locations: List<MapLocation>,
    centerLocation: MapLocation?,
    initialBounds: Pair<MapLocation, MapLocation>?,
    onMapClick: ((MapLocation) -> Unit)?
) {
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
                mapView.addAnnotation(annotation)
            }
            
            // Set map region
            when {
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
                    val latDelta = maxOf((maxLat - minLat) * 1.5, 0.01) // Add padding
                    val lngDelta = maxOf((maxLng - minLng) * 1.5, 0.01)
                    
                    val region = MKCoordinateRegion(
                        center = CLLocationCoordinate2DMake(centerLat, centerLng),
                        span = platform.MapKit.MKCoordinateSpan(
                            latitudeDelta = latDelta,
                            longitudeDelta = lngDelta
                        )
                    )
                    mapView.setRegion(region, animated = true)
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
                    mapView.setRegion(region, animated = true)
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
                    val latDelta = maxOf((maxLat - minLat) * 1.2, 0.01) // Add padding
                    val lngDelta = maxOf((maxLng - minLng) * 1.2, 0.01)
                    
                    val region = MKCoordinateRegion(
                        center = CLLocationCoordinate2DMake(centerLat, centerLng),
                        span = platform.MapKit.MKCoordinateSpan(
                            latitudeDelta = latDelta,
                            longitudeDelta = lngDelta
                        )
                    )
                    mapView.setRegion(region, animated = true)
                }
                else -> {
                    // Default to Denver, CO
                    val coordinate = CLLocationCoordinate2DMake(39.7392, -104.9903)
                    val region = MKCoordinateRegionMakeWithDistance(
                        coordinate,
                        10000.0, // 10km span
                        10000.0
                    )
                    mapView.setRegion(region, animated = true)
                }
            }
        }
    )
}