package com.example.core_ui.projection

import com.example.shared.data.model.Location

expect class CalculateMapPositionFromScreenUseCase() {
    operator fun invoke(
        screenX: Int,
        screenY: Int,
        mapInstance: Any
    ): Location?
}