package com.example.shared.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.shared.data.dao.InPlayEventDao
import com.example.shared.data.dao.LocationDao
import com.example.shared.data.entity.InPlayEventEntity
import com.example.shared.data.entity.LocationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [
        LocationEntity::class,
        InPlayEventEntity::class
    ],
    version = DatabaseConstants.DATABASE_VERSION,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun inPlayEventDao(): InPlayEventDao
}

// Platform-specific database builder - implemented in platform modules
expect object DatabaseBuilder {
    fun build(): AppDatabase
}

// Common database configuration
fun buildDatabase(databasePath: String): AppDatabase {
    return Room.databaseBuilder<AppDatabase>(
        name = databasePath
    )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}