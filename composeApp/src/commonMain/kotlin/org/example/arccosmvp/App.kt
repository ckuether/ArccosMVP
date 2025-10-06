package org.example.arccosmvp

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.example.core_ui.theme.GolfAppTheme
import com.example.shared.navigation.Route
import org.example.arccosmvp.presentation.GolfHomeScreen
import org.example.arccosmvp.presentation.RoundOfGolf

@Composable
@Preview
fun App() {
    GolfAppTheme {
        val navController = rememberNavController()
        
        NavHost(
            navController = navController,
            startDestination = Route.GOLF_HOME
        ) {
            composable(Route.GOLF_HOME) {
                GolfHomeScreen(navController = navController)
            }
            composable(Route.ROUND_OF_GOLF) {
                RoundOfGolf()
            }
        }
    }
}

