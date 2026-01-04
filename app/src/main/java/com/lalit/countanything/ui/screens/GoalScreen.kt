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
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.ui.unit.sp


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
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- HEADER ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = goalTitle.ifEmpty { "My Goal" },
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    
                    FilledTonalIconButton(
                        onClick = { showGoalEditDialog = true },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Goal")
                    }
                }
                
                Spacer(modifier = Modifier.height(48.dp))

                // --- HERO PROGRESS RING ---
                Box(contentAlignment = Alignment.Center) {
                    // Background Track
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.size(260.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        strokeWidth = 24.dp,
                        trackColor = Color.Transparent,
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                    
                    // Active Progress
                    CircularProgressIndicator(
                        progress = { goalProgress },
                        modifier = Modifier.size(260.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 24.dp,
                        trackColor = Color.Transparent,
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${(goalProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 64.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.finance_savings_label).uppercase(),
                            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(56.dp))

                // --- BENTO STATS GRID ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Column: Total & Saved
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DreamStatCard(
                            label = stringResource(R.string.total_price, "").replace(":", ""),
                            value = "$currencySymbol${"%,.0f".format(goalPrice)}",
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            textColor = MaterialTheme.colorScheme.onSurface,
                            privacyModeEnabled = isPrivacyModeEnabled
                        )
                        
                        DreamStatCard(
                            label = "Saved",
                            value = "$currencySymbol${"%,.0f".format(totalSaved)}",
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            textColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            privacyModeEnabled = isPrivacyModeEnabled
                        )
                    }

                    // Right Column: Remaining (Tall)
                    DreamStatCard(
                        label = stringResource(R.string.amount_still_to_save),
                        value = "$currencySymbol${"%,.0f".format(amountStillNeeded)}",
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        textColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        icon = Icons.Default.Add, // Placeholder or arrow
                        modifier = Modifier.weight(1f).height(180.dp),
                        isGiant = true,
                        privacyModeEnabled = isPrivacyModeEnabled
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

@Composable
fun DreamStatCard(
    label: String,
    value: String,
    color: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    icon:  androidx.compose.ui.graphics.vector.ImageVector? = null,
    isGiant: Boolean = false,
    privacyModeEnabled: Boolean = false
) {
    Card(
        modifier = modifier.springyTouch(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = if (isGiant) Arrangement.SpaceBetween else Arrangement.Center,
            horizontalAlignment = if (isGiant) Alignment.Start else Alignment.Start
        ) {
            Column {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = textColor.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                SensitiveText(
                    text = value,
                    style = if (isGiant) 
                        MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black)
                    else 
                        MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = textColor,
                    privacyModeEnabled = privacyModeEnabled
                )
            }
            
            if (isGiant && icon != null) {
               Box(
                   modifier = Modifier
                        .size(48.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(textColor.copy(alpha = 0.1f)),
                   contentAlignment = Alignment.Center
               ) {
                   Icon(icon, contentDescription = null, tint = textColor)
               }
            }
        }
    }
}
