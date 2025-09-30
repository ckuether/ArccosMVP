package com.example.location.di

import com.example.location.data.repository.LocationRepositoryImpl
import com.example.location.data.service.LocationTrackingServiceImpl
import com.example.location.domain.repository.LocationRepository
import com.example.location.domain.service.LocationTrackingService
import com.example.location.domain.usecase.TrackLocationUseCase
import com.example.location.domain.usecase.GetLocationUseCase
import com.example.location.domain.usecase.StartLocationTrackingUseCase
import com.example.location.domain.usecase.StopLocationTrackingUseCase
import com.example.location.domain.usecase.SaveLocationEventUseCase
import com.example.location.domain.usecase.GetLocationEventsUseCase
import com.example.location.domain.usecase.ClearLocationEventsUseCase
import com.example.shared.platform.Logger
import com.example.shared.platform.createLogger
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
    factoryOf(::StartLocationTrackingUseCase)
    factoryOf(::StopLocationTrackingUseCase)
    factoryOf(::SaveLocationEventUseCase)
    factoryOf(::GetLocationEventsUseCase)
    factoryOf(::ClearLocationEventsUseCase)
    
    // Services
    single<LocationTrackingService> {
        LocationTrackingServiceImpl(get())
    }
}

expect val platformLocationModule: Module