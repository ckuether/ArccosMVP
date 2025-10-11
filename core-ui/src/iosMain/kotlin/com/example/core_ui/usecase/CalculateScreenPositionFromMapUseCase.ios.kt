package com.example.core_ui.usecase

import com.example.core_ui.projection.ScreenPosition
import com.example.shared.data.model.Location

actual class CalculateScreenPositionFromMapUseCase {

    /**
     * iOS implementation - for now returns null, can be implemented later with GMSMapView projection
     */
    actual operator fun invoke(
        location: Location,
        mapInstance: Any
    ): ScreenPosition? {
        // TODO: Implement iOS projection using GMSMapView
        return null
    }
}