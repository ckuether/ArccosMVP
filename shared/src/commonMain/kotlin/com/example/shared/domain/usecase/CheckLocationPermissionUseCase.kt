package com.example.shared.domain.usecase

interface CheckLocationPermissionUseCase {
    suspend operator fun invoke(): Boolean
}