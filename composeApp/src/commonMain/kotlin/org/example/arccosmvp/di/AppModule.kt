package org.example.arccosmvp.di

import com.example.shared.data.repository.GolfCourseRepository
import com.example.shared.data.repository.ResourceReader
import org.example.arccosmvp.presentation.LocationTrackingViewModel
import org.example.arccosmvp.utils.ComposeResourceReader
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single<ResourceReader> { ComposeResourceReader() }
    single<GolfCourseRepository> { GolfCourseRepository(get()) }
    viewModelOf(::LocationTrackingViewModel)
}