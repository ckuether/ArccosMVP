package org.example.arccosmvp.database

import androidx.room.RoomDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformSpecificModule: Module

val databaseModule = module {
    // Include platform-specific module
    includes(platformSpecificModule)

    single<AppDatabase> {
        val builder = get<RoomDatabase.Builder<AppDatabase>>()
        getRoomDatabase(builder)
    }

    single { get<AppDatabase>().scoreCardDao() }
    single { get<AppDatabase>().roundOfGolfEventDao() }
}