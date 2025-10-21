package org.example.arccosmvp.strings

import androidx.compose.runtime.Composable
import com.example.core_ui.strings.StringResourcesManager
import com.example.core_ui.utils.UiText
import arccosmvp.composeapp.generated.resources.Res
import arccosmvp.composeapp.generated.resources.*

/**
 * Implementation of StringResourcesManager that provides access to composeApp string resources.
 * This allows other modules to access string resources without depending on composeApp directly.
 */
class AppStringResourcesManager : StringResourcesManager {
    
    // Golf scoring terms
    @Composable override fun getHoleInOne(): String = 
        UiText.StringResourceId(Res.string.hole_in_one).asString()
    
    @Composable override fun getEagle(): String = 
        UiText.StringResourceId(Res.string.eagle).asString()
    
    @Composable override fun getBirdie(): String = 
        UiText.StringResourceId(Res.string.birdie).asString()
    
    @Composable override fun getPar(): String = 
        UiText.StringResourceId(Res.string.par).asString()
    
    @Composable override fun getBogey(): String = 
        UiText.StringResourceId(Res.string.bogey).asString()
    
    @Composable override fun getEvenPar(): String = 
        UiText.StringResourceId(Res.string.even_par).asString()
    
    // Location permission and tracking
    @Composable override fun getLocationPermissionRequiredPleaseGrant(): String = 
        UiText.StringResourceId(Res.string.location_permission_required_please_grant).asString()
    
    @Composable override fun getLocationTrackingError(message: String): String = 
        UiText.StringResourceId(Res.string.location_tracking_error_template, arrayOf(message)).asString()
    
    @Composable override fun getFailedToStartLocationTracking(): String = 
        UiText.StringResourceId(Res.string.failed_to_start_location_tracking).asString()
    
    @Composable override fun getLocationPermissionDeniedTryAgain(): String = 
        UiText.StringResourceId(Res.string.location_permission_denied_try_again).asString()
    
    @Composable override fun getLocationPermissionPermanentlyDeniedSettings(): String = 
        UiText.StringResourceId(Res.string.location_permission_permanently_denied_settings).asString()
    
    @Composable override fun getErrorRequestingPermission(): String = 
        UiText.StringResourceId(Res.string.error_requesting_permission).asString()
    
    @Composable override fun getFailedToStopLocationTracking(): String = 
        UiText.StringResourceId(Res.string.failed_to_stop_location_tracking).asString()
    
    @Composable override fun getErrorCheckingPermission(): String = 
        UiText.StringResourceId(Res.string.error_checking_permission).asString()
    
    // Hole Stats Bottom Sheet
    @Composable override fun getHoleScore(holeNumber: Int): String = 
        UiText.StringResourceId(Res.string.hole_score_template, arrayOf(holeNumber)).asString()
    
    @Composable override fun getOthers(): String = 
        UiText.StringResourceId(Res.string.others).asString()
    
    @Composable override fun getPutts(): String = 
        UiText.StringResourceId(Res.string.putts).asString()
    
    @Composable override fun getPuttsFourOrMore(): String = 
        UiText.StringResourceId(Res.string.putts_four_or_more).asString()
    
    @Composable override fun getPreviousHole(): String = 
        UiText.StringResourceId(Res.string.previous_hole).asString()
    
    @Composable override fun getNextHole(): String = 
        UiText.StringResourceId(Res.string.next_hole).asString()
    
    @Composable override fun getFinishRound(): String = 
        UiText.StringResourceId(Res.string.finish_round).asString()
    
    @Composable override fun getFinishHole(holeNumber: Int): String = 
        UiText.StringResourceId(Res.string.finish_hole_template, arrayOf(holeNumber)).asString()
    
    // Club Selection Dialog
    @Composable override fun getSelectClub(): String = 
        UiText.StringResourceId(Res.string.select_club).asString()
    
    @Composable override fun getSelect(): String = 
        UiText.StringResourceId(Res.string.select).asString()
    
    @Composable override fun getCancel(): String = 
        UiText.StringResourceId(Res.string.cancel).asString()
    
    // Navigation
    @Composable override fun getHole(holeNumber: Int): String = 
        UiText.StringResourceId(Res.string.hole_template, arrayOf(holeNumber)).asString()
    
    // Score Card Bottom Sheet
    @Composable override fun getGolfCourseFallback(): String = 
        UiText.StringResourceId(Res.string.golf_course_fallback).asString()
    
    @Composable override fun getBlueTee(): String = 
        UiText.StringResourceId(Res.string.blue_tee).asString()
    
    @Composable override fun getHole(): String = 
        UiText.StringResourceId(Res.string.hole).asString()
    
    @Composable override fun getTotal(): String = 
        UiText.StringResourceId(Res.string.total).asString()
    
    @Composable override fun getDash(): String = 
        UiText.StringResourceId(Res.string.dash).asString()
    
    // Target Shot Card
    @Composable override fun getClub(): String = 
        UiText.StringResourceId(Res.string.club).asString()
    
    @Composable override fun getDistance(): String = 
        UiText.StringResourceId(Res.string.distance).asString()
    
    @Composable override fun getYards(distance: Int): String = 
        UiText.StringResourceId(Res.string.yards_template, arrayOf(distance)).asString()
    
    // Track Shot Card
    @Composable override fun getTrackShot(): String = 
        UiText.StringResourceId(Res.string.track_shot).asString()
    
    // Hole Info Card
    @Composable override fun getMidGreen(): String = 
        UiText.StringResourceId(Res.string.mid_green).asString()
    
    // Location Permission Card
    @Composable override fun getLocationPermissionRequiredTitle(): String = 
        UiText.StringResourceId(Res.string.location_permission_required_title).asString()
    
    @Composable override fun getLocationPermissionDescription(): String = 
        UiText.StringResourceId(Res.string.location_permission_description).asString()
    
    @Composable override fun getRequesting(): String = 
        UiText.StringResourceId(Res.string.requesting).asString()
    
    @Composable override fun getGrantPermission(): String = 
        UiText.StringResourceId(Res.string.grant_permission).asString()
    
    // Golf Markers
    @Composable override fun getGolfBallTee(): String = 
        UiText.StringResourceId(Res.string.golf_ball_tee).asString()
    
    // Round of Golf Main
    @Composable override fun getExitTrackShotMode(): String = 
        UiText.StringResourceId(Res.string.exit_track_shot_mode).asString()
    
    @Composable override fun getShotTracked(holeNumber: Int): String = 
        UiText.StringResourceId(Res.string.shot_tracked_template, arrayOf(holeNumber)).asString()
    
    @Composable override fun getFailedToTrackShot(message: String): String = 
        UiText.StringResourceId(Res.string.failed_to_track_shot_template, arrayOf(message)).asString()
    
    @Composable override fun getSelectGolfClub(): String = 
        UiText.StringResourceId(Res.string.select_golf_club).asString()
    
    @Composable override fun getRoundCompleted(): String = 
        UiText.StringResourceId(Res.string.round_completed).asString()
    
    // MiniScorecard
    @Composable override fun getScorecard(): String = 
        UiText.StringResourceId(Res.string.scorecard).asString()
    
    // Golf Home Screen strings
    @Composable
    fun getLoadingCourse(): String = 
        UiText.StringResourceId(Res.string.loading_course).asString()
    
    @Composable
    fun getStartRound(): String = 
        UiText.StringResourceId(Res.string.start_round).asString()
    
    @Composable
    fun getPastRounds(): String = 
        UiText.StringResourceId(Res.string.past_rounds).asString()
    
    // Previous Rounds Bottom Sheet strings
    @Composable
    fun getPreviousRounds(): String = 
        UiText.StringResourceId(Res.string.previous_rounds).asString()
    
    @Composable
    fun getNoPreviousRounds(): String = 
        UiText.StringResourceId(Res.string.no_previous_rounds).asString()
    
    @Composable
    fun getRoundsAppearHere(): String = 
        UiText.StringResourceId(Res.string.rounds_appear_here).asString()
    
    @Composable
    fun getFinalThruHoles(holes: Int): String = 
        UiText.StringResourceId(Res.string.final_thru_holes, arrayOf(holes)).asString()
    
    @Composable
    fun getToPar(): String = 
        UiText.StringResourceId(Res.string.to_par).asString()
    
    @Composable
    fun getGrossScore(score: Int): String = 
        UiText.StringResourceId(Res.string.gross_score, arrayOf(score)).asString()
    
    @Composable
    fun getBirdies(): String = 
        UiText.StringResourceId(Res.string.birdies).asString()
    
    @Composable
    fun getBogeys(): String = 
        UiText.StringResourceId(Res.string.bogeys).asString()
    
    // Player string
    @Composable
    fun getDefaultPlayer(): String = 
        UiText.StringResourceId(Res.string.default_player).asString()
}