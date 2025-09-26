package com.example.location.data.usecase

import com.example.location.domain.usecase.CheckLocationPermissionUseCase
import platform.CoreLocation.*

class IOSCheckLocationPermissionUseCase : CheckLocationPermissionUseCase {
    
    override suspend fun invoke(): Boolean {
        val status = CLLocationManager.authorizationStatus()
        return status == kCLAuthorizationStatusAuthorizedWhenInUse || 
               status == kCLAuthorizationStatusAuthorizedAlways
    }
}