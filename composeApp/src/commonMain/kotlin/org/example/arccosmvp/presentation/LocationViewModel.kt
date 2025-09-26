package org.example.arccosmvp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.location.domain.usecase.GetLocationUseCase
import com.example.location.domain.usecase.CheckLocationPermissionUseCase
import com.example.location.domain.usecase.RequestLocationPermissionUseCase
import com.example.location.domain.usecase.PermissionResult
import com.example.location.domain.model.LocationResult
import com.example.shared.event.InPlayEvent
import com.example.shared.event.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.arccosmvp.platform.getCurrentTimeMillis

class LocationViewModel(
    private val getLocationUseCase: GetLocationUseCase,
    private val checkLocationPermissionUseCase: CheckLocationPermissionUseCase,
    private val requestLocationPermissionUseCase: RequestLocationPermissionUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LocationUiState())
    val uiState: StateFlow<LocationUiState> = _uiState.asStateFlow()
    
    fun requestLocationPermission() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRequestingPermission = true, errorMessage = null)
            
            try {
                val result = requestLocationPermissionUseCase()
                
                when (result) {
                    is PermissionResult.Granted -> {
                        _uiState.value = _uiState.value.copy(
                            hasPermission = true,
                            isRequestingPermission = false,
                            errorMessage = null
                        )
                    }
                    is PermissionResult.Denied -> {
                        _uiState.value = _uiState.value.copy(
                            hasPermission = false,
                            isRequestingPermission = false,
                            errorMessage = "Location permission denied. Please try again."
                        )
                    }
                    is PermissionResult.PermanentlyDenied -> {
                        _uiState.value = _uiState.value.copy(
                            hasPermission = false,
                            isRequestingPermission = false,
                            errorMessage = "Location permission permanently denied. Please enable it in settings."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isRequestingPermission = false,
                    errorMessage = e.message ?: "Error requesting permission"
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
                    errorMessage = e.message ?: "Error checking permission"
                )
            }
        }
    }

    fun startLocationTracking() {
        viewModelScope.launch {
            // Check permission first
            if (!checkLocationPermissionUseCase()) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Location permission required. Please grant permission first.",
                    isTracking = false
                )
                return@launch
            }
            
            _uiState.value = _uiState.value.copy(isTracking = true, errorMessage = null)
            
            try {
                while (_uiState.value.isTracking) {
                    val result = getLocationUseCase()
                    
                    when (result) {
                        is LocationResult.Success -> {
                            val locationEvent = InPlayEvent.LocationUpdated(result.location)
                            val locationItem = LocationItem(
                                location = result.location,
                                timestamp = getCurrentTimeMillis()
                            )
                            
                            _uiState.value = _uiState.value.copy(
                                locations = _uiState.value.locations + locationItem,
                                errorMessage = null
                            )
                        }
                        is LocationResult.Error -> {
                            _uiState.value = _uiState.value.copy(
                                errorMessage = result.message,
                                isTracking = false
                            )
                        }
                        is LocationResult.PermissionDenied -> {
                            _uiState.value = _uiState.value.copy(
                                errorMessage = "Location permission required",
                                isTracking = false,
                                hasPermission = false
                            )
                        }
                        is LocationResult.LocationDisabled -> {
                            _uiState.value = _uiState.value.copy(
                                errorMessage = "Location services disabled",
                                isTracking = false
                            )
                        }
                    }
                    
                    kotlinx.coroutines.delay(5000) // 5 second interval
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Unknown error",
                    isTracking = false
                )
            }
        }
    }
    
    fun stopLocationTracking() {
        _uiState.value = _uiState.value.copy(isTracking = false)
    }
    
    fun clearLocations() {
        _uiState.value = _uiState.value.copy(locations = emptyList())
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

data class LocationUiState(
    val isTracking: Boolean = false,
    val hasPermission: Boolean? = null, // null = unknown, true = granted, false = denied
    val isRequestingPermission: Boolean = false,
    val locations: List<LocationItem> = emptyList(),
    val errorMessage: String? = null
)

data class LocationItem(
    val location: Location,
    val timestamp: Long // Epoch milliseconds
)