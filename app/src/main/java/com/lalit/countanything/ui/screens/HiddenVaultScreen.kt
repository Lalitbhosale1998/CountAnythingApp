package com.lalit.countanything.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lalit.countanything.R
import com.lalit.countanything.StorageHelper
import com.lalit.countanything.ui.components.StealthMorseText
import com.lalit.countanything.ui.components.springyTouch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiddenVaultScreen(
    onBack: () -> Unit,
    vaultEntries: List<StorageHelper.VaultEntry>,
    onAddEntry: (String) -> Unit,
    onUpdateEntry: (String, String) -> Unit,
    onDeleteEntry: (String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingEntry by remember { mutableStateOf<StorageHelper.VaultEntry?>(null) }
    var secretText by remember { mutableStateOf("") }

    // Helper to open dialog
    fun openDialog(entry: StorageHelper.VaultEntry? = null) {
        editingEntry = entry
        secretText = entry?.secretText ?: ""
        showAddDialog = true
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, null, tint = Color.Green, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.vault_title), color = Color.Green, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, stringResource(R.string.cancel), tint = Color.Green)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openDialog() },
                containerColor = Color.Green,
                contentColor = Color.Black,
                modifier = Modifier.springyTouch()
            ) {
                Icon(Icons.Default.Add, stringResource(R.string.vault_add_secret))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                stringResource(R.string.vault_subtitle),
                color = Color.Green.copy(alpha = 0.5f),
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            if (vaultEntries.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No secrets yet. Tap + to add.",
                        color = Color.Green.copy(alpha = 0.3f),
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = vaultEntries,
                        key = { it.id }
                    ) { entry ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                if (it == SwipeToDismissBoxValue.EndToStart) {
                                    onDeleteEntry(entry.id)
                                    true
                                } else {
                                    false
                                }
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                val color by animateColorAsState(
                                    when (dismissState.targetValue) {
                                        SwipeToDismissBoxValue.EndToStart -> Color.Red.copy(alpha = 0.8f)
                                        else -> Color.Transparent
                                    }, label = "SwipeColor"
                                )
                                val scale by animateFloatAsState(
                                    if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1f else 0.75f,
                                    label = "SwipeScale"
                                )
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(color, MaterialTheme.shapes.medium)
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        modifier = Modifier.scale(scale),
                                        tint = Color.White
                                    )
                                }
                            },
                            content = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { openDialog(entry) }
                                ) {
                                    StealthMorseText(
                                        text = entry.secretText,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
        
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text(if (editingEntry == null) stringResource(R.string.vault_new_entry) else "Edit Secret") },
                text = {
                    OutlinedTextField(
                        value = secretText,
                        onValueChange = { secretText = it },
                        label = { Text(stringResource(R.string.vault_secret_text)) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Green,
                            focusedLabelColor = Color.Green,
                            focusedTextColor = Color.Green,
                            cursorColor = Color.Green
                        )
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (secretText.isNotBlank()) {
                                if (editingEntry != null) {
                                    onUpdateEntry(editingEntry!!.id, secretText)
                                } else {
                                    onAddEntry(secretText)
                                }
                                showAddDialog = false
                            }
                        }
                    ) {
                        Text(stringResource(R.string.vault_encrypt_save), color = Color.Green)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text(stringResource(R.string.cancel), color = Color.Green)
                    }
                },
                containerColor = Color(0xFF121212),
                titleContentColor = Color.Green,
                textContentColor = Color.Green
            )
        }
    }
}
