package com.example.location.di

import com.example.location.data.repository.LocationRepositoryImpl
import com.example.location.data.service.LocationTrackingServiceImpl
import com.example.location.domain.repository.LocationRepository
import com.example.location.domain.service.LocationTrackingService
import com.example.location.domain.usecase.TrackLocationUseCase
import com.example.location.domain.usecase.GetLocationUseCase
import com.example.shared.platform.Logger
import com.example.shared.platform.createLogger
import kotlinx.coroutines.CoroutineScope
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val locationModule = module {
    // Platform Services
    single<Logger> { createLogger() }
    
    // Repository
    single<LocationRepository> { LocationRepositoryImpl(get(), get()) }
    
    // Use Cases
    factoryOf(::TrackLocationUseCase)
    factoryOf(::GetLocationUseCase)
    
    // Services
    single<LocationTrackingService> {
        LocationTrackingServiceImpl(get(), get<CoroutineScope>())
    }
}

expect val platformLocationModule: Module