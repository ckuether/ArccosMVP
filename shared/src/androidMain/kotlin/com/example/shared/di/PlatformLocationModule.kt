package com.example.shared.di

import android.content.Context
import com.example.shared.data.repository.AndroidLocationProvider
import com.example.shared.data.repository.LocationProvider
import com.example.shared.data.usecase.AndroidCheckLocationPermissionUseCase
import com.example.shared.domain.usecase.CheckLocationPermissionUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformLocationModule = module {
    single<LocationProvider> { AndroidLocationProvider(androidContext()) }
    single<CheckLocationPermissionUseCase> { AndroidCheckLocationPermissionUseCase(androidContext()) }
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.IO) }
}