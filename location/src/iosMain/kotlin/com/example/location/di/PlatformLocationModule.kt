package com.example.location.di

import com.example.location.data.repository.IOSLocationProvider
import com.example.location.data.repository.LocationProvider
import com.example.location.data.usecase.IOSCheckLocationPermissionUseCase
import com.example.location.data.usecase.IOSRequestLocationPermissionUseCase
import com.example.location.domain.usecase.CheckLocationPermissionUseCase
import com.example.location.domain.usecase.RequestLocationPermissionUseCase
import com.example.location.platform.BackgroundLocationService
import com.example.location.platform.IOSBackgroundLocationService
import com.example.location.platform.IOSBackgroundLocationServiceWrapper
import com.example.shared.platform.createLogger
import org.koin.dsl.module

actual val platformLocationModule = module {
    single<LocationProvider> { IOSLocationProvider() }
    single<CheckLocationPermissionUseCase> { IOSCheckLocationPermissionUseCase() }
    single<RequestLocationPermissionUseCase> { IOSRequestLocationPermissionUseCase() }
    single<BackgroundLocationService> {
        // Create the iOS service directly without registering it as a separate dependency
        // to avoid KClass reflection issues with Objective-C subclasses
        IOSBackgroundLocationServiceWrapper(IOSBackgroundLocationService(createLogger()))
    }
}