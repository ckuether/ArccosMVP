package org.example.arccosmvp.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import com.example.shared.data.model.Hole
import kotlin.math.roundToInt

@Composable
fun DraggableScoreCardBottomSheet(
    currentHole: Hole?,
    currentHoleNumber: Int,
    totalHoles: Int,
    onDismiss: () -> Unit,
    onFinishHole: (score: Int, putts: Int) -> Unit,
    onNavigateToHole: (holeNumber: Int) -> Unit
) {
    val density = LocalDensity.current
    val screenHeight = with(density) { 800.dp.toPx() } // Approximate screen height
    val sheetHeight = screenHeight * 0.8f
    
    var dragOffsetY by remember { mutableStateOf(0f) }
    val dismissThreshold = sheetHeight * 0.3f // Dismiss if dragged down 30% of sheet height
    
    // Simple entrance animation - starts from bottom and slides to 0
    val entranceOffset by animateFloatAsState(
        targetValue = 0f,
        animationSpec = tween(durationMillis = 300),
        label = "entranceAnimation"
    )
    
    // Combine entrance animation with drag offset
    val totalOffset = entranceOffset + dragOffsetY
    
    // Background overlay
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() }
    ) {
        // Bottom sheet content
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .offset { IntOffset(0, totalOffset.roundToInt()) }
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(Color.White)
                .clickable { /* Prevent click through */ }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            if (dragOffsetY > dismissThreshold) {
                                onDismiss()
                            } else {
                                dragOffsetY = 0f
                            }
                        }
                    ) { _, dragAmount ->
                        val newOffset = dragOffsetY + dragAmount.y
                        // Only allow dragging down (positive Y)
                        dragOffsetY = if (newOffset > 0) newOffset else 0f
                    }
                }
        ) {
            Column {
                // Drag handle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .background(
                                Color.Gray.copy(alpha = 0.5f),
                                RoundedCornerShape(2.dp)
                            )
                    )
                }
                
                // Score card content
                ScoreCard(
                    currentHole = currentHole,
                    currentHoleNumber = currentHoleNumber,
                    totalHoles = totalHoles,
                    onDismiss = onDismiss,
                    onFinishHole = onFinishHole,
                    onNavigateToHole = onNavigateToHole
                )
            }
        }
    }
}