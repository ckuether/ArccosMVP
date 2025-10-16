package org.example.arccosmvp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.example.core_ui.theme.GolfAppTheme
import com.example.shared.navigation.Route
import org.example.arccosmvp.presentation.GolfHomeScreen
import org.example.arccosmvp.presentation.RoundOfGolf
import org.example.arccosmvp.presentation.viewmodel.AppViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    GolfAppTheme {
        val navController = rememberNavController()
        val appViewModel: AppViewModel = koinViewModel()
        val snackbarHostState = remember { SnackbarHostState() }
        
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Box(
                modifier = androidx.compose.ui.Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
            ) {
                NavHost(
                    navController = navController,
                    startDestination = Route.GOLF_HOME
                ) {
                    composable(Route.GOLF_HOME) {
                        GolfHomeScreen(
                            navController = navController,
                            appViewModel = appViewModel
                        )
                    }
                    composable(Route.ROUND_OF_GOLF) {
                        val course by appViewModel.course.collectAsStateWithLifecycle()
                        val currentPlayer by appViewModel.currentPlayer.collectAsStateWithLifecycle()
                        if (course != null && currentPlayer != null) {
                            RoundOfGolf(
                                currentPlayer = currentPlayer!!,
                                golfCourse = course!!,
                                snackbarHostState = snackbarHostState
                            )
                        }
                    }
                }
            }
        }
    }
}

