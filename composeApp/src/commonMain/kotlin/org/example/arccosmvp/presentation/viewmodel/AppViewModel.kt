package org.example.arccosmvp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.model.Course
import com.example.shared.data.model.Player
import com.example.shared.domain.usecase.GetAllScoreCardsUseCase
import com.example.shared.domain.usecase.LoadGolfCourseUseCase
import com.example.shared.domain.usecase.LoadCurrentUserUseCase
import com.example.shared.platform.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(
    private val loadGolfCourseUseCase: LoadGolfCourseUseCase,
    private val loadCurrentUserUseCase: LoadCurrentUserUseCase,
    private val getAllScoreCardsUseCase: GetAllScoreCardsUseCase,
    private val logger: Logger
) : ViewModel() {
    
    companion object {
        private const val TAG = "AppViewModel"
    }
    
    private val _course = MutableStateFlow<Course?>(null)
    val course: StateFlow<Course?> = _course.asStateFlow()

    private val _currentPlayer = MutableStateFlow<Player?>(null)
    val currentPlayer: StateFlow<Player?> = _currentPlayer.asStateFlow()

    val allScoreCards = getAllScoreCardsUseCase()

    init {
        loadGolfCourse()
        loadCurrentUser()
    }
    
    private fun loadGolfCourse() {
        viewModelScope.launch {
            loadGolfCourseUseCase().fold(
                onSuccess = { course ->
                    _course.value = course
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
}