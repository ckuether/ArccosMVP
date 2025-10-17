package com.example.round_of_golf_presentation.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core_ui.resources.LocalDimensionResources
import com.example.shared.data.model.GolfClubType

@Suppress("FrequentlyChangingValue")
@Composable
fun ClubSelectionDialog(
    modifier: Modifier = Modifier,
    onClubSelected: (GolfClubType) -> Unit,
    onDismiss: () -> Unit
) {
    val dimensions = LocalDimensionResources.current
    val clubTypes = GolfClubType.entries
    val listState = rememberLazyListState()
    var selectedIndex by remember { mutableStateOf(clubTypes.size / 2) }
    
    val itemHeight = 60.dp
    val containerHeight = 180.dp
    val visibleItemsCount = 3 // Show 3 items at a time like NumberPicker
    val centerItemIndex = visibleItemsCount / 2

    // Use snap behavior to snap to items like NumberPicker
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    // Calculate selected index based on scroll position
    val currentSelectedIndex by remember {
        derivedStateOf {
            // The selected item is the one that appears in the center (where purple background is)
            // Since we have padding items, the actual club index is firstVisibleIndex
            val actualIndex = listState.firstVisibleItemIndex.coerceIn(0, clubTypes.size - 1)
            actualIndex
        }
    }

    LaunchedEffect(currentSelectedIndex) {
        selectedIndex = currentSelectedIndex
    }

    // Helper function to handle dismissal with selected club
    val handleDismissWithSelection = {
        onClubSelected(clubTypes[selectedIndex])
        onDismiss()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { handleDismissWithSelection() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .width(300.dp)
                .clickable { }, // Prevent dismissing when clicking on card
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(dimensions.paddingLarge),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Club",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = dimensions.paddingLarge)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(containerHeight)
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxWidth(),
                        flingBehavior = snapBehavior
                    ) {
                        // Add padding items at the beginning to center the first item
                        items(centerItemIndex) {
                            Box(modifier = Modifier.height(itemHeight))
                        }
                        
                        itemsIndexed(clubTypes) { index, clubType ->
                            val isSelected = index == selectedIndex
                            val alpha = if (isSelected) 1.0f else 0.4f
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(itemHeight),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = clubType.shortName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = Color.Black.copy(alpha = alpha),
                                    fontSize = if (isSelected) 24.sp else 18.sp
                                )
                            }
                        }
                        
                        // Add padding items at the end to center the last item
                        items(centerItemIndex) {
                            Box(modifier = Modifier.height(itemHeight))
                        }
                    }
                    
                    // Fixed purple background in center position
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight)
                            .align(Alignment.Center)
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                RoundedCornerShape(8.dp)
                            )
                    )

                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensions.paddingLarge),
                    horizontalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
                ) {
                    OutlinedButton(
                        onClick = handleDismissWithSelection,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = handleDismissWithSelection,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Select")
                    }
                }
            }
        }
    }
}