package com.lalit.countanything.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun HolidayInputDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int, Float) -> Unit
) {
    var holidays by remember { mutableStateOf("0") }
    var trainFare by remember { mutableStateOf("0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Salary Details") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("1. Holidays (excluding weekends)?")
                    OutlinedTextField(
                        value = holidays,
                        onValueChange = { if (it.all { char -> char.isDigit() }) holidays = it },
                        label = { Text("Number of Holidays") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("2. One-way Train Fare?")
                    OutlinedTextField(
                        value = trainFare,
                        onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) trainFare = it },
                        label = { Text("Fare Amount (e.g. 200)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        "We'll double this (round trip) × working days.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val holidaysInt = holidays.toIntOrNull() ?: 0
                    val fareFloat = trainFare.toFloatOrNull() ?: 0f
                    onConfirm(holidaysInt, fareFloat)
                }
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
