package com.example.shared.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GolfCourse(
    val id: Long,
    val name: String,
    val holes: List<Hole>
)