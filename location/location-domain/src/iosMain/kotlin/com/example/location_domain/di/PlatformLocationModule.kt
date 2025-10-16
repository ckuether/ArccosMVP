package com.example.location_domain.di

import com.example.location_domain.data.repository.IOSLocationProvider
import com.example.location_domain.data.repository.LocationProvider
import com.example.location_domain.data.usecase.IOSCheckLocationPermissionUseCase
import com.example.location_domain.data.usecase.IOSRequestLocationPermissionUseCase
import com.example.location_domain.domain.usecase.CheckLocationPermissionUseCase
import com.example.location_domain.domain.usecase.RequestLocationPermissionUseCase
import com.example.location_domain.platform.BackgroundLocationService
import com.example.location_domain.platform.IOSBackgroundLocationService
import com.example.location_domain.platform.IOSBackgroundLocationServiceWrapper
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