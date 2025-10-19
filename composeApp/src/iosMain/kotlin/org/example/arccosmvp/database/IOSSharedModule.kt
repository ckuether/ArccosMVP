package org.example.arccosmvp.database

import androidx.room.RoomDatabase
import org.koin.dsl.module

actual val platformSpecificModule = module {

    // Room Database Builder
    single<RoomDatabase.Builder<AppDatabase>> {
        getDatabaseBuilder()
    }
}