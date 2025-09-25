package com.example.shared.di

import com.example.shared.data.repository.LocationProvider
import com.example.shared.data.repository.LocationRepositoryImpl
import com.example.shared.data.service.LocationTrackingServiceImpl
import com.example.shared.domain.repository.LocationRepository
import com.example.shared.domain.service.LocationTrackingService
import com.example.shared.domain.usecase.TrackLocationUseCase
import com.example.shared.domain.usecase.GetLocationUseCase
import com.example.shared.domain.usecase.CheckLocationPermissionUseCase
import kotlinx.coroutines.CoroutineScope
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val locationModule = module {
    // Repository
    single<LocationRepository> { LocationRepositoryImpl(get()) }
    
    // Use Cases
    factoryOf(::TrackLocationUseCase)
    factoryOf(::GetLocationUseCase)
    
    // Services
    single<LocationTrackingService> { 
        LocationTrackingServiceImpl(get(), get<CoroutineScope>()) 
    }
}

expect val platformLocationModule: org.koin.core.module.Module