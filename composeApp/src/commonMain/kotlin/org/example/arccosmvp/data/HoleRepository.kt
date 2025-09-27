package org.example.arccosmvp.data

import com.example.shared.data.model.Hole
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import arccosmvp.composeapp.generated.resources.Res

class HoleRepository {
    
    @OptIn(ExperimentalResourceApi::class)
    suspend fun loadHoles(): List<Hole> {
        return try {
            val jsonString = Res.readBytes("files/holes.json").decodeToString()
            Json.decodeFromString<List<Hole>>(jsonString)
        } catch (e: Exception) {
            // Return empty list if loading fails
            emptyList()
        }
    }
    
    suspend fun getHoleById(id: Int): Hole? {
        return loadHoles().find { it.id == id }
    }
    
    suspend fun getFirstHole(): Hole? {
        return loadHoles().firstOrNull()
    }
}