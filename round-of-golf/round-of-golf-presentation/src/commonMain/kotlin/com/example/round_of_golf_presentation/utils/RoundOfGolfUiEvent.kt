package com.example.round_of_golf_presentation.utils

import com.example.shared.data.model.Location

sealed class RoundOfGolfUiEvent {
    object ResetUiTimer: RoundOfGolfUiEvent()
    object MiniScorecardClicked: RoundOfGolfUiEvent()
    object PreviousHoleClicked: RoundOfGolfUiEvent()
    object NextHoleClicked: RoundOfGolfUiEvent()
    object HoleNavigationCardClicked: RoundOfGolfUiEvent()
    object TrackShotClicked: RoundOfGolfUiEvent()
    data class OnMapClick(val location: Location): RoundOfGolfUiEvent()
    data class TargetLocationUpdated(val location: Location): RoundOfGolfUiEvent()
    data class UserLocationUpdated(val location: Location): RoundOfGolfUiEvent()
    data class OnFinishHole(val score: Int, val putts: Int? = null): RoundOfGolfUiEvent()
    object OnFinishRound: RoundOfGolfUiEvent()
}