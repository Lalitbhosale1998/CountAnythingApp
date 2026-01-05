package com.lalit.countanything.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
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
    
    // Cyberpunk Theme Colors
    val NeonBlueGrey = Color(0xFF607D8B) 
    val NeonIce = Color(0xFF80DEEA)
    val BgDark = Color(0xFF0F171A) // Slightly bluish dark
    val PanelBg = Color(0xFF162025)

    // Helper to open dialog
    fun openDialog(entry: StorageHelper.VaultEntry? = null) {
        editingEntry = entry
        secretText = entry?.secretText ?: ""
        showAddDialog = true
    }

    Scaffold(
        containerColor = BgDark,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                         Icon(Icons.Default.Lock, null, tint = NeonIce, modifier = Modifier.size(16.dp))
                         Spacer(modifier = Modifier.width(8.dp))
                         Text(
                            "SECURE_CORE // VAULT", 
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            letterSpacing = (-0.5).sp,
                            color = NeonIce
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = NeonIce)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BgDark,
                    titleContentColor = NeonIce
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openDialog() },
                containerColor = NeonIce,
                contentColor = Color.Black,
                modifier = Modifier.springyTouch(),
                shape = CutCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, "ENC_NEW")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header Status
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    "ENCRYPTION: AES-256",
                    color = NeonBlueGrey,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )
                Text(
                    "ENTRIES: ${vaultEntries.size}",
                    color = NeonBlueGrey,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(NeonBlueGrey.copy(alpha = 0.3f)).padding(vertical = 8.dp))
            Spacer(modifier = Modifier.height(16.dp))
            
            if (vaultEntries.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Lock, null, tint = NeonBlueGrey.copy(alpha = 0.2f), modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "[ STORAGE_EMPTY ]",
                            color = NeonBlueGrey.copy(alpha = 0.5f),
                            fontFamily = FontFamily.Monospace
                        )
                    }
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
                                        SwipeToDismissBoxValue.EndToStart -> Color(0xFFD32F2F).copy(alpha = 0.8f)
                                        else -> Color.Transparent
                                    }, label = "SwipeColor"
                                )
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(color, CutCornerShape(topStart = 0.dp, bottomEnd = 16.dp))
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.White
                                    )
                                }
                            },
                            content = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(PanelBg, CutCornerShape(topStart = 0.dp, bottomEnd = 16.dp))
                                        .border(1.dp, NeonBlueGrey.copy(alpha = 0.3f), CutCornerShape(topStart = 0.dp, bottomEnd = 16.dp))
                                        .clickable { openDialog(entry) }
                                        .padding(16.dp)
                                ) {
                                    Column {
                                        Text(
                                            "ID: ${entry.id.take(8).uppercase()}",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 8.sp,
                                            color = NeonBlueGrey
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        StealthMorseText(
                                            text = entry.secretText,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
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
                title = { 
                    Text(
                        if (editingEntry == null) "NEW_SECURE_ENTRY" else "MODIFY_ENTRY", 
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = NeonIce
                    ) 
                },
                text = {
                    Column {
                        Text(
                            "> ENTER_PAYLOAD_DATA:", 
                            fontFamily = FontFamily.Monospace, 
                            fontSize = 10.sp, 
                            color = NeonBlueGrey
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = secretText,
                            onValueChange = { secretText = it },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonIce,
                                unfocusedBorderColor = NeonBlueGrey,
                                focusedTextColor = NeonIce,
                                cursorColor = NeonIce,
                                focusedContainerColor = Color(0xFF0F171A),
                                unfocusedContainerColor = Color(0xFF0F171A)
                            ),
                            textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace)
                        )
                    }
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
                        Text("[ ENCRYPT_SAVE ]", fontFamily = FontFamily.Monospace, color = NeonIce, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("[ ABORT ]", fontFamily = FontFamily.Monospace, color = Color.Gray)
                    }
                },
                containerColor = Color(0xFF1E272C),
                shape = CutCornerShape(16.dp)
            )
        }
    }
}
