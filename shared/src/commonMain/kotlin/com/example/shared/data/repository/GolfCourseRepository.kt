package com.example.shared.data.repository

import com.example.shared.data.model.Course
import com.example.shared.data.model.Hole
import com.example.shared.config.ResourcePaths
import kotlinx.serialization.json.Json

class GolfCourseRepository(
    private val resourceReader: ResourceReader
) {

    suspend fun loadGolfCourse(): Course? {
        return try {
            val jsonString = resourceReader.readTextFile(ResourcePaths.GOLF_COURSE_DATA)
            val result = jsonString?.let { Json.Default.decodeFromString<Course>(it) }
            result
        } catch (e: Exception) {
            println("DEBUG: Error in loadGolfCourse: ${e.message}")
            // Return null if loading fails
            null
        }
    }

    suspend fun getHoleById(id: Int): Hole? {
        return loadGolfCourse()?.holes?.find { it.id == id }
    }

    suspend fun getFirstHole(): Hole? {
        return loadGolfCourse()?.holes?.firstOrNull()
    }

    suspend fun getAllHoles(): List<Hole> {
        return loadGolfCourse()?.holes ?: emptyList()
    }
}