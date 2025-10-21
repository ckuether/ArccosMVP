package com.example.core_ui.strings

import androidx.compose.runtime.Composable

/**
 * Interface for providing string resources to modules that cannot directly access 
 * composeApp string resources. This follows dependency inversion principle where
 * feature modules depend on abstractions, not concrete implementations.
 */
interface StringResourcesManager {
    
    // Golf scoring terms
    @Composable fun getHoleInOne(): String
    @Composable fun getEagle(): String
    @Composable fun getBirdie(): String
    @Composable fun getPar(): String
    @Composable fun getBogey(): String
    @Composable fun getEvenPar(): String
    
    // Location permission and tracking
    @Composable fun getLocationPermissionRequiredPleaseGrant(): String
    @Composable fun getLocationTrackingError(message: String): String
    @Composable fun getFailedToStartLocationTracking(): String
    @Composable fun getLocationPermissionDeniedTryAgain(): String
    @Composable fun getLocationPermissionPermanentlyDeniedSettings(): String
    @Composable fun getErrorRequestingPermission(): String
    @Composable fun getFailedToStopLocationTracking(): String
    @Composable fun getErrorCheckingPermission(): String
    
    // Hole Stats Bottom Sheet
    @Composable fun getHoleScore(holeNumber: Int): String
    @Composable fun getOthers(): String
    @Composable fun getPutts(): String
    @Composable fun getPuttsFourOrMore(): String
    @Composable fun getPreviousHole(): String
    @Composable fun getNextHole(): String
    @Composable fun getFinishRound(): String
    @Composable fun getFinishHole(holeNumber: Int): String
    
    // Club Selection Dialog
    @Composable fun getSelectClub(): String
    @Composable fun getSelect(): String
    @Composable fun getCancel(): String
    
    // Navigation
    @Composable fun getHole(holeNumber: Int): String
    
    // Score Card Bottom Sheet
    @Composable fun getGolfCourseFallback(): String
    @Composable fun getBlueTee(): String
    @Composable fun getHole(): String
    @Composable fun getTotal(): String
    @Composable fun getDash(): String
    
    // Target Shot Card
    @Composable fun getClub(): String
    @Composable fun getDistance(): String
    @Composable fun getYards(distance: Int): String
    
    // Track Shot Card
    @Composable fun getTrackShot(): String
    
    // Hole Info Card
    @Composable fun getMidGreen(): String
    
    // Location Permission Card
    @Composable fun getLocationPermissionRequiredTitle(): String
    @Composable fun getLocationPermissionDescription(): String
    @Composable fun getRequesting(): String
    @Composable fun getGrantPermission(): String
    
    // Golf Markers
    @Composable fun getGolfBallTee(): String
    
    // Round of Golf Main
    @Composable fun getExitTrackShotMode(): String
    @Composable fun getShotTracked(holeNumber: Int): String
    @Composable fun getFailedToTrackShot(message: String): String
    @Composable fun getSelectGolfClub(): String
    @Composable fun getRoundCompleted(): String
    
    // MiniScorecard
    @Composable fun getScorecard(): String
}