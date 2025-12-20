package com.lalit.countanything.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.lalit.countanything.SettingsManager
import com.lalit.countanything.StorageHelper
import com.lalit.countanything.Theme
import kotlinx.coroutines.launch
import androidx.compose.material3.Switch
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.GridView
import com.lalit.countanything.R
import androidx.compose.ui.res.stringResource
import com.lalit.countanything.ui.components.AnimatedColumn
import com.lalit.countanything.ui.components.AnimatedItem
import com.lalit.countanything.ui.components.springyTouch
import com.lalit.countanything.Currency
import androidx.compose.material.icons.filled.BarChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsManager: SettingsManager
) {
    val scope = rememberCoroutineScope()
    val theme by settingsManager.theme.collectAsState(initial = Theme.SYSTEM)
    val isAppLockEnabled by settingsManager.isAppLockEnabled.collectAsState(initial = false)
    val isPrivacyModeEnabled by settingsManager.isPrivacyModeEnabled.collectAsState(initial = false)
    val selectedCurrency by settingsManager.currency.collectAsState(initial = Currency.YEN)
    
    val themes = Theme.values()
    val supportedLanguages = listOf("en" to "English", "ja" to "日本語")
    val context = LocalContext.current // Get the context for Toasts
    // --- INSERT THIS CODE SNIPPET ---
    // Launcher for EXPORT: Asks the user where to save a new file.
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

    // Launcher for IMPORT: Asks the user to pick an existing file.
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
    // --- END OF SNIPPET ---

    AnimatedColumn(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        AnimatedItem(index = 0) {
            // --- THEME SETTINGS CARD ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .springyTouch(),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.theme),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        themes.forEachIndexed { index, item ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = themes.size),
                                onClick = {
                                    scope.launch { settingsManager.setTheme(item) }
                                },
                                selected = theme == item
                            ) {
                                Text(item.name.lowercase().replaceFirstChar { it.uppercase() })
                            }
                        }
                    }
                }
            }
        }
        AnimatedItem(index = 1) {
            // --- NEW: LANGUAGE SETTINGS CARD ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .springyTouch(),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.language),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val currentLocale = AppCompatDelegate.getApplicationLocales().toLanguageTags()
                    val currentLang = if (currentLocale.isEmpty()) "en" else currentLocale

                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        supportedLanguages.forEachIndexed { index, (langCode, langName) ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = supportedLanguages.size),
                                onClick = {
                                    // Set the new app locale
                                    val appLocale = LocaleListCompat.forLanguageTags(langCode)
                                    AppCompatDelegate.setApplicationLocales(appLocale)
                                },
                                selected = currentLang.startsWith(langCode)
                            ) {
                                Text(langName)
                            }
                        }
                    }
                }
            }
        }
        AnimatedItem(index = 2) {
            // --- NEW: SECURITY SETTINGS CARD ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .springyTouch(),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.security),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = stringResource(R.string.app_lock))
                        }
                        Switch(
                            checked = isAppLockEnabled,
                            onCheckedChange = { enabled ->
                                scope.launch { settingsManager.setAppLockEnabled(enabled) }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VisibilityOff, contentDescription = null)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = "Privacy Mode (Finance)")
                        }
                        Switch(
                            checked = isPrivacyModeEnabled,
                            onCheckedChange = { enabled ->
                                scope.launch { settingsManager.setPrivacyModeEnabled(enabled) }
                            }
                        )
                    }
                }
            }
        }


        AnimatedItem(index = 3) {
            // --- NEW: CURRENCY SETTINGS CARD ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .springyTouch(),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Currency",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth(),
                        space = 8.dp
                    ) {
                        Currency.values().forEachIndexed { index, curr ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = Currency.values().size),
                                onClick = {
                                    scope.launch { settingsManager.setCurrency(curr) }
                                },
                                selected = selectedCurrency == curr,
                                label = {
                                    Text(
                                        text = curr.symbol,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        // --- NEW: STYLED DATA MANAGEMENT SECTION ---
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // --- Define the colors from your design ---
            val peachColor = Color(0xFFFDE4D6) // A light peachy color for the card backgrounds
            val brownColor = Color(0xFF6F4E37) // A coffee-brown color for text and icons

            // Header label matching Counter tab style
            Text(
                text = stringResource(R.string.data_management),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Two cards for Export and Import
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // A common set of colors for both cards
                val cardColors = CardDefaults.cardColors(
                    containerColor = peachColor,
                    contentColor = brownColor
                )

                // EXPORT CARD
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .springyTouch()
                        .clickable { exportLauncher.launch("MyLog_Backup.json") },
                    shape = RoundedCornerShape(24.dp),

                    ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth() // Ensure column takes full width of the card
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = "Export Data",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(stringResource(R.string.export_data), fontWeight = FontWeight.SemiBold)
                    }
                }

                // IMPORT CARD
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .springyTouch()
                        .clickable { importLauncher.launch("application/json") },
                    shape = RoundedCornerShape(24.dp),

                    ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth() // Ensure column takes full width of the card
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = "Import Data",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(stringResource(R.string.import_data), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // --- BOTTOM SPACER FOR BREATHING ROOM ---
        Spacer(modifier = Modifier.height(80.dp))
    }
}
