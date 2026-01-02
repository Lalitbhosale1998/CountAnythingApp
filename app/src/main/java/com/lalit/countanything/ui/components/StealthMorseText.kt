package com.lalit.countanything.ui.components

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.lalit.countanything.utils.BiometricHelper
import com.lalit.countanything.utils.MorseCodeHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StealthMorseText(
    text: String,
    modifier: Modifier = Modifier,
    isBiometricEnabled: Boolean = true
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isRevealed by remember { mutableStateOf(false) }

    // Convert text to Morse Code for the "Masked" state
    val morseMask = remember(text) { 
        // Simple conversion: "A" -> ".-", "B" -> "-..." etc.
        // We'll use a mocked conversion if Helper isn't perfect for display, 
        // but let's assume MorseCodeHelper.toMorse returns a list of enums.
        // For this component we want a String representation.
        text.uppercase().map { char ->
            when(char) {
                'A' -> ".-"
                'B' -> "-..."
                'C' -> "-.-."
                'D' -> "-.."
                'E' -> "."
                'F' -> "..-."
                'G' -> "--."
                'H' -> "...."
                'I' -> ".."
                'J' -> ".---"
                'K' -> "-.-"
                'L' -> ".-.."
                'M' -> "--"
                'N' -> "-."
                'O' -> "---"
                'P' -> ".--."
                'Q' -> "--.-"
                'R' -> ".-."
                'S' -> "..."
                'T' -> "-"
                'U' -> "..-"
                'V' -> "...-"
                'W' -> ".--"
                'X' -> "-..-"
                'Y' -> "-.--"
                'Z' -> "--.."
                ' ' -> "/"
                else -> "?"
            }
        }.joinToString(" ")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isRevealed) MaterialTheme.colorScheme.primaryContainer else Color.Black)
            .clickable {
                if (isRevealed) {
                    // Tap to hide immediately
                    isRevealed = false
                } else {
                    // Tap to Reveal
                    if (isBiometricEnabled && context is FragmentActivity) {
                        BiometricHelper.authenticate(
                            activity = context,
                            onSuccess = {
                                isRevealed = true
                                // Auto-hide after 5 seconds
                                scope.launch {
                                    delay(5000)
                                    isRevealed = false
                                }
                            },
                            onError = { error ->
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        // If biometrics not available/enabled (e.g. preview), just reveal
                        isRevealed = true
                         scope.launch {
                            delay(5000)
                            isRevealed = false
                        }
                    }
                }
            }
            .padding(12.dp)
    ) {
        Crossfade(targetState = isRevealed, animationSpec = tween(500)) { revealed ->
            if (revealed) {
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            } else {
                Text(
                    text = morseMask,
                    color = Color.Green, // Matrix/Hacker vibe
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}
