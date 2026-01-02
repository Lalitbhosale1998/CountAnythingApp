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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.lalit.countanything.ui.viewmodels.MainViewModel

import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import com.lalit.countanything.ui.components.LockScreen
import androidx.compose.runtime.collectAsState

class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false)
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
                // Collect Settings
                val isAppLockEnabled by settingsManager.isAppLockEnabled.collectAsState(initial = null)
                
                var isLocked by remember { mutableStateOf(true) } // Default to locked
                
                // Effect to handle initial lock state based on setting
                LaunchedEffect(isAppLockEnabled) {
                    // Only unlock if we are SURE it's disabled.
                    // If it's null (loading) or true (enabled), we stay locked.
                    if (isAppLockEnabled == false) {
                        isLocked = false
                    } 
                }

                // Authentication Function
                fun authenticate() {
                    val executor = ContextCompat.getMainExecutor(this)
                    val biometricPrompt = BiometricPrompt(this, executor,
                        object : BiometricPrompt.AuthenticationCallback() {
                            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                super.onAuthenticationSucceeded(result)
                                isLocked = false
                            }
                            // Handle error/fail? For now, just stay locked.
                        })

                    val promptInfo = BiometricPrompt.PromptInfo.Builder()
                        .setTitle(getString(R.string.unlock_title))
                        .setSubtitle(getString(R.string.unlock_subtitle))
                        .setNegativeButtonText(getString(R.string.cancel))
                        .setAllowedAuthenticators(BIOMETRIC_STRONG or BIOMETRIC_WEAK)
                        .build()

                    biometricPrompt.authenticate(promptInfo)
                }

                // Trigger auth ONCE when lock is enabled and we are locked
                LaunchedEffect(isAppLockEnabled, isLocked) {
                    if (isAppLockEnabled == true && isLocked) {
                        authenticate()
                    }
                }

                // Main Content Switching
                if (isAppLockEnabled == true && isLocked) {
                    LockScreen(onUnlockClick = { authenticate() })
                } else {
                    // Safe Content
                    AnimatedContent(
                        targetState = welcomeShown,
                        label = "WelcomeAppTransition",
                        transitionSpec = {
                            fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
                        }
                    ) { hasBeenWelcomed ->
                        if (hasBeenWelcomed) {
                            CountAnythingApp(
                                viewModel = mainViewModel,
                                settingsManager = settingsManager
                            )
                        } else {
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
}
