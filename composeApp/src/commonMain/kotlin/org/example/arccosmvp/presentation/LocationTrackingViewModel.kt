package org.example.arccosmvp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.location.domain.service.LocationTrackingService
import com.example.location.domain.usecase.LocationException
import com.example.shared.event.InPlayEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class LocationTrackingViewModel(
    private val locationTrackingService: LocationTrackingService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LocationTrackingUiState())
    val uiState: StateFlow<LocationTrackingUiState> = _uiState.asStateFlow()
    
    val isTracking = locationTrackingService.isTracking
    
    fun startLocationTracking() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                locationTrackingService.startLocationTracking()
                    .catch { throwable ->
                        val errorMessage = when (throwable) {
                            is LocationException -> throwable.message
                            else -> "Unknown error occurred"
                        }
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = errorMessage
                        )
                    }
                    .collect { locationEvent ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            lastLocationEvent = locationEvent,
                            error = null
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to start location tracking"
                )
            }
        }
    }
    
    fun stopLocationTracking() {
        viewModelScope.launch {
            try {
                locationTrackingService.stopLocationTracking()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to stop location tracking"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class LocationTrackingUiState(
    val isLoading: Boolean = false,
    val lastLocationEvent: InPlayEvent.LocationUpdated? = null,
    val error: String? = null
)