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
import com.example.shared.data.repository.GolfCourseRepository
import com.example.shared.data.repository.UserRepository
import com.example.shared.data.model.GolfCourse
import com.example.shared.data.model.Player
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

class LocationTrackingViewModel(
    private val locationTrackingService: LocationTrackingService,
    private val saveLocationEventUseCase: SaveLocationEventUseCase,
    private val getLocationEventsUseCase: GetLocationEventsUseCase,
    private val clearLocationEventsUseCase: ClearLocationEventsUseCase,
    private val checkLocationPermissionUseCase: CheckLocationPermissionUseCase,
    private val requestLocationPermissionUseCase: RequestLocationPermissionUseCase,
    private val golfCourseRepository: GolfCourseRepository,
    private val userRepository: UserRepository,
    private val logger: Logger
) : ViewModel() {
    
    companion object {
        private const val TAG = "LocationTrackingViewModel"
    }
    
    private val _locationState = MutableStateFlow(LocationTrackingUiState())
    val locationState: StateFlow<LocationTrackingUiState> = _locationState.asStateFlow()

    private val _golfCourse = MutableStateFlow<GolfCourse?>(null)
    val golfCourse: StateFlow<GolfCourse?> = _golfCourse.asStateFlow()

    private val _currentPlayer = MutableStateFlow<Player?>(null)
    val currentPlayer: StateFlow<Player?> = _currentPlayer.asStateFlow()
    
    // Flow of location events from database
    val locationEvents = getLocationEventsUseCase()
    
    private var trackingJob: Job? = null

    init {
        loadGolfCourse()
        loadCurrentUser()
        checkPermissionStatus()
    }
    
    private fun loadGolfCourse() {
        viewModelScope.launch {
            try {
                val course = golfCourseRepository.loadGolfCourse()
                _golfCourse.value = course
                logger.info(TAG, "Golf course loaded: ${course?.name}")
            } catch (e: Exception) {
                logger.error(TAG, "Failed to load golf course", e)
            }
        }
    }
    
    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val player = userRepository.getCurrentUser()
                if (player != null) {
                    _currentPlayer.value = player
                    logger.info(TAG, "Current player loaded: ${player.name} (ID: ${player.id})")
                } else {
                    logger.warn(TAG, "No user data found, using default player")
                    // Create a default player if none found
                    _currentPlayer.value = Player(name = "Guest Player")
                }
            } catch (e: Exception) {
                logger.error(TAG, "Failed to load current user", e)
                // Fallback to default player on error
                _currentPlayer.value = Player(name = "Guest Player")
            }
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
                            saveLocationEventUseCase(locationEvent).fold(
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