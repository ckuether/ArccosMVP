package org.example.arccosmvp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.location.domain.usecase.LocationException
import com.example.location.domain.usecase.CheckLocationPermissionUseCase
import com.example.location.domain.usecase.RequestLocationPermissionUseCase
import com.example.location.domain.usecase.SaveLocationEventUseCase
import com.example.location.domain.usecase.GetLocationEventsUseCase
import com.example.location.domain.usecase.ClearLocationEventsUseCase
import com.example.location.domain.usecase.PermissionResult
import com.example.location.domain.service.LocationTrackingService
import com.example.shared.data.model.GolfCourse
import com.example.shared.data.model.Player
import com.example.shared.data.model.ScoreCard
import com.example.shared.platform.getCurrentTimeMillis
import com.example.shared.domain.usecase.SaveScoreCardUseCase
import com.example.shared.domain.usecase.LoadGolfCourseUseCase
import com.example.shared.domain.usecase.LoadCurrentUserUseCase
import com.example.shared.domain.usecase.GetAllScoreCardsUseCase
import com.example.shared.platform.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

class RoundOfGolfViewModel(
    private val locationTrackingService: LocationTrackingService,
    private val saveLocationEventUseCase: SaveLocationEventUseCase,
    private val getLocationEventsUseCase: GetLocationEventsUseCase,
    private val clearLocationEventsUseCase: ClearLocationEventsUseCase,
    private val checkLocationPermissionUseCase: CheckLocationPermissionUseCase,
    private val requestLocationPermissionUseCase: RequestLocationPermissionUseCase,
    private val loadGolfCourseUseCase: LoadGolfCourseUseCase,
    private val loadCurrentUserUseCase: LoadCurrentUserUseCase,
    private val saveScoreCardUseCase: SaveScoreCardUseCase,
    private val getAllScoreCardsUseCase: GetAllScoreCardsUseCase,
    private val logger: Logger
) : ViewModel() {
    
    companion object Companion {
        private const val TAG = "LocationTrackingViewModel"
    }
    
    private val _locationState = MutableStateFlow(LocationTrackingUiState())
    val locationState: StateFlow<LocationTrackingUiState> = _locationState.asStateFlow()

    private val _golfCourse = MutableStateFlow<GolfCourse?>(null)
    val golfCourse: StateFlow<GolfCourse?> = _golfCourse.asStateFlow()

    private val _currentPlayer = MutableStateFlow<Player?>(null)
    val currentPlayer: StateFlow<Player?> = _currentPlayer.asStateFlow()

    private val _currentScoreCard = MutableStateFlow(
        ScoreCard(
            courseId = 0L,
            playerId = 0L,
            scorecard = emptyMap()
        )
    )
    val currentScoreCard: StateFlow<ScoreCard> = _currentScoreCard.asStateFlow()
    
    private val roundId: Long get() = _currentScoreCard.value.roundId

    // Flow of location events from database
    val locationEvents = getLocationEventsUseCase()
    
    // Flow of all scorecards from database
    val allScoreCards = getAllScoreCardsUseCase()
    
    private var trackingJob: Job? = null

    init {
        loadGolfCourse()
        loadCurrentUser()
        checkPermissionStatus()
    }
    
    private fun loadGolfCourse() {
        viewModelScope.launch {
            loadGolfCourseUseCase().fold(
                onSuccess = { course ->
                    _golfCourse.value = course
                    logger.info(TAG, "Golf course loaded: ${course?.name}")
                },
                onFailure = { error ->
                    logger.error(TAG, "Failed to load golf course", error)
                }
            )
        }
    }
    
    private fun loadCurrentUser() {
        viewModelScope.launch {
            loadCurrentUserUseCase().fold(
                onSuccess = { player ->
                    _currentPlayer.value = player
                    logger.info(TAG, "Current player loaded: ${player.name} (ID: ${player.id})")
                },
                onFailure = { error ->
                    logger.error(TAG, "Failed to load current user", error)
                    _currentPlayer.value = Player(name = "Player")
                }
            )
        }
    }
    
    fun startLocationTracking() {
        logger.info(TAG, "startLocationTracking() called")
        viewModelScope.launch {
            // Check permission first
            if (!checkLocationPermissionUseCase()) {
                logger.warn(TAG, "Location permission not granted")
                _locationState.value = _locationState.value.copy(
                    error = "Location permission required. Please grant permission first.",
                    hasPermission = false
                )
                return@launch
            }
            
            logger.info(TAG, "Permission granted, proceeding with tracking")
            
            // Cancel any existing tracking job but don't stop the service yet
            trackingJob?.cancel()
            trackingJob = null
            
            try {
                logger.info(TAG, "Setting UI state and starting location service")
                _locationState.value = _locationState.value.copy(isLoading = true, isTracking = true, error = null)
                
                logger.info(TAG, "Calling locationTrackingService.startLocationTracking()")
                trackingJob = locationTrackingService.startLocationTracking()
                    .onEach { locationEvent ->
                        // Only save to database, no UI updates to prevent recomposition
                        launch(Dispatchers.IO) {
                            saveLocationEventUseCase(locationEvent, roundId).fold(
                                onSuccess = { 
                                    // Location saved successfully - no UI update needed
                                },
                                onFailure = { error ->
                                    logger.error(TAG, "Failed to save location event", error)
                                }
                            )
                        }
                    }
                    .catch { throwable ->
                        val errorMessage = when (throwable) {
                            is LocationException -> throwable.message
                            else -> "Location tracking error: ${throwable.message}"
                        }
                        _locationState.value = _locationState.value.copy(
                            isLoading = false,
                            isTracking = false,
                            error = errorMessage
                        )
                    }
                    .launchIn(viewModelScope)
                    
            } catch (e: Exception) {
                _locationState.value = _locationState.value.copy(
                    isLoading = false,
                    isTracking = false,
                    error = e.message ?: "Failed to start location tracking"
                )
            }
        }
    }

    fun stopLocationTracking() {
        logger.info(TAG, "stopLocationTracking() called")
        viewModelScope.launch {
            try {
                trackingJob?.cancel()
                trackingJob = null
                locationTrackingService.stopLocationTracking()
                _locationState.value = _locationState.value.copy(
                    isLoading = false,
                    isTracking = false,
                    error = null
                )
                logger.info(TAG, "Location tracking stopped successfully")
            } catch (e: Exception) {
                logger.error(TAG, "Error stopping location tracking", e)
                _locationState.value = _locationState.value.copy(
                    error = e.message ?: "Failed to stop location tracking"
                )
            }
        }
    }

    fun requestLocationPermission() {
        viewModelScope.launch {
            _locationState.value = _locationState.value.copy(isRequestingPermission = true, error = null)
            
            try {
                val result = requestLocationPermissionUseCase()
                
                when (result) {
                    is PermissionResult.Granted -> {
                        _locationState.value = _locationState.value.copy(
                            hasPermission = true,
                            isRequestingPermission = false,
                            error = null
                        )
                        // Automatically start location tracking after permission is granted
                        logger.info(TAG, "Permission granted, starting location tracking")
                        startLocationTracking()
                    }
                    is PermissionResult.Denied -> {
                        _locationState.value = _locationState.value.copy(
                            hasPermission = false,
                            isRequestingPermission = false,
                            error = "Location permission denied. Please try again."
                        )
                    }
                    is PermissionResult.PermanentlyDenied -> {
                        _locationState.value = _locationState.value.copy(
                            hasPermission = false,
                            isRequestingPermission = false,
                            error = "Location permission permanently denied. Please enable it in settings."
                        )
                    }
                }
            } catch (e: Exception) {
                _locationState.value = _locationState.value.copy(
                    isRequestingPermission = false,
                    error = e.message ?: "Error requesting permission"
                )
            }
        }
    }
    
    fun updateHoleScore(holeNumber: Int, score: Int) {
        val currentCard = _currentScoreCard.value
        val updatedScorecard = currentCard.scorecard.toMutableMap()
        updatedScorecard[holeNumber] = score
        
        val updatedCard = currentCard.copy(
            scorecard = updatedScorecard,
            lastUpdatedTimestamp = getCurrentTimeMillis()
        )
        _currentScoreCard.value = updatedCard
        
        // Save to database using UseCase
        viewModelScope.launch(Dispatchers.IO) {
            saveScoreCardUseCase(updatedCard).fold(
                onSuccess = {
                    logger.info(TAG, "Updated hole $holeNumber score to $score and saved to database")
                },
                onFailure = { error ->
                    logger.error(TAG, "Failed to save scorecard to database", error)
                }
            )
        }
    }
    
    fun getHoleScore(holeNumber: Int): Int? {
        return _currentScoreCard.value.scorecard[holeNumber]
    }
    
    fun getTotalScore(): Int {
        return _currentScoreCard.value.scorecard.values.filterNotNull().sum()
    }
    
    fun getTotalPar(): Int {
        val course = _golfCourse.value ?: return 0
        return course.holes.sumOf { it.par }
    }
    
    fun getCompletedHolesPar(): Int {
        val course = _golfCourse.value ?: return 0
        val completedHoles = _currentScoreCard.value.scorecard.keys
        return course.holes.filter { it.id in completedHoles }.sumOf { it.par }
    }
    
    fun getScoreToPar(): String {
        val totalScore = getTotalScore()
        val completedPar = getCompletedHolesPar()
        
        return if (totalScore > 0 && completedPar > 0) {
            val difference = totalScore - completedPar
            when {
                difference > 0 -> "+$difference"
                difference < 0 -> "$difference"
                else -> "E"
            }
        } else {
            "E"
        }
    }
    
    fun checkPermissionStatus() {
        viewModelScope.launch {
            try {
                val hasPermission = checkLocationPermissionUseCase()
                _locationState.value = _locationState.value.copy(hasPermission = hasPermission)
                
                // Automatically start location tracking if permission is granted
                if (hasPermission && !_locationState.value.isTracking) {
                    logger.info(TAG, "Permission granted, starting location tracking automatically")
                    startLocationTracking()
                }
            } catch (e: Exception) {
                _locationState.value = _locationState.value.copy(
                    error = e.message ?: "Error checking permission"
                )
            }
        }
    }
    
    fun clearLocations() {
        viewModelScope.launch {
            clearLocationEventsUseCase().fold(
                onSuccess = {
                    logger.info(TAG, "All location events cleared successfully")
                },
                onFailure = { error ->
                    logger.error(TAG, "Failed to clear location events", error)
                }
            )
        }
    }
    
    fun clearError() {
        _locationState.value = _locationState.value.copy(error = null)
    }
    
    override fun onCleared() {
        super.onCleared()
        stopLocationTracking()
    }
}

data class LocationTrackingUiState(
    val isLoading: Boolean = false,
    val isTracking: Boolean = false,
    val hasPermission: Boolean? = null, // null = unknown, true = granted, false = denied
    val isRequestingPermission: Boolean = false,
    val error: String? = null
)