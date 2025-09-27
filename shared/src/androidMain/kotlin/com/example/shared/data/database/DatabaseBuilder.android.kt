package com.example.shared.data.database

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

actual object DatabaseBuilder {
    private lateinit var context: Context
    
    fun initialize(context: Context) {
        this.context = context
    }
    
    actual fun build(): AppDatabase {
        val databasePath = context.getDatabasePath(DatabaseConstants.DATABASE_NAME).absolutePath
        return buildDatabase(databasePath)
    }
}