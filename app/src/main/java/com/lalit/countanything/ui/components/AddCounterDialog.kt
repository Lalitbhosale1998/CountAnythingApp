package com.lalit.countanything.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lalit.countanything.ui.models.CounterType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCounterDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, CounterType) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(CounterType.STANDARD) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "New Tracker") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "What do you want to track?")
                
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tracker Name") },
                    placeholder = { Text("e.g. Daily Meditation") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Type",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = selectedType == CounterType.STANDARD,
                        onClick = { selectedType = CounterType.STANDARD },
                        label = { Text("Standard") },
                        leadingIcon = if (selectedType == CounterType.STANDARD) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                        } else null
                    )
                    FilterChip(
                        selected = selectedType == CounterType.SEXUAL_HEALTH,
                        onClick = { selectedType = CounterType.SEXUAL_HEALTH },
                        label = { Text("Sexual Health") },
                        leadingIcon = if (selectedType == CounterType.SEXUAL_HEALTH) {
                            { Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                        } else null
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, selectedType)
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
