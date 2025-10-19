package org.example.arccosmvp.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shared.data.database.DatabaseConstants

fun getDatabaseBuilder(ctx: Context): RoomDatabase.Builder<AppDatabase> {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath(DatabaseConstants.DATABASE_NAME)
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}