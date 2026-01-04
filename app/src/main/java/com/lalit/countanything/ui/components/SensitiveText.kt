package com.lalit.countanything.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

import androidx.compose.ui.text.style.TextOverflow

@Composable
fun SensitiveText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    color: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Unspecified,
    privacyModeEnabled: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    placeholder: String = "---"
) {
    var revealedManually by remember(privacyModeEnabled) { mutableStateOf(false) }
    val isHidden = privacyModeEnabled && !revealedManually

    Box(
        modifier = modifier.clickable {
            if (privacyModeEnabled) {
                revealedManually = !revealedManually
            }
        }
    ) {
        Text(
            text = if (isHidden) placeholder else text,
            style = style,
            color = color,
            maxLines = maxLines,
            overflow = overflow
        )
    }
}
