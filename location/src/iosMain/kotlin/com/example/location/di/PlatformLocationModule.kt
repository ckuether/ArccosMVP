package com.example.location.di

import com.example.location.data.repository.IOSLocationProvider
import com.example.location.data.repository.LocationProvider
import com.example.location.data.usecase.IOSCheckLocationPermissionUseCase
import com.example.location.data.usecase.IOSRequestLocationPermissionUseCase
import com.example.location.domain.usecase.CheckLocationPermissionUseCase
import com.example.location.domain.usecase.RequestLocationPermissionUseCase
import org.koin.dsl.module

actual val platformLocationModule = module {
    single<LocationProvider> { IOSLocationProvider() }
    single<CheckLocationPermissionUseCase> { IOSCheckLocationPermissionUseCase() }
    single<RequestLocationPermissionUseCase> { IOSRequestLocationPermissionUseCase() }
}