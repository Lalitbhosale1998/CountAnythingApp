package com.lalit.countanything

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.lalit.countanything.ui.CountAnythingApp
import com.lalit.countanything.ui.screens.WelcomeScreen
import com.lalit.countanything.ui.theme.CountAnyThingTheme
import kotlinx.coroutines.launch

import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settingsManager = SettingsManager(this)
        setContent {
            val theme by settingsManager.theme.collectAsState(initial = Theme.SYSTEM)
            val useDarkTheme = when (theme) {
                Theme.LIGHT -> false
                Theme.DARK -> true
                Theme.SYSTEM -> isSystemInDarkTheme()
            }

            // --- System UI Controller for Status Bar ---
            val systemUiController = rememberSystemUiController()
            SideEffect {
                systemUiController.setStatusBarColor(
                    color = Color.Transparent,
                    darkIcons = !useDarkTheme
                )
            }

            // --- Welcome Screen Logic ---
            val scope = rememberCoroutineScope()
            // We initialize to 'true' so the main app flashes for a moment while DataStore loads.
            // A dedicated loading screen would be an alternative.
            val welcomeShown by settingsManager.welcomeShown.collectAsState(initial = true)


            CountAnyThingTheme(darkTheme = useDarkTheme) {
                // We use AnimatedContent to smoothly switch between Welcome and Main app.
                AnimatedContent(
                    targetState = welcomeShown,
                    label = "WelcomeAppTransition",
                    transitionSpec = {
                        fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
                    }
                ) { hasBeenWelcomed ->
                    if (hasBeenWelcomed) {
                        // User has seen the welcome screen, show the main app
                        CountAnythingApp(settingsManager)
                    } else {
                        // First time user, show the Welcome Screen
                        WelcomeScreen(
                            onContinueClicked = {
                                scope.launch {
                                    settingsManager.setWelcomeShown()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
