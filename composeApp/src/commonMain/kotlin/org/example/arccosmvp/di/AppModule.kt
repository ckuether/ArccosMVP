package org.example.arccosmvp.di

import org.example.arccosmvp.presentation.LocationViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::LocationViewModel)
}