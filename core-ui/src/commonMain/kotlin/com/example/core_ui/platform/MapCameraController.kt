package com.example.core_ui.platform

import com.example.shared.data.model.Hole
import com.example.shared.data.model.MapCameraPosition

/**
 * Platform-specific camera controller for handling map camera positioning.
 * Implements bounds-based zoom and bearing logic per platform.
 */
expect class MapCameraController {

    /**
     * Applies camera positioning for a golf hole with platform-specific logic.
     *
     * @param hole The golf hole containing tee and flag locations
     * @param mapCameraPosition The calculated camera position from use case
     */
    suspend fun applyHoleCameraPosition(hole: Hole, mapCameraPosition: MapCameraPosition)
}