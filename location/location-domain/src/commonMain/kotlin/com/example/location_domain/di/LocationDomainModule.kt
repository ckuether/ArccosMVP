package com.example.location_domain.di

import com.example.location_domain.data.repository.LocationManagerImpl
import com.example.location_domain.data.service.LocationTrackingServiceImpl
import com.example.location_domain.domain.repository.LocationManager
import com.example.location_domain.domain.service.LocationTrackingService
import com.example.location_domain.domain.usecase.GetCurrentLocationUseCase
import com.example.shared.platform.Logger
import com.example.shared.platform.createLogger
import com.example.location_domain.domain.usecase.CalculateMapCameraPositionUseCase
import com.example.location_domain.domain.usecase.CalculateBearingUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val locationDomainModule = module {
    // Platform Services
    single<Logger> { createLogger() }
    
    // Repository
    single<LocationManager> { LocationManagerImpl(get()) }
    
    // Use Cases
    factoryOf(::GetCurrentLocationUseCase)
    factoryOf(::CalculateBearingUseCase)
    factoryOf(::CalculateMapCameraPositionUseCase)
    
    // Services
    single<LocationTrackingService> {
        LocationTrackingServiceImpl(get())
    }
}

expect val platformLocationModule: Module