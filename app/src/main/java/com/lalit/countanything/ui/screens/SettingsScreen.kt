package com.lalit.countanything.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import android.Manifest
import android.os.Build
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.lalit.countanything.Currency
import com.lalit.countanything.NotificationScheduler
import com.lalit.countanything.R
import com.lalit.countanything.SettingsManager
import com.lalit.countanything.StorageHelper
import com.lalit.countanything.Theme
import com.lalit.countanything.ui.components.AnimatedColumn
import com.lalit.countanything.ui.components.AnimatedItem
import com.lalit.countanything.ui.components.springyTouch
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsManager: SettingsManager
) {
    val scope = rememberCoroutineScope()
    val theme by settingsManager.theme.collectAsState(initial = Theme.SYSTEM)
    val isAppLockEnabled by settingsManager.isAppLockEnabled.collectAsState(initial = false)
    val isPrivacyModeEnabled by settingsManager.isPrivacyModeEnabled.collectAsState(initial = false)
    val isDailyReminderEnabled by settingsManager.isDailyReminderEnabled.collectAsState(initial = false)
    val selectedCurrency by settingsManager.currency.collectAsState(initial = Currency.YEN)

    
    val themes = Theme.values()
    val supportedLanguages = listOf("en" to "English", "ja" to "日本語")
    val context = LocalContext.current

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
        onResult = { uri ->
            uri?.let {
                scope.launch {
                    try {
                        val dataToExport = StorageHelper.exportAllData(context)
                        context.contentResolver.openOutputStream(it)?.use { outputStream ->
                            outputStream.write(dataToExport.toByteArray())
                        }
                        Toast.makeText(context, "Exported successfully!", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    )

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                scope.launch {
                    try {
                        val jsonString = context.contentResolver.openInputStream(it)?.bufferedReader()?.use { reader ->
                            reader.readText()
                        }
                        if (jsonString != null) {
                            StorageHelper.importAllData(context, jsonString)
                            Toast.makeText(context, "Imported successfully!", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Import failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    )

    // Notification Permission Launcher
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                scope.launch {
                    settingsManager.setDailyReminderEnabled(true)
                    NotificationScheduler.scheduleDailyReminder(context)
                }
            } else {
                Toast.makeText(context, "Notifications won't be sent without permission", Toast.LENGTH_SHORT).show()
                scope.launch { settingsManager.setDailyReminderEnabled(false) } // Revert toggle
            }
        }
    )

    fun onDailyReminderToggle(enabled: Boolean) {
        if (enabled) {
            // Check for permission on Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permission = Manifest.permission.POST_NOTIFICATIONS
                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                     scope.launch {
                        settingsManager.setDailyReminderEnabled(true)
                        NotificationScheduler.scheduleDailyReminder(context)
                     }
                } else {
                    notificationPermissionLauncher.launch(permission)
                }
            } else {
                // Pre-Android 13, no runtime permission needed
                scope.launch {
                    settingsManager.setDailyReminderEnabled(true)
                    NotificationScheduler.scheduleDailyReminder(context)
                }
            }
        } else {
            scope.launch {
                settingsManager.setDailyReminderEnabled(false)
                NotificationScheduler.cancelDailyReminder(context)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // --- HEADER SECTION ---


        AnimatedColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp) // Increased spacing for bubbles
        ) {
            Spacer(modifier = Modifier.statusBarsPadding().height(2.dp))

            // --- APPEARANCE GROUP (Deep Purple) ---
            SettingsGroup(
                title = stringResource(R.string.settings_appearance),
                icon = Icons.Default.Palette,
                containerColor = Color(0xFF5E35B1),
                contentColor = Color.White
            ) {
                // Theme Setting
                SettingsRow(
                    label = stringResource(R.string.theme),
                    icon = Icons.Default.AutoAwesome
                ) {
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        themes.forEachIndexed { index, item ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = themes.size),
                                onClick = { scope.launch { settingsManager.setTheme(item) } },
                                selected = theme == item,
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = Color.White,
                                    activeContentColor = Color(0xFF5E35B1),
                                    inactiveContainerColor = Color.White.copy(alpha = 0.2f),
                                    inactiveContentColor = Color.White
                                )
                            ) {
                                Text(item.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                // Currency Setting
                SettingsRow(
                    label = stringResource(R.string.settings_currency),
                    icon = Icons.Default.AccountBalanceWallet
                ) {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth(),
                        space = 4.dp
                    ) {
                        Currency.values().forEachIndexed { index, curr ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = Currency.values().size),
                                onClick = { scope.launch { settingsManager.setCurrency(curr) } },
                                selected = selectedCurrency == curr,
                                label = { Text(text = curr.symbol, fontWeight = FontWeight.Bold) },
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = Color.White,
                                    activeContentColor = Color(0xFF5E35B1),
                                    inactiveContainerColor = Color.White.copy(alpha = 0.2f),
                                    inactiveContentColor = Color.White
                                )
                            )
                        }
                    }
                }
            }



            // --- NOTIFICATIONS GROUP (Orange) ---
            SettingsGroup(
                title = stringResource(R.string.notifications),
                icon = Icons.Default.Notifications,
                containerColor = Color(0xFFF57C00),
                contentColor = Color.White
            ) {
                SettingsToggleRow(
                    label = stringResource(R.string.daily_reminder_title),
                    icon = Icons.Default.NotificationsActive,
                    checked = isDailyReminderEnabled,
                    onCheckedChange = { onDailyReminderToggle(it) }
                )
                 Text(
                    text = stringResource(R.string.daily_reminder_subtitle),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 40.dp, top = 4.dp) // Indent to align with text
                )
            }

            // --- LOCALIZATION GROUP (Vibrant Blue) ---
            SettingsGroup(
                title = stringResource(R.string.settings_localization),
                icon = Icons.Default.Translate,
                containerColor = Color(0xFF1E88E5),
                contentColor = Color.White
            ) {
                SettingsRow(
                    label = stringResource(R.string.language),
                    icon = Icons.Default.Translate
                ) {
                    val currentLocale = AppCompatDelegate.getApplicationLocales().toLanguageTags()
                    val currentLang = if (currentLocale.isEmpty()) "en" else currentLocale

                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        supportedLanguages.forEachIndexed { index, (langCode, langName) ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = supportedLanguages.size),
                                onClick = {
                                    val appLocale = LocaleListCompat.forLanguageTags(langCode)
                                    AppCompatDelegate.setApplicationLocales(appLocale)
                                },
                                selected = currentLang.startsWith(langCode),
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = Color.White,
                                    activeContentColor = Color(0xFF1E88E5),
                                    inactiveContainerColor = Color.White.copy(alpha = 0.2f),
                                    inactiveContentColor = Color.White
                                )
                            ) {
                                Text(langName, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            // --- SECURITY GROUP (Vibrant Pink) ---
            SettingsGroup(
                title = stringResource(R.string.security),
                icon = Icons.Default.Lock,
                containerColor = Color(0xFFD81B60),
                contentColor = Color.White
            ) {
                // App Lock Toggle
                SettingsToggleRow(
                    label = stringResource(R.string.app_lock),
                    icon = Icons.Default.Lock,
                    checked = isAppLockEnabled,
                    onCheckedChange = { scope.launch { settingsManager.setAppLockEnabled(it) } }
                )

                // Privacy Mode Toggle
                SettingsToggleRow(
                    label = stringResource(R.string.settings_privacy_mode),
                    icon = Icons.Default.VisibilityOff,
                    checked = isPrivacyModeEnabled,
                    onCheckedChange = { scope.launch { settingsManager.setPrivacyModeEnabled(it) } }
                )
            }

            // --- DATA MANAGEMENT GROUP (Teal) ---
            SettingsGroup(
                title = stringResource(R.string.data_management),
                icon = Icons.Default.GridView,
                containerColor = Color(0xFF00897B),
                contentColor = Color.White
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DataActionCard(
                        label = stringResource(R.string.export_data),
                        icon = Icons.Default.FileUpload,
                        modifier = Modifier.weight(1f),
                        onClick = { exportLauncher.launch("CountAnything_Backup.json") },
                         // Pass content color to ensure card adapts to the teal background
                        containerColor = Color.White.copy(alpha = 0.2f),
                        contentColor = Color.White
                    )
                    DataActionCard(
                        label = stringResource(R.string.import_data),
                        icon = Icons.Default.FileDownload,
                        modifier = Modifier.weight(1f),
                        onClick = { importLauncher.launch("application/json") },
                        containerColor = Color.White.copy(alpha = 0.2f),
                        contentColor = Color.White
                    )
                }
            }

            // --- ABOUT SECTION (Blue Gray) ---
            SettingsGroup(
                title = stringResource(R.string.settings_about),
                icon = Icons.Default.Info,
                containerColor = Color(0xFF455A64),
                contentColor = Color.White
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.settings_app_version),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                    Text(
                        text = stringResource(R.string.settings_made_with_love),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


@Composable
fun SettingsGroup(
    title: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp), // Bubble shape
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Flat and bold
    ) {
        Column(
            modifier = Modifier.padding(24.dp), // Increased padding
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Expressive Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor.copy(alpha = 0.9f),
                    modifier = Modifier.size(32.dp) // Larger Icon
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall, // Bold Title
                    fontWeight = FontWeight.Black,
                    color = contentColor
                )
            }
            
            // Content
            CompositionLocalProvider(LocalContentColor provides contentColor) {
               content()
            }
        }
    }
}

@Composable
fun SettingsRow(
    label: String,
    icon: ImageVector,
    action: @Composable (() -> Unit)? = null
) {
    val contentColor = LocalContentColor.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = contentColor.copy(alpha = 0.8f) // Use local content color
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium, // Slightly larger text
                fontWeight = FontWeight.SemiBold,
                color = contentColor // Use local content color
            )
        }
        if (action != null) {
            Spacer(modifier = Modifier.height(12.dp))
            action()
        }
    }
}

@Composable
fun SettingsToggleRow(
    label: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val contentColor = LocalContentColor.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = contentColor.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = contentColor, // Match thumb to content (White)
                checkedTrackColor = Color.Black.copy(alpha = 0.3f), // Darker track for contrast
                uncheckedThumbColor = contentColor.copy(alpha = 0.6f),
                uncheckedTrackColor = Color.Black.copy(alpha = 0.1f)
            )
        )
    }
}

@Composable
fun DataActionCard(
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer
) {
    Card(
        modifier = modifier
            .springyTouch()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(32.dp),
                tint = contentColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        }
    }
}
