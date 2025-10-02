package com.example.location.platform

import com.example.shared.data.model.event.InPlayEvent
import com.example.shared.data.model.Location
import com.example.shared.platform.Logger
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import platform.CoreLocation.*
import platform.Foundation.NSTimer
import platform.darwin.NSObject

class IOSBackgroundLocationService(
    private val logger: Logger
): NSObject(), CLLocationManagerDelegateProtocol {

    private val TAG = "IOSBackgroundLocationService"
    
    private val _isBackgroundTrackingActive = MutableStateFlow(false)
    val isBackgroundTrackingActive: StateFlow<Boolean> = _isBackgroundTrackingActive.asStateFlow()
    
    private val locationManager = CLLocationManager().apply {
        delegate = this@IOSBackgroundLocationService
        desiredAccuracy = kCLLocationAccuracyBest
        distanceFilter = 10.0 // meters
    }
    
    private var locationCallback: ((InPlayEvent.LocationUpdated) -> Unit)? = null
    private var updateTimer: NSTimer? = null
    
    fun startBackgroundLocationTracking(intervalMs: Long): Flow<InPlayEvent.LocationUpdated> {
        logger.info(TAG, "startBackgroundLocationTracking called with intervalMs: $intervalMs")
        return callbackFlow {
            if (_isBackgroundTrackingActive.value) {
                logger.warn(TAG, "Background tracking already active, closing flow")
                close()
                return@callbackFlow
            }
            
            logger.info(TAG, "Starting background location tracking")
            _isBackgroundTrackingActive.value = true
            locationCallback = { locationEvent ->
                trySend(locationEvent)
            }
            
            // Request background location permission
            logger.info(TAG, "Checking location authorization status: ${locationManager.authorizationStatus}")
            when (locationManager.authorizationStatus) {
                kCLAuthorizationStatusNotDetermined -> {
                    logger.info(TAG, "Requesting always authorization")
                    locationManager.requestAlwaysAuthorization()
                }
                kCLAuthorizationStatusAuthorizedAlways,
                kCLAuthorizationStatusAuthorizedWhenInUse -> {
                    logger.info(TAG, "Location authorized, starting updates")
                    startLocationUpdates()
                }
                else -> {
                    logger.error(TAG, "Location permission denied")
                    _isBackgroundTrackingActive.value = false
                    close(Exception("Location permission denied"))
                }
            }
            
            awaitClose {
                logger.info(TAG, "Stopping background location tracking")
                stopLocationUpdates()
                locationCallback = null
                _isBackgroundTrackingActive.value = false
            }
        }
    }
    
    fun stopBackgroundLocationTracking() {
        logger.info(TAG, "stopBackgroundLocationTracking called")
        stopLocationUpdates()
        _isBackgroundTrackingActive.value = false
        locationCallback = null
        logger.info(TAG, "Background location tracking stopped")
    }
    
    private fun startLocationUpdates() {
        logger.info(TAG, "Starting location updates")
        locationManager.allowsBackgroundLocationUpdates = true
        locationManager.pausesLocationUpdatesAutomatically = false
        locationManager.startUpdatingLocation()
        locationManager.startMonitoringSignificantLocationChanges()
        logger.info(TAG, "Location updates started")
    }
    
    private fun stopLocationUpdates() {
        logger.info(TAG, "Stopping location updates")
        updateTimer?.invalidate()
        updateTimer = null
        locationManager.stopUpdatingLocation()
        locationManager.stopMonitoringSignificantLocationChanges()
        locationManager.allowsBackgroundLocationUpdates = false
        logger.info(TAG, "Location updates stopped")
    }


    @OptIn(ExperimentalForeignApi::class)
    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
        val locations = didUpdateLocations as List<CLLocation>
        logger.debug(TAG, "Received ${locations.size} location updates")
        locations.lastOrNull()?.let { location ->
            val coordinate = location.coordinate
            val lat = coordinate.useContents { latitude }
            val lng = coordinate.useContents { longitude }
            logger.debug(TAG, "Location received: lat=$lat, long=$lng")

            logger.debug(TAG, "Location received: lat=$lat, long=$lng")
            val locationEvent = InPlayEvent.LocationUpdated(
                Location(
                    lat = lat,
                    long = lng
                )
            )
            locationCallback?.invoke(locationEvent)
        }
    }
    
    override fun locationManager(manager: CLLocationManager, didFailWithError: platform.Foundation.NSError) {
        logger.error(TAG, "Location manager failed with error: ${didFailWithError.localizedDescription}")
        _isBackgroundTrackingActive.value = false
    }
    
    override fun locationManager(manager: CLLocationManager, didChangeAuthorizationStatus: CLAuthorizationStatus) {
        logger.info(TAG, "Authorization status changed to: $didChangeAuthorizationStatus")
        when (didChangeAuthorizationStatus) {
            kCLAuthorizationStatusAuthorizedAlways,
            kCLAuthorizationStatusAuthorizedWhenInUse -> {
                logger.info(TAG, "Location authorized")
                if (_isBackgroundTrackingActive.value) {
                    startLocationUpdates()
                }
            }
            else -> {
                logger.warn(TAG, "Location not authorized, stopping updates")
                stopLocationUpdates()
                _isBackgroundTrackingActive.value = false
            }
        }
    }
}


class IOSBackgroundLocationServiceWrapper(
    private val iosService: IOSBackgroundLocationService
): BackgroundLocationService{
    override val isBackgroundTrackingActive: Flow<Boolean>
        get() = iosService.isBackgroundTrackingActive

    override fun startBackgroundLocationTracking(intervalMs: Long): Flow<InPlayEvent.LocationUpdated> {
        return iosService.startBackgroundLocationTracking(intervalMs)
    }

    override fun stopBackgroundLocationTracking() {
        iosService.stopBackgroundLocationTracking()
    }
}