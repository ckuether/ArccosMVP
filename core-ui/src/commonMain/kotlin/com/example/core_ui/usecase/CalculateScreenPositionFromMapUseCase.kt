package com.example.core_ui.usecase

import com.example.core_ui.projection.ScreenPosition
import com.example.shared.data.model.Location

expect class CalculateScreenPositionFromMapUseCase() {
    operator fun invoke(
        location: Location,
        mapInstance: Any
    ): ScreenPosition?
}