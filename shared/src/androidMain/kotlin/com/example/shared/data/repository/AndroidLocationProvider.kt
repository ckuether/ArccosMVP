package com.example.shared.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.example.shared.data.event.Location
import com.example.shared.domain.model.LocationResult

class AndroidLocationProvider(
    private val context: Context
) : LocationProvider {
    
    private val locationManager by lazy { 
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager 
    }
    
    override suspend fun getCurrentLocation(): LocationResult {
        return try {
            if (!hasLocationPermission()) {
                return LocationResult.PermissionDenied
            }
            
            if (!isLocationEnabled()) {
                return LocationResult.LocationDisabled
            }
            
            // This is a simplified implementation
            // In a real app, you'd use FusedLocationProviderClient or LocationManager
            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            
            if (lastKnownLocation != null) {
                LocationResult.Success(
                    Location(
                        lat = lastKnownLocation.latitude,
                        long = lastKnownLocation.longitude
                    )
                )
            } else {
                LocationResult.Error("Unable to get location")
            }
        } catch (e: Exception) {
            LocationResult.Error(e.message ?: "Unknown location error")
        }
    }
    
    override suspend fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    override suspend fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
               locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}