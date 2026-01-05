package com.lalit.countanything.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lalit.countanything.ui.components.BouncyButton
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import kotlinx.coroutines.delay

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import com.lalit.countanything.R

@Composable
fun WelcomeScreen(onContinueClicked: () -> Unit) {
    val haptics = LocalHapticFeedback.current
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startAnimation = true
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets(0, 0, 0, 0) // Edge-to-edge
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // --- TOP HERO SECTION (65% Height) ---
            Box(
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 24.dp) // Fixed padding syntax
                    .clip(RoundedCornerShape(32.dp)) // Round all corners
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                            )
                        )
                    )
            ) {
                // Determine layout based on orientation or just center
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val infiniteTransition = rememberInfiniteTransition(label = "FloatingAssets")
                    
                    val floatAnim by infiniteTransition.animateFloat(
                        initialValue = -12f,
                        targetValue = 12f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2500, easing = LinearOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "FloatAnim"
                    )

                    val swayAnim by infiniteTransition.animateFloat(
                        initialValue = -8f,
                        targetValue = 8f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(3500, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "SwayAnim"
                    )

                    val scaleAnim by infiniteTransition.animateFloat(
                        initialValue = 0.95f,
                        targetValue = 1.05f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(4000, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "ScaleAnim"
                    )

                    AnimatedVisibility(
                        visible = startAnimation,
                        enter = scaleIn(tween(800, delayMillis = 200)) + fadeIn()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // --- HABITS ORB (CENTER) ---
                            GlassOrb(
                                icon = Icons.Default.AutoAwesome,
                                size = 160.dp,
                                mainColor = Color(0xFFFF5252),
                                secondaryColor = Color(0xFFFF1744),
                                modifier = Modifier
                                    .graphicsLayer {
                                        translationY = floatAnim
                                        rotationZ = swayAnim * 0.5f
                                        scaleX = scaleAnim
                                        scaleY = scaleAnim
                                    }
                            )
                            
                            // --- STUDY ORB (TOP RIGHT) ---
                            GlassOrb(
                                icon = Icons.Default.School,
                                size = 80.dp,
                                mainColor = Color(0xFF448AFF),
                                secondaryColor = Color(0xFF2979FF),
                                modifier = Modifier
                                    .offset(x = 100.dp, y = (-90).dp)
                                    .graphicsLayer {
                                        translationY = -floatAnim
                                        rotationZ = swayAnim
                                    }
                            )
                            
                            // --- FINANCE ORB (BOTTOM LEFT) ---
                            GlassOrb(
                                icon = Icons.Default.AccountBalanceWallet,
                                size = 80.dp,
                                mainColor = Color(0xFFFFD740),
                                secondaryColor = Color(0xFFFFAB40),
                                modifier = Modifier
                                    .offset(x = (-100).dp, y = 90.dp)
                                    .graphicsLayer {
                                        translationY = floatAnim * 0.8f
                                        rotationZ = -swayAnim * 1.5f
                                    }
                            )
                        }
                    }
                }
            }

            // --- BOTTOM CONTENT SECTION (35% Height) ---
            Column(
                modifier = Modifier
                    .weight(0.35f)
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Text Block
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AnimatedVisibility(
                        visible = startAnimation,
                        enter = slideInVertically(tween(600, delayMillis = 400)) { 50 } + fadeIn()
                    ) {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-2).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    AnimatedVisibility(
                        visible = startAnimation,
                        enter = slideInVertically(tween(600, delayMillis = 600)) { 50 } + fadeIn()
                    ) {
                        Text(
                            text = stringResource(R.string.welcome_subtitle),
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Button Block
                AnimatedVisibility(
                    visible = startAnimation,
                    enter = scaleIn(tween(500, delayMillis = 900)) + fadeIn()
                ) {
                    Button(
                        onClick = { 
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onContinueClicked() 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = CircleShape // Pill shape
                    ) {
                        Text(
                            text = stringResource(R.string.welcome_get_started),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
fun GlassOrb(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    size: androidx.compose.ui.unit.Dp,
    mainColor: Color,
    secondaryColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // --- Radial Glow ---
        Box(
            modifier = Modifier
                .size(size * 2)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            mainColor.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
        )

        // --- The Orb Body ---
        Surface(
            modifier = Modifier.size(size),
            shape = CircleShape,
            color = Color.Transparent,
            border = androidx.compose.foundation.BorderStroke(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.6f),
                        Color.White.copy(alpha = 0.1f)
                    )
                )
            ),
            shadowElevation = 8.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                mainColor.copy(alpha = 0.8f),
                                secondaryColor.copy(alpha = 0.4f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(size * 0.5f),
                    tint = Color.White
                )
            }
        }
    }
}
