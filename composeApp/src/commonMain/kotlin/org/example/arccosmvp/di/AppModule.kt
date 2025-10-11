package org.example.arccosmvp.di

import com.example.shared.data.model.Course
import com.example.shared.data.repository.GolfCourseRepository
import com.example.shared.data.repository.ResourceReader
import com.example.shared.data.repository.UserRepository
import com.example.shared.data.repository.UserRepositoryImpl
import com.example.shared.domain.usecase.LoadGolfCourseUseCase
import com.example.shared.domain.usecase.LoadCurrentUserUseCase
import com.example.shared.domain.usecase.SaveScoreCardUseCase
import com.example.shared.domain.usecase.GetAllScoreCardsUseCase
import org.example.arccosmvp.presentation.viewmodel.RoundOfGolfViewModel
import org.example.arccosmvp.presentation.viewmodel.AppViewModel
import org.example.arccosmvp.utils.ComposeResourceReader
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single<ResourceReader> { ComposeResourceReader() }
    single<GolfCourseRepository> { GolfCourseRepository(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    
    // UseCases
    factoryOf(::LoadGolfCourseUseCase)
    factoryOf(::LoadCurrentUserUseCase)
    factoryOf(::SaveScoreCardUseCase)
    factoryOf(::GetAllScoreCardsUseCase)
    
    factory { (course: Course) ->
        RoundOfGolfViewModel(
            course = course,
            locationTrackingService = get(),
            saveLocationEventUseCase = get(),
            checkLocationPermissionUseCase = get(),
            requestLocationPermissionUseCase = get(),
            saveScoreCardUseCase = get(),
            logger = get()
        )
    }
    viewModelOf(::AppViewModel)
}