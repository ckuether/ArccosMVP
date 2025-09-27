package org.example.arccosmvp.di

import org.example.arccosmvp.presentation.LocationTrackingViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::LocationTrackingViewModel)
}