package org.example.arccosmvp.di

import org.example.arccosmvp.presentation.LocationTrackingViewModel
import org.example.arccosmvp.data.HoleRepository
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::HoleRepository)
    viewModelOf(::LocationTrackingViewModel)
}