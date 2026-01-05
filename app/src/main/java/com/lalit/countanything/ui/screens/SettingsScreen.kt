package com.lalit.countanything.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import com.lalit.countanything.Currency
import com.lalit.countanything.NotificationScheduler
import com.lalit.countanything.R
import com.lalit.countanything.SettingsManager
import com.lalit.countanything.StorageHelper
import com.lalit.countanything.Theme
import com.lalit.countanything.ui.components.springyTouch
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsManager: SettingsManager
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    // --- State Observation ---
    val theme by settingsManager.theme.collectAsState(initial = Theme.SYSTEM)
    val isAppLockEnabled by settingsManager.isAppLockEnabled.collectAsState(initial = false)
    val isPrivacyModeEnabled by settingsManager.isPrivacyModeEnabled.collectAsState(initial = false)
    val isDailyReminderEnabled by settingsManager.isDailyReminderEnabled.collectAsState(initial = false)
    val selectedCurrency by settingsManager.currency.collectAsState(initial = Currency.YEN)
    val userName by settingsManager.userName.collectAsState(initial = "User")

    // --- Launchers (Preserved Logic) ---
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
                scope.launch { settingsManager.setDailyReminderEnabled(false) }
            }
        }
    )

    fun onDailyReminderToggle(enabled: Boolean) {
        if (enabled) {
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

    // --- Content ---
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { 
                    Text(
                        "Settings",
                        fontWeight = FontWeight.SemiBold
                    ) 
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            
            // --- PROFILE HEADER ---
            item {
                UserProfileHeader(name = userName) { newName ->
                    scope.launch { settingsManager.setUserName(newName) }
                }
            }

            // --- APPEARANCE ---
            item {
                SettingsSectionTitle("Appearance")
                
                // Theme
                M3SettingsItem(
                    title = "App Theme",
                    subtitle = theme.name.lowercase().replaceFirstChar { it.uppercase() },
                    icon = Icons.Outlined.Palette,
                    iconColor = Color(0xFF6750A4), // M3 Purple
                    onClick = { 
                         // Cycle through themes for simplicity or show dialog. 
                         // Implementing simple toggle for now or logic to switch next.
                         val nextTheme = when(theme) {
                             Theme.LIGHT -> Theme.DARK
                             Theme.DARK -> Theme.SYSTEM
                             Theme.SYSTEM -> Theme.LIGHT
                         }
                         scope.launch { settingsManager.setTheme(nextTheme) }
                    }
                )

                // Currency
                M3SettingsItem(
                    title = "Currency",
                    subtitle = "Active: ${selectedCurrency.symbol} ($selectedCurrency)",
                    icon = Icons.Outlined.Paid,
                    iconColor = Color(0xFF2E7D32), // Green
                    onClick = {
                        val nextCurrency = Currency.values()[(selectedCurrency.ordinal + 1) % Currency.values().size]
                        scope.launch { settingsManager.setCurrency(nextCurrency) }
                    }
                )
                
                 // Language
                M3SettingsItem(
                    title = "Language",
                    subtitle = "English / 日本語",
                    icon = Icons.Outlined.Language,
                    iconColor = Color(0xFF00695C), // Teal
                    onClick = {
                       // Toggle simple logic for demo
                       val currentLocale = AppCompatDelegate.getApplicationLocales().toLanguageTags()
                       val newLang = if (currentLocale.contains("ja")) "en" else "ja"
                       val appLocale = LocaleListCompat.forLanguageTags(newLang)
                       AppCompatDelegate.setApplicationLocales(appLocale)
                    }
                )
            }

            // --- SECURITY ---
            item {
                SettingsSectionTitle("Security")
                
                M3SettingsSwitch(
                    title = "App Lock",
                    subtitle = "Require biometrics to open",
                    icon = Icons.Outlined.Lock,
                    iconColor = Color(0xFFB71C1C), // Red
                    checked = isAppLockEnabled,
                    onCheckedChange = { scope.launch { settingsManager.setAppLockEnabled(it) } }
                )

                M3SettingsSwitch(
                    title = "Privacy Mode",
                    subtitle = "Hide sensitive values",
                    icon = Icons.Outlined.VisibilityOff,
                    iconColor = Color(0xFFC2185B), // Pink
                    checked = isPrivacyModeEnabled,
                    onCheckedChange = { scope.launch { settingsManager.setPrivacyModeEnabled(it) } }
                )
            }

            // --- NOTIFICATIONS ---
            item {
                SettingsSectionTitle("Notifications")
                
                M3SettingsSwitch(
                    title = "Daily Reminder",
                    subtitle = "Get notified at 8:00 AM",
                    icon = Icons.Outlined.Notifications,
                    iconColor = Color(0xFFE65100), // Orange
                    checked = isDailyReminderEnabled,
                    onCheckedChange = { onDailyReminderToggle(it) }
                )
            }

            // --- DATA ---
            item {
                SettingsSectionTitle("Data Management")
                
                M3SettingsItem(
                    title = "Backup Data",
                    subtitle = "Export to JSON file",
                    icon = Icons.Outlined.Upload,
                    iconColor = Color(0xFF1565C0), // Blue
                    onClick = { exportLauncher.launch("CountAnything_Backup.json") }
                )
                
                M3SettingsItem(
                    title = "Restore Data",
                    subtitle = "Import from JSON file",
                    icon = Icons.Outlined.Download,
                    iconColor = Color(0xFF0277BD), // Light Blue
                    onClick = { importLauncher.launch("application/json") }
                )
            }

            // --- FOOTER ---
             item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Version 1.0.0",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                     Text(
                        text = "Made with ❤️ by Antigravity",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

// --- COMMPOSABLES ---

@Composable
fun UserProfileHeader(
    name: String,
    onNameChange: (String) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showEditDialog = true }
            .padding(vertical = 8.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = name.take(1).uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = if(name.isNotBlank()) name else "User",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Tap to edit profile",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if(showEditDialog) {
        var tempName by remember { mutableStateOf(name) }
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Name") },
            text = { 
                OutlinedTextField(
                    value = tempName, 
                    onValueChange = { tempName = it },
                    label = { Text("Your Name") }
                ) 
            },
            confirmButton = {
                Button(onClick = { onNameChange(tempName); showEditDialog = false }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
    )
}

@Composable
fun M3SettingsItem(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Container
        Surface(
            shape = CircleShape,
            color = iconColor.copy(alpha = 0.1f),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Text
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Action/Arrow
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun M3SettingsSwitch(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    iconColor: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable { onCheckedChange(!checked) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = iconColor.copy(alpha = 0.1f),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
