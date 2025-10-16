package com.example.shared.di

import com.example.shared.data.repository.RoundOfGolfEventRepository
import com.example.shared.data.repository.RoundOfGolfEventRepositoryImpl
import com.example.shared.usecase.ReplayRoundOfGolfUseCase
import com.example.shared.usecase.TrackRoundEventUseCase
import com.example.shared.usecase.GetRoundEventsUseCase
import com.example.shared.usecase.GetRoundStatisticsUseCase
import org.koin.dsl.module

val sharedModule = module {
    
    // Repositories
    single<RoundOfGolfEventRepository> { 
        RoundOfGolfEventRepositoryImpl(get()) 
    }
    
    // Use Cases - Single Responsibility Principle
    single { TrackRoundEventUseCase(get()) }
    single { GetRoundEventsUseCase(get()) }
    single { GetRoundStatisticsUseCase(get()) }
    single { ReplayRoundOfGolfUseCase(get()) }
}