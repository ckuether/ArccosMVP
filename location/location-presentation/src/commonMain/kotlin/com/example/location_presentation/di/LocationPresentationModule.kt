package com.example.location_presentation.di

import com.example.location_domain.domain.service.MapProjectionService
import com.example.location_presentation.service.PlatformMapProjectionService
import org.koin.dsl.module

val locationPresentationModule = module {
    
    // Platform Services
    single<MapProjectionService> { PlatformMapProjectionService() }
}