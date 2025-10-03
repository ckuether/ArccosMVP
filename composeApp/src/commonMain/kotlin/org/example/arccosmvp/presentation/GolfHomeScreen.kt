package org.example.arccosmvp.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import org.example.arccosmvp.utils.DrawableHelper

@Composable
fun GolfHomeScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = DrawableHelper.golfBackground(),
            contentDescription = "Golf course background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}