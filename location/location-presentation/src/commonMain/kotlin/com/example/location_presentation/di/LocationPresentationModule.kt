package com.example.location_presentation.di

import com.example.location_presentation.projection.CalculateMapPositionFromScreenUseCase
import com.example.location_presentation.projection.CalculateScreenPositionFromMapUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val locationPresentationModule = module {

    factoryOf(::CalculateScreenPositionFromMapUseCase)
    factoryOf(::CalculateMapPositionFromScreenUseCase)

}