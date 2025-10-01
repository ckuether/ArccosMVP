package org.example.arccosmvp.di

import com.example.shared.data.repository.GolfCourseRepository
import com.example.shared.data.repository.ResourceReader
import com.example.shared.data.repository.UserRepository
import com.example.shared.data.repository.UserRepositoryImpl
import org.example.arccosmvp.presentation.viewmodel.RoundOfGolfViewModel
import org.example.arccosmvp.utils.ComposeResourceReader
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single<ResourceReader> { ComposeResourceReader() }
    single<GolfCourseRepository> { GolfCourseRepository(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    viewModelOf(::RoundOfGolfViewModel)
}