package com.example.round_of_golf_presentation.utils

import com.example.shared.data.model.GolfClubType
import com.example.shared.data.model.Location

sealed class TrackShotUiEvent {
    data class ClubSelected(val selectedClub: GolfClubType): TrackShotUiEvent()
    object ClubSelectionButtonClicked: TrackShotUiEvent()
    object TrackShotCardClicked: TrackShotUiEvent()
    data class LocationStartDragUpdate(val location: Location): TrackShotUiEvent()
    data class LocationEndDragUpdate(val location: Location): TrackShotUiEvent()
    object ExitButtonClicked: TrackShotUiEvent()
}