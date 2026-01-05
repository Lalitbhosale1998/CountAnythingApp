package com.lalit.countanything.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinFlipScreen(
    onBack: () -> Unit
) {
    var isHeads by remember { mutableStateOf(true) }
    var isFlipping by remember { mutableStateOf(false) }
    
    val rotationAnim = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    val neonGold = Color(0xFFFFD700)
    val bgDark = Color(0xFF101010)

    // Flip Logic (Preserved)
    fun flipCoin() {
        if (isFlipping) return
        isFlipping = true
        
        scope.launch {
            val resultIsHeads = kotlin.random.Random.nextBoolean()
            val baseSpins = 1800f 
            val current = rotationAnim.value
            
            val currentMod = current % 360
            val targetMod = if (resultIsHeads) 0f else 180f
            
            var delta = targetMod - currentMod
            if (delta < 0) delta += 360f
            
            val nextTarget = current + 1800f + delta 
            
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)

            rotationAnim.animateTo(
                targetValue = nextTarget,
                animationSpec = tween(
                    durationMillis = 2000,
                    easing = FastOutSlowInEasing
                )
            )
            
            isHeads = resultIsHeads
            isFlipping = false
            
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "PROBABILITY_ENGINE", 
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = (-0.5).sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = neonGold)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = bgDark,
                    titleContentColor = neonGold
                )
            )
        },
        containerColor = bgDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.5f))
            
            // STATUS READOUT
            Text(
                text = if (isFlipping) "STATUS: COMPUTING_TRAJECTORY..." else "STATUS: READY_FOR_INPUT",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = neonGold.copy(alpha = if (isFlipping) 1f else 0.6f)
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            // THE COIN
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(260.dp)
                    .graphicsLayer {
                        rotationY = rotationAnim.value
                        cameraDistance = 12f * density
                    }
                    .clickable(enabled = !isFlipping) { flipCoin() }
            ) {
                // Determine face based on rotation
                val angle = rotationAnim.value % 360
                val isBack = angle in 90f..270f
                
                if (isBack) {
                    Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                         TechCoin(
                             text = "FALSE",
                             subText = "TAILS",
                             color = neonGold
                         )
                    }
                } else {
                    TechCoin(
                        text = "TRUE",
                        subText = "HEADS",
                        color = neonGold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // RESULT LOG
            if (!isFlipping) {
                Text(
                    text = "RESULT: ${if(isHeads) "TRUE (HEADS)" else "FALSE (TAILS)"}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            
            // DECORATIVE BOTTOM TEXT
            Text(
                "BINARY_DECISION_UNIT // v2.0",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun TechCoin(
    text: String,
    subText: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .size(260.dp)
            .clip(CircleShape)
            .background(Color(0xFF1A1A1A))
            .border(2.dp, color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Tech Circles
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2
            
            // Dashed Ring
            drawCircle(
                color = color.copy(alpha = 0.5f),
                radius = radius - 30.dp.toPx(),
                style = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f))
                )
            )
            
            // Inner Solid Ring
             drawCircle(
                color = color.copy(alpha = 0.2f),
                radius = radius - 60.dp.toPx(),
                style = Stroke(width = 4.dp.toPx())
            )
        }
        
        // Text Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = color
            )
            
            Text(
                text = subText, 
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.labelLarge,
                color = color.copy(alpha = 0.7f)
            )
        }
    }
}
