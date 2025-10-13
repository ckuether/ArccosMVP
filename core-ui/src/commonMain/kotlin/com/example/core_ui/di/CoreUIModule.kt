package com.example.core_ui.di

import com.example.core_ui.projection.CalculateScreenPositionFromMapUseCase
import com.example.core_ui.projection.CalculateMapPositionFromScreenUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val coreUIModule = module {
    factoryOf(::CalculateScreenPositionFromMapUseCase)
    factoryOf(::CalculateMapPositionFromScreenUseCase)
}