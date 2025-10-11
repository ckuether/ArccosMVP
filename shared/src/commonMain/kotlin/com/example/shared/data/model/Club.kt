package com.example.shared.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Club(
    val id: Long,
    val name: String,
    val courseIDs: List<Long>
)