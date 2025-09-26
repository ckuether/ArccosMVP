package com.example.shared.data.usecase

import com.example.shared.domain.usecase.CheckLocationPermissionUseCase
import platform.CoreLocation.*

class IOSCheckLocationPermissionUseCase : CheckLocationPermissionUseCase {
    
    override suspend fun invoke(): Boolean {
        val status = CLLocationManager.authorizationStatus()
        return status == kCLAuthorizationStatusAuthorizedWhenInUse || 
               status == kCLAuthorizationStatusAuthorizedAlways
    }
}