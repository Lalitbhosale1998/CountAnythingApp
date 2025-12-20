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
import com.airbnb.lottie.compose.*
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import kotlinx.coroutines.delay

import androidx.compose.ui.res.stringResource
import com.lalit.countanything.R

@Composable
fun WelcomeScreen(onContinueClicked: () -> Unit) {
    var showLottie by remember { mutableStateOf(false) }
    var showText by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showLottie = true
        delay(400)
        showText = true
        delay(400)
        showButton = true
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.welcome))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // --- 1. LOTTIE HERO ---
        AnimatedVisibility(
            visible = showLottie,
            enter = fadeIn(tween(1000)) + slideInVertically(tween(1000)) { -100 }
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(280.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 2. MODERN TYPOGRAPHY ---
        AnimatedVisibility(
            visible = showText,
            enter = fadeIn(tween(800)) + slideInVertically(tween(800)) { 50 }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "マイログ",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.welcome_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        // --- 3. CTA BUTTON ---
        AnimatedVisibility(
            visible = showButton,
            enter = fadeIn(tween(600)) + expandVertically(tween(600))
        ) {
            BouncyButton(
                onClick = onContinueClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                contentPadding = PaddingValues(horizontal = 32.dp)
            ) {
                Text(
                    text = stringResource(R.string.welcome_get_started),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
