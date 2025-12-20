package com.lalit.countanything.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lalit.countanything.ui.components.AddAmountSentDialog
import com.lalit.countanything.ui.components.EditTotalSentDialog
import com.lalit.countanything.ui.components.GoalEditDialog
import androidx.compose.ui.res.stringResource
import com.lalit.countanything.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreen(
    monthlySavings: Map<String, Float>,
    goalTitle: String,
    goalPrice: Float,    goalAmountNeeded: Float,
    onSaveGoal: (title: String, price: Float, amountNeeded: Float) -> Unit,
    totalSentToIndia: Float,
    onAddToTotalSent: (Float) -> Unit,
    onSetTotalSent: (Float) -> Unit
) {
    // --- State for the Edit Dialogs ---
    var showGoalEditDialog by remember { mutableStateOf(false) }
    var showSentAmountDialog by remember { mutableStateOf(false) }

    // --- Calculations ---
    val totalSaved = monthlySavings.values.sum()
    val amountStillNeeded = (goalAmountNeeded - totalSaved).coerceAtLeast(0f)
    val goalProgress = if (goalAmountNeeded > 0) (totalSaved / goalAmountNeeded).coerceIn(0f, 1f) else 0f
    var showEditTotalSentDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp) // Use spacedBy for consistent spacing
    ) {
        // --- GOAL CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = goalTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    IconButton(onClick = { showGoalEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Goal")
                    }
                }
                Text(
                    text = stringResource(R.string.total_price, "%,.0f".format(goalPrice)),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(32.dp))
                // --- NEW CAR ANIMATION ---
                com.lalit.countanything.ui.components.CarProgress(
                    progress = goalProgress,
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    fillColor = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
                
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "${(goalProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    stringResource(R.string.amount_still_to_save),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "¥${"%,.0f".format(amountStillNeeded)}",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(R.string.total_saved_so_far, "%,.0f".format(totalSaved)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // --- NEW CARD: AMOUNT SENT TO INDIA ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showSentAmountDialog = true }, // Open dialog on click
            shape = RoundedCornerShape(28.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.money_sent_to_india),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "¥${"%,.0f".format(totalSentToIndia)}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                // --- ACTION BUTTONS ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // EDIT button to overwrite the total
                    IconButton(onClick = { showEditTotalSentDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Total Amount Sent"
                        )
                    }
                    // ADD button to add a new amount
                    IconButton(onClick = { showSentAmountDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Amount Sent",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
        }
    }

    // --- DIALOGS ---
    if (showGoalEditDialog) {
        GoalEditDialog(
            initialTitle = goalTitle,
            initialPrice = goalPrice,
            initialAmountNeeded = goalAmountNeeded,
            onDismiss = { showGoalEditDialog = false },
            onSave = { title, price, amountNeeded ->
                onSaveGoal(title, price, amountNeeded)
                showGoalEditDialog = false
            }
        )
    }
    if (showEditTotalSentDialog) {
        EditTotalSentDialog(
            initialAmount = totalSentToIndia,
            onDismiss = { showEditTotalSentDialog = false },
            onSave = { newTotal ->
                onSetTotalSent(newTotal) // Call the overwrite handler
                showEditTotalSentDialog = false
            }
        )
    }
    if (showSentAmountDialog) {
        AddAmountSentDialog(
            onDismiss = { showSentAmountDialog = false },
            onAdd = { amount ->
                onAddToTotalSent(amount)
                showSentAmountDialog = false
            }
        )
    }
}
