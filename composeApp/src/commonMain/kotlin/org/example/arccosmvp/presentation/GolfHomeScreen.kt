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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.core_ui.resources.LocalDimensionResources
import com.example.shared.navigation.Route
import org.example.arccosmvp.utils.DrawableHelper

@Composable
fun GolfHomeScreen(
    navController: NavController
) {
    val dimensions = LocalDimensionResources.current
    
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
                text = "Welcome to Golf Tracker",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    navController.navigate(Route.ROUND_OF_GOLF)
                }
            ) {
                Text("Start Round")
            }
        }
    }
}