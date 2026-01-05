package com.lalit.countanything.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
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

    // Cyberpunk Theme Colors
    val NeonRed = Color(0xFFFF5252)
    val NeonBlue = Color(0xFF448AFF)
    val BgDark = Color(0xFF101010)
    
    // Active Color based on Type (Shrine=Red, Temple=Blue/Cyan for difference? Or stick to Red as planned?)
    // Let's use Red for Shrine (Torii gates are red) and maybe Gold/Purple for Temple? 
    // Plan said Red Neon. Let's stick to Red for general, maybe shift slightly for Temple.
    val activeColor = if (placeType == PlaceType.SHRINE) NeonRed else Color(0xFFFF9100) // Orange for Temple

    val shrineSteps = listOf(
        GuideStep("TORII_GATE", "PERIMETER_CHECK", "BOW ONCE BEFORE ENTRY.", "⛩️"),
        GuideStep("TEMIZUYA", "DECONTAMINATION", "WASH LEFT, RIGHT, MOUTH.", "💧"),
        GuideStep("OFFERING", "CURRENCY_TRANSFER", "DISPENSE COIN QUIETLY.", "🪙"),
        GuideStep("RING_BELL", "SIGNAL_DEITY", "ACTIVATE BELL MECHANISM.", "🔔"),
        GuideStep("TWO_BOWS", "RESPECT_PROTOCOL_1", "EXECUTE 90° BOW (x2).", "🙇"),
        GuideStep("TWO_CLAPS", "RESPECT_PROTOCOL_2", "PERCUSSIVE SIGNAL (x2).", "👏"),
        GuideStep("PRAY", "DATA_UPLOAD", "TRANSMIT PRAYER SILENTLY.", "🙏"),
        GuideStep("ONE_BOW", "TERMINATION", "EXECUTE FINAL BOW.", "🙇")
    )

    val templeSteps = listOf(
        GuideStep("SANMON_GATE", "PERIMETER_CHECK", "BOW + CROSS THRESHOLD.", "🏯"),
        GuideStep("TEMIZUYA", "DECONTAMINATION", "WASH HANDS + MOUTH.", "💧"),
        GuideStep("INCENSE", "SMOKE_PURGE", "APPLY SMOKE TO CHASSIS.", "💨"),
        GuideStep("OFFERING", "CURRENCY_TRANSFER", "DISPENSE COIN.", "🪙"),
        GuideStep("PRAY", "DATA_UPLOAD", "SILENT PRAYER. [NO CLAPPING].", "🙏"),
        GuideStep("ONE_BOW", "TERMINATION", "EXECUTE FINAL BOW.", "🙇")
    )

    val currentSteps = if (placeType == PlaceType.SHRINE) shrineSteps else templeSteps
    val currentStep = currentSteps.getOrElse(currentStepIndex) { currentSteps.last() }

    LaunchedEffect(placeType) { currentStepIndex = 0 }

    val progress by animateFloatAsState(targetValue = (currentStepIndex + 1).toFloat() / currentSteps.size)

    Scaffold(
        containerColor = BgDark,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "PROTOCOL_DATABASE", 
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = (-0.5).sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = activeColor)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BgDark,
                    titleContentColor = activeColor
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
                    .height(48.dp)
                    .border(1.dp, activeColor, CutCornerShape(8.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TechTabButton(
                    text = "SHRINE [JINJA]",
                    isSelected = placeType == PlaceType.SHRINE,
                    activeColor = NeonRed,
                    modifier = Modifier.weight(1f),
                    onClick = { placeType = PlaceType.SHRINE }
                )
                Box(Modifier.width(1.dp).fillMaxHeight().background(activeColor))
                TechTabButton(
                    text = "TEMPLE [OTERA]",
                    isSelected = placeType == PlaceType.TEMPLE,
                    activeColor = Color(0xFFFF9100),
                    modifier = Modifier.weight(1f),
                    onClick = { placeType = PlaceType.TEMPLE }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tech Progress Bar
            Column(Modifier.fillMaxWidth()) {
                Text(
                    "SEQUENCE_PROGRESS: ${(progress * 100).toInt()}%",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = activeColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = activeColor,
                    trackColor = activeColor.copy(alpha = 0.2f)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            // Step Card (Holographic)
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    fadeIn() + slideInHorizontally { it/2 } togetherWith fadeOut() + slideOutHorizontally { -it/2 }
                },
                label = "StepTransition"
            ) { step ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.85f)
                        .clip(CutCornerShape(bottomEnd = 32.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(activeColor.copy(alpha = 0.1f), Color.Transparent),
                                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                end = androidx.compose.ui.geometry.Offset(500f, 500f)
                            )
                        )
                        .border(1.dp, activeColor.copy(alpha = 0.5f), CutCornerShape(bottomEnd = 32.dp))
                ) {
                    // Hologram lines
                    Column(Modifier.fillMaxSize()) {
                        repeat(10) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(activeColor.copy(alpha = 0.05f))
                            )
                            Spacer(Modifier.weight(1f))
                        }
                    }

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
                            text = "[ ${step.title} ]",
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = activeColor,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "> ${step.description}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(activeColor.copy(alpha = 0.1f))
                                .padding(16.dp)
                        ) {
                             Text(
                                text = step.instruction,
                                fontFamily = FontFamily.Monospace,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    
                    // Decorative corners
                    Box(Modifier.size(10.dp).background(activeColor).align(Alignment.TopStart))
                    Box(Modifier.size(10.dp).background(activeColor).align(Alignment.TopEnd))
                    Box(Modifier.size(10.dp).background(activeColor).align(Alignment.BottomStart))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back Button
                 Button(
                    onClick = { if (currentStepIndex > 0) currentStepIndex-- },
                    enabled = currentStepIndex > 0,
                    modifier = Modifier.springyTouch(),
                    shape = CutCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    ),
                    border = BorderStroke(1.dp, if(currentStepIndex > 0) activeColor else Color.Gray)
                ) {
                    Text("<< PREV", fontFamily = FontFamily.Monospace, color = if(currentStepIndex > 0) activeColor else Color.Gray)
                }

                // Next Button
                Button(
                    onClick = {
                        if (currentStepIndex < currentSteps.size - 1) {
                            currentStepIndex++
                        } else {
                            onBack()
                        }
                    },
                    modifier = Modifier.springyTouch(),
                    shape = CutCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = activeColor
                    )
                ) {
                    Text(
                        if (currentStepIndex < currentSteps.size - 1) "NEXT >>" else "COMPLETE", 
                        fontFamily = FontFamily.Monospace, 
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun TechTabButton(
    text: String,
    isSelected: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(if (isSelected) activeColor.copy(alpha = 0.2f) else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) activeColor else Color.Gray,
            fontSize = 12.sp
        )
    }
}
