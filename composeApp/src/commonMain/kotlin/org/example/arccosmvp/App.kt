package org.example.arccosmvp

import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.example.core_ui.theme.GolfAppTheme
import org.example.arccosmvp.presentation.RoundOfGolf

@Composable
@Preview
fun App() {
    GolfAppTheme {
        RoundOfGolf()
    }
}

