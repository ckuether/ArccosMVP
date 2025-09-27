package com.example.shared.di

import androidx.room.RoomDatabase
import com.example.shared.data.database.AppDatabase
import com.example.shared.data.database.getDatabaseBuilder
import org.koin.dsl.module

actual val platformSpecificModule = module {

    // Room Database Builder
    single<RoomDatabase.Builder<AppDatabase>> {
        getDatabaseBuilder()
    }
}