package com.lalit.countanything.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lalit.countanything.ui.components.StealthMorseText
import com.lalit.countanything.ui.components.springyTouch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiddenVaultScreen(
    onBack: () -> Unit
) {
    // In a real app, this list would come from encrypted storage
    var secrets by remember { mutableStateOf(listOf(
        "Julia Roberts",
        "Scarlett Johansson",
        "Anne Hathaway",
        "Emma Stone"
    )) }
    
    var showAddDialog by remember { mutableStateOf(false) }
    var newSecretText by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, null, tint = Color.Green, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SECURE VAULT", color = Color.Green, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.Green)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color.Green,
                contentColor = Color.Black,
                modifier = Modifier.springyTouch()
            ) {
                Icon(Icons.Default.Add, "Add Secret")
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
                "ENCRYPTED_DATA_STORE // V.2.0",
                color = Color.Green.copy(alpha = 0.5f),
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(secrets) { secret ->
                    StealthMorseText(
                        text = secret,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("New Secure Entry") },
                text = {
                    OutlinedTextField(
                        value = newSecretText,
                        onValueChange = { newSecretText = it },
                        label = { Text("Secret Text") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newSecretText.isNotBlank()) {
                                secrets = secrets + newSecretText
                                newSecretText = ""
                                showAddDialog = false
                            }
                        }
                    ) {
                        Text("ENCRYPT & SAVE")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("CANCEL")
                    }
                }
            )
        }
    }
}
