package com.example.shared.di

import androidx.room.RoomDatabase
import com.example.shared.data.database.AppDatabase
import com.example.shared.data.database.getRoomDatabase
import org.koin.dsl.module

expect val platformSpecificModule: org.koin.core.module.Module

val databaseModule = module {
    // Include platform-specific module
    includes(platformSpecificModule)

    single<AppDatabase> {
        val builder = get<RoomDatabase.Builder<AppDatabase>>()
        getRoomDatabase(builder)
    }
    
    single { get<AppDatabase>().locationDao() }
    single { get<AppDatabase>().inPlayEventDao() }
}