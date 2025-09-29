package com.example.shared.data.repository

interface ResourceReader {
    suspend fun readTextFile(filePath: String): String?
}