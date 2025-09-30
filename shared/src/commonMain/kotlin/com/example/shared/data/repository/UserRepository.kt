package com.example.shared.data.repository

import com.example.shared.data.model.Player

interface UserRepository {
    suspend fun getCurrentUser(): Player?
}