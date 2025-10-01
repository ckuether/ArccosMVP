package com.example.shared.data.repository

import com.example.shared.config.ResourcePaths
import com.example.shared.data.model.Player
import kotlinx.serialization.json.Json

class UserRepositoryImpl(
    private val resourceReader: ResourceReader
) : UserRepository {
    
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getCurrentUser(): Player? {
        return try {
            val jsonString = resourceReader.readTextFile(ResourcePaths.USER_DATA)
            val result = jsonString?.let { Json.Default.decodeFromString<Player>(it) }
            result
        } catch (e: Exception) {
            println("DEBUG: Error in loadGolfCourse: ${e.message}")
            // Return null if loading fails
            null
        }
    }
}