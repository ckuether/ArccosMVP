package com.example.shared.di

import com.example.shared.data.repository.RoundOfGolfEventRepository
import com.example.shared.data.repository.RoundOfGolfEventRepositoryImpl
import org.koin.dsl.module

val sharedModule = module {
    
    // Repositories
    single<RoundOfGolfEventRepository> { 
        RoundOfGolfEventRepositoryImpl(get()) 
    }
}