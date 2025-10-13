package com.example.core_ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.core_ui.platform.DrawableProvider
import com.example.core_ui.resources.LocalDimensionResources
import org.koin.compose.koinInject

object TeeMarkerDefaults {
    @Composable
    fun getSize(): Dp {
        val dimensions = LocalDimensionResources.current
        return dimensions.iconMedium
    }
}

object FlagMarkerDefaults {
    @Composable
    fun getSize(): Dp {
        val dimensions = LocalDimensionResources.current
        return dimensions.iconSmall
    }
}

object TargetMarkerDefaults {
    @Composable
    fun getSize(): Dp {
        val dimensions = LocalDimensionResources.current
        return dimensions.iconXXLarge
    }
}

@Composable
fun TeeMarker(
    modifier: Modifier = Modifier
) {
    val drawableProvider: DrawableProvider = koinInject()
    val teeSize = TeeMarkerDefaults.getSize()

    Image(
        painter = drawableProvider.getGolfBallPainter(),
        contentDescription = "Golf Ball Tee",
        modifier = modifier
            .size(teeSize)
    )
}

@Composable
fun FlagMarker(
    modifier: Modifier = Modifier
) {
    val flagSize = FlagMarkerDefaults.getSize()

    Box(
        modifier = modifier
            .size(flagSize),
        contentAlignment = Alignment.Center
    ) {
        // Outer circle (white outline)
        Box(
            modifier = Modifier
                .size(flagSize)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = .5f))
        )

        // Inner circle (solid white center)
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

@Composable
fun TargetMarker(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val targetSize = TargetMarkerDefaults.getSize()
    
    Box(
        modifier = modifier
            .size(targetSize)
            .clickable { onClick?.invoke() },
        contentAlignment = Alignment.Center
    ) {
        // Outer circle (white outline)
        Box(
            modifier = Modifier
                .size(targetSize)
                .clip(CircleShape)
                .border(1.dp, Color.White, CircleShape)
        )
        
        // Inner circle (solid white center)
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}