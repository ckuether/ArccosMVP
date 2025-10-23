package com.example.shared.data.repository

import com.example.shared.data.model.Course
import com.example.shared.utils.FilePaths
import kotlinx.serialization.json.Json

class GolfCourseRepository(
    private val resourceReader: ResourceReader
) {

    suspend fun loadGolfCourse(): Course? {
        return try {
            val jsonString = resourceReader.readTextFile(FilePaths.GOLF_COURSE_DATA)
            val result = jsonString?.let { Json.Default.decodeFromString<Course>(it) }
            result
        } catch (e: Exception) {
            println("DEBUG: Error in loadGolfCourse: ${e.message}")
            // Return null if loading fails
            null
        }
    }
}