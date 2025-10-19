package com.example.round_of_golf_domain.di

import com.example.round_of_golf_domain.domain.usecase.DeleteRoundEventsUseCase
import com.example.round_of_golf_domain.domain.usecase.GetRoundEventsUseCase
import com.example.round_of_golf_domain.domain.usecase.GetRoundStatisticsUseCase
import com.example.round_of_golf_domain.domain.usecase.ReplayRoundOfGolfUseCase
import com.example.round_of_golf_domain.domain.usecase.TrackMultipleRoundEventsUseCase
import com.example.round_of_golf_domain.domain.usecase.TrackSingleRoundEventUseCase
import org.koin.dsl.module


val roundOfGolfDomainModule = module {
    // Use Cases - Single Responsibility Principle
    single { TrackSingleRoundEventUseCase(get()) }
    single { TrackMultipleRoundEventsUseCase(get()) }
    single { DeleteRoundEventsUseCase(get()) }
    single { GetRoundEventsUseCase(get()) }
    single { GetRoundStatisticsUseCase(get()) }
    single { ReplayRoundOfGolfUseCase(get()) }
}