package com.lalit.countanything.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
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

    // Flip Logic
    fun flipCoin() {
        if (isFlipping) return
        isFlipping = true
        
        scope.launch {
            val resultIsHeads = kotlin.random.Random.nextBoolean()
            
            // 5 full spins (1800) + target
            val baseSpins = 1800f 
            // If current is Heads (0), target Heads is 0 + 1800 = 1800
            // If current is Heads (0), target Tails is 180 + 1800 = 1980
            // logic relative to current rotation to keep adding up
            
            val current = rotationAnim.value
            // We want to land on a multiple of 360 for Heads, 180+360*N for Tails
            // But visually 0 and 360 are same.
            
            val targetRotation = if (resultIsHeads) 0f else 180f
            // We need to add enough 360s to current to make it spin
            // Find next multiple of 360 greater than current + baseSpins
            
            val minimumTarget = current + baseSpins
            // Adjustment to land on 0 or 180
            // If we want 0 (Heads):
            // next target = (minimumTarget round up to next 360) 
            // If we want 180 (Tails):
            // next target = (minimumTarget round up to next 360) + 180?
            
            // Simpler: Just add 1800 + delta
            // current % 360 gives us current position (0 or 180 essentially)
            // If result (0/180) != current % 360, add 180 extra.
            
            val currentMod = current % 360
            val targetMod = if (resultIsHeads) 0f else 180f
            
            var delta = targetMod - currentMod
            if (delta < 0) delta += 360f
            
            val nextTarget = current + 1800f + delta // 1800 is 5 full spins
            
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
                title = { Text("Coin Flip", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(0.5f))
            
            // INSTRUCTION
            Text(
                text = if (isFlipping) "Flipping..." else "Touch the coin to flip",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            // THE COIN
            // We use graphicsLayer for 3D rotation
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
                // 90-270 is Back
                val angle = rotationAnim.value % 360
                val isBack = angle in 90f..270f
                
                if (isBack) {
                    // Back Face (Tails)
                    // We must rotate content 180Y locally so it's not mirrored when seen from "behind" the container
                    Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                         PremiumCoin(
                             text = "TAILS",
                             subText = "ONE DOLLAR",
                             isHeads = false
                         )
                    }
                } else {
                    // Front Face (Heads)
                    PremiumCoin(
                        text = "HEADS",
                        subText = "LIBERTY",
                        isHeads = true
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun PremiumCoin(
    text: String,
    subText: String,
    isHeads: Boolean
) {
    // Gold Palettes
    val goldLight = Color(0xFFFFE672)
    val goldMedium = Color(0xFFFFD700)
    val goldDark = Color(0xFFC59200)
    val goldShadow = Color(0xFF8B6500)

    val gradient = Brush.radialGradient(
        colors = listOf(goldLight, goldMedium, goldDark),
        center = Offset.Unspecified,
        radius = 300f
    )
    
    val sheenGradient = Brush.linearGradient(
        colors = listOf(
            goldShadow.copy(alpha = 0.4f),
            Color.Transparent,
            Color.White.copy(alpha = 0.4f),
            Color.Transparent,
            goldShadow.copy(alpha = 0.4f)
        ),
        start = Offset(0f, 0f),
        end = Offset(100f, 1000f) // Diagonal sheen
    )

    Box(
        modifier = Modifier
            .size(260.dp)
            .shadow(elevation = 12.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        // Shine Overlay
         Box(
            modifier = Modifier
                .fillMaxSize()
                .background(sheenGradient)
        )
        
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val center = Offset(w / 2, h / 2)
            
            // 1. Milled Edge (Ridges)
            drawCircle(
                color = goldShadow,
                radius = w / 2 - 4.dp.toPx(),
                style = Stroke(
                    width = 8.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                )
            )
            
            // 2. Inner Ring
            drawCircle(
                color = goldDark,
                radius = w / 2 - 20.dp.toPx(),
                style = Stroke(width = 2.dp.toPx())
            )
            
            // 3. Center Decoration (Star or simple geometry)
            if (isHeads) {
                // Star for Heads
                // Simplified drawing or stick to text
            }
        }
        
        // Text Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = goldShadow,
                        offset = Offset(2f, 2f),
                        blurRadius = 2f
                    )
                ),
                color = goldDark
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "★ $subText ★", // Add stars
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                ),
                color = goldShadow
            )
        }
    }
}
