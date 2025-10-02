package com.example.shared.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.shared.data.dao.InPlayEventDao
import com.example.shared.data.dao.ScoreCardDao
import com.example.shared.data.entity.InPlayEventEntity
import com.example.shared.data.entity.ScoreCardEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [
        InPlayEventEntity::class,
        ScoreCardEntity::class
    ],
    version = DatabaseConstants.DATABASE_VERSION,
    exportSchema = true
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun inPlayEventDao(): InPlayEventDao
    abstract fun scoreCardDao(): ScoreCardDao
}

// Database constructor for Room
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

fun getInPlayEventDao(appDatabase: AppDatabase) = appDatabase.inPlayEventDao()

fun getScoreCardDao(appDatabase: AppDatabase) = appDatabase.scoreCardDao()