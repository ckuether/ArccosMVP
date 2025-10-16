package com.example.location_domain.di

import com.example.location_domain.data.repository.AndroidLocationProvider
import com.example.location_domain.data.repository.LocationProvider
import com.example.location_domain.data.usecase.AndroidCheckLocationPermissionUseCase
import com.example.location_domain.data.usecase.AndroidRequestLocationPermissionUseCase
import com.example.location_domain.domain.usecase.CheckLocationPermissionUseCase
import com.example.location_domain.domain.usecase.RequestLocationPermissionUseCase
import com.example.location_domain.platform.AndroidBackgroundLocationService
import com.example.location_domain.platform.BackgroundLocationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformLocationModule = module {
    single<LocationProvider> { AndroidLocationProvider(androidContext()) }
    single<CheckLocationPermissionUseCase> { AndroidCheckLocationPermissionUseCase(androidContext()) }
    single<RequestLocationPermissionUseCase> {
        AndroidRequestLocationPermissionUseCase(
            androidContext(),
            get()
        )
    }
    single<BackgroundLocationService> { AndroidBackgroundLocationService(androidContext(), get()) }
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.IO) }
}