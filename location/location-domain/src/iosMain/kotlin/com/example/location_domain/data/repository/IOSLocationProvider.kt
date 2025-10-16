package com.example.location_domain.data.repository

import com.example.location_domain.domain.model.LocationResult
import platform.CoreLocation.*

class IOSLocationProvider : LocationProvider {
    
    private val locationManager = CLLocationManager()
    
    override suspend fun getCurrentLocation(): LocationResult {
        return try {
            // This is a simplified implementation
            // In a real app, you'd implement proper CLLocationManager delegates
            val location = locationManager.location
            
            if (location != null) {
                LocationResult.Error("TODO")
            } else {
                LocationResult.Error("Unable to get location")
            }
        } catch (e: Exception) {
            LocationResult.Error(e.message ?: "Unknown location error")
        }
    }
}