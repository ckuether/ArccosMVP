package com.example.shared.di

import com.example.shared.data.database.AppDatabase
import com.example.shared.data.database.DatabaseBuilder
import org.koin.dsl.module

val databaseModule = module {
    single<AppDatabase> { DatabaseBuilder.build() }
    
    single { get<AppDatabase>().locationDao() }
    single { get<AppDatabase>().inPlayEventDao() }
}