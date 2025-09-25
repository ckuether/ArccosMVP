package org.example.arccosmvp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.event.InPlayEvent
import com.example.shared.data.event.Location
import com.example.shared.domain.usecase.GetLocationUseCase
import com.example.shared.domain.model.LocationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.arccosmvp.platform.getCurrentTimeMillis

class LocationViewModel(
    private val getLocationUseCase: GetLocationUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LocationUiState())
    val uiState: StateFlow<LocationUiState> = _uiState.asStateFlow()
    
    fun startLocationTracking() {
        viewModelScope.launch {
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
                                isTracking = false
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
    val locations: List<LocationItem> = emptyList(),
    val errorMessage: String? = null
)

data class LocationItem(
    val location: Location,
    val timestamp: Long // Epoch milliseconds
)