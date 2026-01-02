package com.lalit.countanything.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lalit.countanything.ui.components.GoalEditDialog
import com.lalit.countanything.ui.components.AnimatedColumn
import com.lalit.countanything.ui.components.springyTouch
import androidx.compose.ui.res.stringResource
import com.lalit.countanything.R
import com.lalit.countanything.ui.components.AddAmountSentDialog
import com.lalit.countanything.ui.components.AnimatedItem
import com.lalit.countanything.ui.components.EditTotalSentDialog
import com.lalit.countanything.ui.components.SensitiveText
import com.lalit.countanything.ui.components.WavyProgressBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreen(
    monthlySavings: Map<String, Float>,
    goalTitle: String,
    goalPrice: Float,
    goalAmountNeeded: Float,
    currencySymbol: String,
    isPrivacyModeEnabled: Boolean,
    onUpdateGoal: (title: String, price: Float, amountNeeded: Float) -> Unit
) {
    // --- State for the Edit Dialogs ---
    var showGoalEditDialog by remember { mutableStateOf(false) }

    // --- Calculations ---
    val totalSaved = monthlySavings.values.sum()
    val amountStillNeeded = (goalAmountNeeded - totalSaved).coerceAtLeast(0f)
    val goalProgress = if (goalAmountNeeded > 0) (totalSaved / goalAmountNeeded).coerceIn(0f, 1f) else 0f

    AnimatedColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        AnimatedItem(index = 0) {
            // --- GOAL CARD ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .springyTouch(),
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
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = Color(0xFFFFAB40) // Sunset Orange
                            ),
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
                    
                    // --- WAVY PROGRESS ---
                    WavyProgressBar(
                        progress = goalProgress,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(Modifier.height(16.dp))
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
                    SensitiveText(
                        text = "$currencySymbol${"%,.0f".format(amountStillNeeded)}",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF00E676) // Keep Vibrant Emerald
                        ),
                        privacyModeEnabled = isPrivacyModeEnabled
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        stringResource(R.string.total_saved_so_far, "%,.0f".format(totalSaved)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // --- BOTTOM SPACER FOR BREATHING ROOM ---
        Spacer(modifier = Modifier.height(80.dp))
    } // End of AnimatedColumn

    // --- DIALOGS ---
    if (showGoalEditDialog) {
        GoalEditDialog(
            initialTitle = goalTitle,
            initialPrice = goalPrice,
            initialAmountNeeded = goalAmountNeeded,
            onDismiss = { showGoalEditDialog = false },
            onSave = { title, price, amountNeeded ->
                onUpdateGoal(title, price, amountNeeded)
                showGoalEditDialog = false
            }
        )
    }
}
