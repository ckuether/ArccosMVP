package org.example.arccosmvp.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.core_ui.components.RoundedButton
import com.example.core_ui.resources.LocalDimensionResources
import com.example.shared.navigation.Route
import org.example.arccosmvp.presentation.viewmodel.AppViewModel
import org.example.arccosmvp.utils.DrawableHelper

@Composable
fun GolfHomeScreen(
    navController: NavController,
    appViewModel: AppViewModel
) {
    val dimensions = LocalDimensionResources.current
    val allScoreCards by appViewModel.allScoreCards.collectAsStateWithLifecycle(emptyList())
    val course by appViewModel.course.collectAsStateWithLifecycle()
    var showPreviousRounds by remember { mutableStateOf(false) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = DrawableHelper.golfBackground(),
            contentDescription = "Golf course background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .safeContentPadding()
                .padding(dimensions.paddingLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to Broken Tee",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            RoundedButton(
                modifier = Modifier
                    .padding(vertical = dimensions.paddingMedium),
                text = if (course == null) "Loading Course..." else "Start Round",
                enabled = course != null,
                onClick = {
                    navController.navigate(Route.ROUND_OF_GOLF)
                }
            )


            RoundedButton(
                modifier = Modifier
                    .padding(vertical = dimensions.paddingMedium),
                text = "Past Rounds",
                onClick = {
                    showPreviousRounds = true
                }
            )
        }
        
        // Previous Rounds Bottom Sheet
        if (showPreviousRounds) {
            PreviousRoundsBottomSheet(
                scoreCards = allScoreCards,
                onDismiss = { showPreviousRounds = false }
            )
        }
    }
}