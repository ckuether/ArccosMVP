package com.example.location_domain.data.usecase

import com.example.location_domain.domain.usecase.CheckLocationPermissionUseCase
import platform.CoreLocation.*

class IOSCheckLocationPermissionUseCase : CheckLocationPermissionUseCase {
    
    override suspend fun invoke(): Boolean {
        val status = CLLocationManager.authorizationStatus()
        return status == kCLAuthorizationStatusAuthorizedWhenInUse || 
               status == kCLAuthorizationStatusAuthorizedAlways
    }
}