package com.lalit.countanything.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.TempleHindu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lalit.countanything.ui.components.springyTouch

enum class PlaceType {
    SHRINE, TEMPLE
}

data class GuideStep(
    val title: String,
    val description: String,
    val instruction: String,
    val iconEmoji: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShrineGuideScreen(
    onBack: () -> Unit
) {
    var placeType by remember { mutableStateOf(PlaceType.SHRINE) }
    var currentStepIndex by remember { mutableIntStateOf(0) }

    val shrineSteps = listOf(
        GuideStep("Torii Gate", "Before entering", "Bow once before passing through.", "⛩️"),
        GuideStep("Temizuya", "Purify yourself", "Wash left hand, right hand, then mouth.", "💧"),
        GuideStep("Offer Coin", "Saisen", "Quietly toss a coin into the box.", "🪙"),
        GuideStep("Ring Bell", "Wake the Kami", "Shake the rope firmly to ring the bell.", "🔔"),
        GuideStep("Two Bows", "Respect", "Bow deeply twice (90 degrees).", "🙇"),
        GuideStep("Two Claps", "Signaling", "Clap your hands twice firmly.", "👏"),
        GuideStep("Pray", "Make your wish", "Keep hands together and pray silently.", "🙏"),
        GuideStep("One Bow", "Farewell", "Bow deeply once more before leaving.", "🙇")
    )

    val templeSteps = listOf(
        GuideStep("Sanmon Gate", "Before entering", "Bow and step over the threshold (don't step on it).", "🏯"),
        GuideStep("Temizuya", "Purify yourself", "Wash hands and mouth.", "💧"),
        GuideStep("Incense", "Osenko", "Light incense and wave smoke towards yourself.", "💨"),
        GuideStep("Offer Coin", "Saisen", "Quietly toss a coin.", "🪙"),
        GuideStep("Pray", "Make your wish", "Put hands together silently. DO NOT CLAP.", "🙏"),
        GuideStep("One Bow", "Farewell", "Bow slightly before leaving.", "🙇")
    )

    val currentSteps = if (placeType == PlaceType.SHRINE) shrineSteps else templeSteps
    val currentStep = currentSteps.getOrElse(currentStepIndex) { currentSteps.last() }

    // Reset steps when switching types
    LaunchedEffect(placeType) {
        currentStepIndex = 0
    }

    val progress by animateFloatAsState(
        targetValue = (currentStepIndex + 1).toFloat() / currentSteps.size,
        label = "Progress"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Spirit Guide") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Type Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TabButton(
                    text = "Shrine (Jinja)",
                    isSelected = placeType == PlaceType.SHRINE,
                    modifier = Modifier.weight(1f),
                    onClick = { placeType = PlaceType.SHRINE }
                )
                TabButton(
                    text = "Temple (Otera)",
                    isSelected = placeType == PlaceType.TEMPLE,
                    modifier = Modifier.weight(1f),
                    onClick = { placeType = PlaceType.TEMPLE }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Progress Indicator
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            // Step Card
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    fadeIn() + slideInHorizontally { it } togetherWith fadeOut() + slideOutHorizontally { -it }
                },
                label = "StepTransition"
            ) { step ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.8f) // Portrait aspect ratio
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(32.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = if (placeType == PlaceType.SHRINE) Color(0xFFE3F2FD) else Color(0xFFFBE9E7)
                    ),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = step.iconEmoji,
                            fontSize = 80.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = step.title,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = step.description,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = step.instruction,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentStepIndex > 0) {
                    Button(
                        onClick = { currentStepIndex-- },
                        modifier = Modifier.springyTouch(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("Back")
                    }
                } else {
                    Spacer(Modifier.width(1.dp)) // Placeholder
                }

                Button(
                    onClick = {
                        if (currentStepIndex < currentSteps.size - 1) {
                            currentStepIndex++
                        } else {
                            onBack()
                        }
                    },
                    modifier = Modifier.springyTouch(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (placeType == PlaceType.SHRINE) Color(0xFF2196F3) else Color(0xFFFF5722)
                    ),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    if (currentStepIndex < currentSteps.size - 1) {
                        Text("Next Step")
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, null)
                    } else {
                        Text("Finish")
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.Check, null)
                    }
                }
            }
        }
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(24.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.background else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
