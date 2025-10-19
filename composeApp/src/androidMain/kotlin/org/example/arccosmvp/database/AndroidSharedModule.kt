package org.example.arccosmvp.database

import androidx.room.RoomDatabase
import org.koin.dsl.module

actual val platformSpecificModule = module {
    single<RoomDatabase.Builder<AppDatabase>> {
        getDatabaseBuilder(get())
    }
}