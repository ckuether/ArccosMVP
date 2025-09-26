package com.example.shared.di

import com.example.shared.data.repository.IOSLocationProvider
import com.example.shared.data.repository.LocationProvider
import com.example.shared.data.usecase.IOSCheckLocationPermissionUseCase
import com.example.shared.data.usecase.IOSRequestLocationPermissionUseCase
import com.example.shared.domain.usecase.CheckLocationPermissionUseCase
import com.example.shared.domain.usecase.RequestLocationPermissionUseCase
import org.koin.dsl.module

actual val platformLocationModule = module {
    single<LocationProvider> { IOSLocationProvider() }
    single<CheckLocationPermissionUseCase> { IOSCheckLocationPermissionUseCase() }
    single<RequestLocationPermissionUseCase> { IOSRequestLocationPermissionUseCase() }
}