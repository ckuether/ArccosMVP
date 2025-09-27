package com.example.shared.data.database

import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual object DatabaseBuilder {
    actual fun build(): AppDatabase {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )
        val databasePath = documentDirectory?.path + "/${DatabaseConstants.DATABASE_NAME}"
        return buildDatabase(databasePath)
    }
}