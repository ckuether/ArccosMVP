package com.example.core_ui.projection

import com.example.shared.data.model.Location

data class ScreenPosition(
    val x: Int,
    val y: Int
)

expect class CalculateScreenPositionFromMapUseCase() {
    operator fun invoke(
        location: Location,
        mapInstance: Any
    ): ScreenPosition?
}