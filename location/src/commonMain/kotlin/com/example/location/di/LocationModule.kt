package com.example.location.di

import com.example.location.data.repository.LocationManagerImpl
import com.example.location.data.service.LocationTrackingServiceImpl
import com.example.location.domain.repository.LocationManager
import com.example.location.domain.service.LocationTrackingService
import com.example.location.domain.usecase.TrackLocationUseCase
import com.example.location.domain.usecase.GetCurrentLocationUseCase
import com.example.location.domain.usecase.StartLocationTrackingUseCase
import com.example.location.domain.usecase.StopLocationTrackingUseCase
import com.example.shared.platform.Logger
import com.example.shared.platform.createLogger
import com.example.shared.usecase.CalculateMapCameraPositionUseCase
import com.example.location.domain.usecase.CalculateBearingUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val locationModule = module {
    // Platform Services
    single<Logger> { createLogger() }
    
    // Repository
    single<LocationManager> { LocationManagerImpl(get()) }
    
    // Use Cases
    factoryOf(::TrackLocationUseCase)
    factoryOf(::GetCurrentLocationUseCase)
    factoryOf(::StartLocationTrackingUseCase)
    factoryOf(::StopLocationTrackingUseCase)
    factoryOf(::CalculateBearingUseCase)
    factoryOf(::CalculateMapCameraPositionUseCase)
    
    // Services
    single<LocationTrackingService> {
        LocationTrackingServiceImpl(get())
    }
}

expect val platformLocationModule: Module