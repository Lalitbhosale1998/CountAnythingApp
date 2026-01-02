package com.lalit.countanything.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lalit.countanything.ui.models.CounterType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFinanceTrackerDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, CounterType, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(CounterType.FINANCE_CUMULATIVE) }
    var targetDate by remember { mutableStateOf(LocalDate.now().plusDays(30)) }
    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add Finance Tracker") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tracker Name (e.g., Rent, Car Fund)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Select Type:", style = MaterialTheme.typography.titleSmall)
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TypeOption(
                        title = "Cumulative Total",
                        description = "Add/Edit a running total (like India Remittance)",
                        selected = selectedType == CounterType.FINANCE_CUMULATIVE,
                        onClick = { selectedType = CounterType.FINANCE_CUMULATIVE }
                    )
                    TypeOption(
                        title = "Date Countdown",
                        description = "Days until a target date (like Salary)",
                        selected = selectedType == CounterType.FINANCE_COUNTDOWN,
                        onClick = { selectedType = CounterType.FINANCE_COUNTDOWN }
                    )
                    TypeOption(
                        title = "Monthly Budget Hub",
                        description = "Track Salary/Savings per month",
                        selected = selectedType == CounterType.FINANCE_BUDGET_HUB,
                        onClick = { selectedType = CounterType.FINANCE_BUDGET_HUB }
                    )
                }

                if (selectedType == CounterType.FINANCE_COUNTDOWN) {
                    Button(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Target Date: ${targetDate.format(DateTimeFormatter.ISO_LOCAL_DATE)}")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val dateStr = if (selectedType == CounterType.FINANCE_COUNTDOWN) targetDate.toString() else null
                        onConfirm(title, selectedType, dateStr)
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

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = targetDate.toEpochDay() * 24 * 60 * 60 * 1000
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        targetDate = LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun TypeOption(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = description, style = MaterialTheme.typography.bodySmall)
        }
    }
}
