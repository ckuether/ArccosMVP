package org.example.arccosmvp.presentation

import androidx.compose.runtime.Composable
import com.example.core_ui.components.DraggableBottomSheetWrapper
import com.example.shared.data.model.Hole

@Composable
fun HoleStatsBottomSheet(
    currentHole: Hole?,
    currentHoleNumber: Int,
    totalHoles: Int,
    existingScore: Int? = null,
    onDismiss: () -> Unit,
    onFinishHole: (score: Int, putts: Int) -> Unit,
    onNavigateToHole: (holeNumber: Int) -> Unit
) {
    DraggableBottomSheetWrapper(
        onDismiss = onDismiss,
        fillMaxHeight = 0.85f
    ) {
        HoleStats(
            currentHole = currentHole,
            currentHoleNumber = currentHoleNumber,
            totalHoles = totalHoles,
            existingScore = existingScore,
            onDismiss = onDismiss,
            onFinishHole = onFinishHole,
            onNavigateToHole = onNavigateToHole
        )
    }
}