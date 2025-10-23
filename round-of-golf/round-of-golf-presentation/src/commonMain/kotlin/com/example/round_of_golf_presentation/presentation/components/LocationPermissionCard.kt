package com.example.round_of_golf_presentation.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.core_ui.resources.LocalDimensionResources
import com.example.shared.utils.StringResources
import com.example.core_ui.utils.UiText

@Composable
fun LocationPermissionCard(
    modifier: Modifier = Modifier,
    isRequestingPermission: Boolean,
    onRequestPermission: () -> Unit
) {
    val dimensions = LocalDimensionResources.current

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(dimensions.paddingLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = UiText.StringResourceId(StringResources.locationPermissionRequiredTitle).asString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = UiText.StringResourceId(StringResources.locationPermissionDescription).asString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.padding(vertical = dimensions.paddingSmall)
            )
            Button(
                onClick = onRequestPermission,
                enabled = !isRequestingPermission
            ) {
                Text(
                    if (isRequestingPermission) UiText.StringResourceId(StringResources.requesting).asString()
                    else UiText.StringResourceId(StringResources.grantPermission).asString()
                )
            }
        }
    }
}