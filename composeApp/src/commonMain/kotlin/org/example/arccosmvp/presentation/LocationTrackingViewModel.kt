package org.example.arccosmvp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.location.domain.service.LocationTrackingService
import com.example.location.domain.usecase.LocationException
import com.example.location.domain.usecase.CheckLocationPermissionUseCase
import com.example.location.domain.usecase.RequestLocationPermissionUseCase
import com.example.location.domain.usecase.PermissionResult
import com.example.shared.data.event.InPlayEvent
import com.example.shared.data.event.Location
import com.example.shared.platform.Logger
import com.example.shared.data.dao.InPlayEventDao
import com.example.shared.data.entity.toEntity
import com.example.shared.data.entity.toInPlayEvent
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job

class LocationTrackingViewModel(
    private val locationTrackingService: LocationTrackingService,
    private val checkLocationPermissionUseCase: CheckLocationPermissionUseCase,
    private val requestLocationPermissionUseCase: RequestLocationPermissionUseCase,
    private val inPlayEventDao: InPlayEventDao,
    private val logger: Logger
) : ViewModel() {
    
    companion object {
        private const val TAG = "LocationTrackingViewModel"
    }
    
    private val _uiState = MutableStateFlow(LocationTrackingUiState())
    val uiState: StateFlow<LocationTrackingUiState> = _uiState.asStateFlow()
    
    val isTracking = locationTrackingService.isTracking
    
    // Flow of location events from database
    val locationEvents = inPlayEventDao.getEventsByType("LocationUpdated")
        .map { entities ->
            entities.mapNotNull { entity ->
                try {
                    // Convert entity back to InPlayEvent and then to LocationItem
                    val event = entity.toInPlayEvent() as? InPlayEvent.LocationUpdated
                    event?.let { 
                        LocationItem(
                            location = it.location,
                            timestamp = it.timestamp
                        )
                    }
                } catch (e: Exception) {
                    logger.error(TAG, "Failed to convert entity to LocationItem", e)
                    null
                }
            }
        }
    
    private var trackingJob: Job? = null
    
    fun startLocationTracking() {
        logger.info(TAG, "startLocationTracking() called")
        viewModelScope.launch {
            // Check permission first
            if (!checkLocationPermissionUseCase()) {
                logger.warn(TAG, "Location permission not granted")
                _uiState.value = _uiState.value.copy(
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
                _uiState.value = _uiState.value.copy(isLoading = true, isTracking = true, error = null)
                
                logger.info(TAG, "Calling locationTrackingService.startLocationTracking()")
                trackingJob = locationTrackingService.startLocationTracking()
                    .onEach { locationEvent ->
                        logger.debug(TAG, "Received location event: ${locationEvent.location.lat}, ${locationEvent.location.long}")
                        
                        // Try to save to database with error handling
                        try {
                            inPlayEventDao.insertEvent(locationEvent.toEntity())
                            logger.debug(TAG, "Location event saved to database successfully")
                        } catch (e: Exception) {
                            logger.error(TAG, "Failed to save location event to database", e)
                            // Continue with UI update even if database save fails
                        }
                        
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isTracking = true,
                            lastLocationEvent = locationEvent,
                            error = null
                        )
                    }
                    .catch { throwable ->
                        val errorMessage = when (throwable) {
                            is LocationException -> throwable.message
                            else -> "Location tracking error: ${throwable.message}"
                        }
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isTracking = false,
                            error = errorMessage
                        )
                    }
                    .launchIn(viewModelScope)
                    
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
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
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isTracking = false,
                    error = null
                )
                logger.info(TAG, "Location tracking stopped successfully")
            } catch (e: Exception) {
                logger.error(TAG, "Error stopping location tracking", e)
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to stop location tracking"
                )
            }
        }
    }
    
    fun requestLocationPermission() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRequestingPermission = true, error = null)
            
            try {
                val result = requestLocationPermissionUseCase()
                
                when (result) {
                    is PermissionResult.Granted -> {
                        _uiState.value = _uiState.value.copy(
                            hasPermission = true,
                            isRequestingPermission = false,
                            error = null
                        )
                    }
                    is PermissionResult.Denied -> {
                        _uiState.value = _uiState.value.copy(
                            hasPermission = false,
                            isRequestingPermission = false,
                            error = "Location permission denied. Please try again."
                        )
                    }
                    is PermissionResult.PermanentlyDenied -> {
                        _uiState.value = _uiState.value.copy(
                            hasPermission = false,
                            isRequestingPermission = false,
                            error = "Location permission permanently denied. Please enable it in settings."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
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
                _uiState.value = _uiState.value.copy(hasPermission = hasPermission)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error checking permission"
                )
            }
        }
    }
    
    fun clearLocations() {
        viewModelScope.launch {
            try {
                // Delete all location events from database
                inPlayEventDao.getEventsByType("LocationUpdated").collect { events ->
                    events.forEach { event ->
                        inPlayEventDao.deleteEvent(event)
                    }
                    logger.info(TAG, "All location events cleared from database")
                }
            } catch (e: Exception) {
                logger.error(TAG, "Failed to clear location events", e)
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    override fun onCleared() {
        super.onCleared()
        stopLocationTracking()
    }
}

data class LocationTrackingUiState(
    val isLoading: Boolean = false,
    val isTracking: Boolean = false,
    val lastLocationEvent: InPlayEvent.LocationUpdated? = null,
    val hasPermission: Boolean? = null, // null = unknown, true = granted, false = denied
    val isRequestingPermission: Boolean = false,
    val error: String? = null
)

data class LocationItem(
    val location: Location,
    val timestamp: Long // Epoch milliseconds
)