package com.example.location.data.repository

import android.Manifest
import android.content.Context
import android.location.LocationManager
import androidx.annotation.RequiresPermission
import com.example.location.domain.model.LocationResult
import com.example.shared.data.event.Location

class AndroidLocationProvider(
    private val context: Context
) : LocationProvider {
    
    private val locationManager by lazy { 
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager 
    }
    
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    override suspend fun getCurrentLocation(): LocationResult {
        return try {
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
}