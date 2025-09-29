package org.example.arccosmvp.utils

import com.example.shared.data.repository.ResourceReader
import org.jetbrains.compose.resources.ExperimentalResourceApi
import arccosmvp.composeapp.generated.resources.Res

class ComposeResourceReader : ResourceReader {
    
    @OptIn(ExperimentalResourceApi::class)
    override suspend fun readTextFile(filePath: String): String? {
        return try {
            println("DEBUG: Reading file: $filePath")
            val result = Res.readBytes(filePath).decodeToString()
            println("DEBUG: File read successfully, length: ${result.length}")
            result
        } catch (e: Exception) {
            println("DEBUG: Error reading file $filePath: ${e.message}")
            null
        }
    }
}