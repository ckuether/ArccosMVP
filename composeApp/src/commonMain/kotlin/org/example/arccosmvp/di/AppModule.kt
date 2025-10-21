package org.example.arccosmvp.di

import com.example.shared.data.model.Course
import com.example.shared.data.repository.GolfCourseRepository
import com.example.shared.data.repository.ResourceReader
import com.example.shared.data.repository.UserRepository
import com.example.shared.data.repository.UserRepositoryImpl
import com.example.shared.usecase.LoadGolfCourseUseCase
import com.example.shared.usecase.LoadCurrentUserUseCase
import com.example.round_of_golf_domain.domain.usecase.SaveScoreCardUseCase
import com.example.shared.usecase.GetAllScoreCardsUseCase
import com.example.core_ui.platform.DrawableProvider
import com.example.core_ui.strings.StringResourcesManager
import com.example.round_of_golf_presentation.RoundOfGolfViewModel
import com.example.shared.data.model.Player
import org.example.arccosmvp.presentation.viewmodel.AppViewModel
import org.example.arccosmvp.utils.ComposeResourceReader
import org.example.arccosmvp.utils.AppDrawableProvider
import org.example.arccosmvp.strings.AppStringResourcesManager
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single<ResourceReader> { ComposeResourceReader() }
    single<GolfCourseRepository> { GolfCourseRepository(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<DrawableProvider> { AppDrawableProvider() }
    single<StringResourcesManager> { AppStringResourcesManager() }
    
    // UseCases
    factoryOf(::LoadGolfCourseUseCase)
    factoryOf(::LoadCurrentUserUseCase)
    factoryOf(::SaveScoreCardUseCase)
    factoryOf(::GetAllScoreCardsUseCase)
    
    factory { (course: Course, player: Player) ->
        RoundOfGolfViewModel(
            course = course,
            currentPlayer = player,
            locationTrackingService = get(),
            trackEventUseCase = get(),
            checkLocationPermissionUseCase = get(),
            requestLocationPermissionUseCase = get(),
            saveScoreCardUseCase = get(),
            logger = get()
        )
    }
    viewModelOf(::AppViewModel)
}