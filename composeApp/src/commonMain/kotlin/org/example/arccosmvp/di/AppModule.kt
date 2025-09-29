package org.example.arccosmvp.di

import com.example.shared.data.repository.GolfCourseRepository
import org.example.arccosmvp.presentation.LocationTrackingViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::GolfCourseRepository)
    viewModelOf(::LocationTrackingViewModel)
}