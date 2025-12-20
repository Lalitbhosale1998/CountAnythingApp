package com.lalit.countanything.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import com.lalit.countanything.ui.models.Screen

import androidx.compose.ui.res.stringResource
import com.lalit.countanything.R

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BottomNavigationBar(currentScreen: Screen, onScreenSelected: (Screen) -> Unit) {
    NavigationBar {
        // --- COUNTER ITEM ---
        CustomNavigationBarItem(
            label = stringResource(R.string.nav_counter),
            isSelected = currentScreen == Screen.Counter,
            selectedIcon = Icons.Filled.CalendarToday,
            unselectedIcon = Icons.Outlined.CalendarToday,
            onClick = { onScreenSelected(Screen.Counter) }
        )

        // --- HISTORY/INSIGHTS ITEM ---
        CustomNavigationBarItem(
            label = stringResource(R.string.nav_history),
            isSelected = currentScreen == Screen.History,
            selectedIcon = Icons.Filled.Analytics,
            unselectedIcon = Icons.Outlined.Analytics,
            onClick = { onScreenSelected(Screen.History) }
        )
        CustomNavigationBarItem(
            label = stringResource(R.string.nav_goal),
            isSelected = currentScreen == Screen.Goal,
            selectedIcon = Icons.Filled.Flag,
            unselectedIcon = Icons.Outlined.Flag,
            onClick = { onScreenSelected(Screen.Goal) }
        )
        // --- NEW: SETTINGS ITEM ---
        CustomNavigationBarItem(
            label = stringResource(R.string.nav_settings),
            isSelected = currentScreen == Screen.Settings,
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            onClick = { onScreenSelected(Screen.Settings) }
        )
    }
}

@Composable
fun RowScope.CustomNavigationBarItem(
    label: String,
    isSelected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    onClick: () -> Unit
) {
    // Animate the scale of the icon
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "IconScale"
    )

    // Animate the vertical offset of the icon
    val offset by animateFloatAsState(
        targetValue = if (isSelected) -5f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "IconOffset"
    )

    NavigationBarItem(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label) },
        icon = {
            AnimatedContent(
                targetState = isSelected,
                label = "IconAnimation",
                transitionSpec = {
                    scaleIn(animationSpec = spring(stiffness = Spring.StiffnessLow)) togetherWith
                            scaleOut(animationSpec = spring(stiffness = Spring.StiffnessHigh))
                }
            ) { isCurrentlySelected ->
                Icon(
                    imageVector = if (isCurrentlySelected) selectedIcon else unselectedIcon,
                    contentDescription = label,
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationY = offset
                        }
                )
            }
        }
    )
}
