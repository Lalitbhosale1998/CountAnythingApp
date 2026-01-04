package com.lalit.countanything.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import com.lalit.countanything.ui.models.Kanji
import com.lalit.countanything.ui.models.Vocab
import com.lalit.countanything.ui.models.Grammar
import com.lalit.countanything.ui.viewmodels.StudyViewModel

@Composable
fun StudyScreen(
    viewModel: StudyViewModel = viewModel()
) {
    var selectedLevel by remember { mutableStateOf<String?>(null) }
    var showCounterGuide by remember { mutableStateOf(false) }

    if (showCounterGuide) {
        CounterStudyScreen(onBack = { showCounterGuide = false })
    } else if (selectedLevel == null) {
        StudyDashboard(
            viewModel = viewModel,
            onLevelSelected = { selectedLevel = it },
            onOpenCounters = { showCounterGuide = true }
        )
    } else {
        StudyLevelDetailScreen(
            level = selectedLevel!!,
            viewModel = viewModel,
            onBack = { selectedLevel = null }
        )
    }
}

@Composable
fun StudyDashboard(
    viewModel: StudyViewModel,
    onLevelSelected: (String) -> Unit,
    onOpenCounters: () -> Unit
) {
    val kanjiList by viewModel.kanjiList.collectAsState()
    val vocabList by viewModel.vocabList.collectAsState()
    val grammarList by viewModel.grammarList.collectAsState()

    val learnedKanji by viewModel.learnedKanji.collectAsState()
    val learnedVocab by viewModel.learnedVocab.collectAsState()
    val learnedGrammar by viewModel.learnedGrammar.collectAsState()

    val totalItems = kanjiList.size + vocabList.size + grammarList.size
    val totalLearned = learnedKanji.size + learnedVocab.size + learnedGrammar.size
    val progress = if (totalItems > 0) totalLearned.toFloat() / totalItems else 0f

    // Calculate top padding to include status bars
    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 24.dp

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentPadding = PaddingValues(
            top = topPadding,
            bottom = 24.dp,
            start = 24.dp,
            end = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // --- Header Section ---
        item {
            DashboardHeader(totalLearned = totalLearned, totalItems = totalItems)
        }

        // --- Quick Stats ---
        item {
            QuickStatsRow(
                kanjiCount = kanjiList.size,
                kanjiLearned = learnedKanji.size,
                vocabCount = vocabList.size,
                vocabLearned = learnedVocab.size,
                grammarCount = grammarList.size,
                grammarLearned = learnedGrammar.size
            )
        }



        // --- Course Cards ---
        item {
            PremiumLevelCard(
                title = "JLPT N2",
                subtitle = "Intermediate",
                description = "Comprehensive Vocab, Kanji & Grammar",
                progress = progress, 
                color = Color(0xFF4CAF50), // Green for N2
                onClick = { onLevelSelected("N2") }
            )
        }

        item {
            PremiumLevelCard(
                title = "Josuushi Guide",
                subtitle = "Utility",
                description = "The Ultimate Dictionary of Japanese Counters",
                progress = 1.0f, // Always available
                color = Color(0xFF00BCD4), // Cyan
                onClick = onOpenCounters
            )
        }
        
        item {
             PremiumLevelCard(
                title = "JLPT N1",
                subtitle = "Advanced",
                description = "Mastery Level Content",
                progress = 0.0f, 
                color = Color(0xFFE91E63), // Pink for N1
                onClick = { /* Todo */ }
            )
        }
    }
}

@Composable
fun DashboardHeader(totalLearned: Int, totalItems: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Welcome back!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "You have $totalItems items to review",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Circular Daily Goal Indicator
        Box(contentAlignment = Alignment.Center) {
            val percentage = if (totalItems > 0) (totalLearned.toFloat() / totalItems) else 0f
            CircularProgressIndicator(
                progress = { percentage }, 
                modifier = Modifier.size(56.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primaryContainer,
                strokeWidth = 6.dp,
            )
            Text(
                text = "${(percentage * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun QuickStatsRow(
    kanjiCount: Int, kanjiLearned: Int,
    vocabCount: Int, vocabLearned: Int,
    grammarCount: Int, grammarLearned: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            title = "Kanji",
            count = kanjiCount,
            learned = kanjiLearned,
            color = Color(0xFFE91E63),
            icon = "字",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Vocab",
            count = vocabCount,
            learned = vocabLearned,
            color = Color(0xFFFF9800),
            icon = "語",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Grammar",
            count = grammarCount,
            learned = grammarLearned,
            color = Color(0xFF2196F3),
            icon = "文",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    title: String,
    count: Int,
    learned: Int,
    color: Color,
    icon: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(110.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(20.dp)
        // elevation = CardDefaults.cardElevation(2.dp) // Optional elevation
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Watermark Icon
            Text(
                text = icon,
                fontSize = 80.sp,
                fontWeight = FontWeight.Black,
                color = color.copy(alpha = 0.05f),
                modifier = Modifier.align(Alignment.BottomEnd).offset(x = 10.dp, y = 10.dp)
            )
            
            Column(
                modifier = Modifier.padding(16.dp).fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                 Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(color.copy(alpha = 0.2f), androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(icon, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
                }
                
                Column {
                   // Dynamic font sizing or just use a readable size
                    Text("$learned/$count", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun PremiumLevelCard(
    title: String,
    subtitle: String,
    description: String,
    progress: Float,
    color: Color,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Gradient Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = listOf(color, color.copy(alpha = 0.7f))
                        )
                    )
            ) {
                // Decorative circles or pattern could go here
                Text(
                    text = title.take(2), // "JL" or "N2"
                    fontSize = 100.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White.copy(alpha = 0.1f),
                    modifier = Modifier.align(Alignment.BottomEnd).offset(x = 20.dp, y = 20.dp)
                )
                
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = subtitle.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
            
            // Content & Progress
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = color,
                        trackColor = color.copy(alpha = 0.2f),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyLevelDetailScreen(
    level: String,
    viewModel: StudyViewModel,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }
    
    val kanjiList by viewModel.kanjiList.collectAsState()
    val vocabList by viewModel.vocabList.collectAsState()
    val grammarList by viewModel.grammarList.collectAsState()
    
    val learnedKanji by viewModel.learnedKanji.collectAsState()
    val learnedVocab by viewModel.learnedVocab.collectAsState()
    val learnedGrammar by viewModel.learnedGrammar.collectAsState()

    val tabs = listOf(
        "Kanji" to kanjiList.size,
        "Vocab" to vocabList.size,
        "Grammar" to grammarList.size
    )

    if (showAddDialog) {
        AddStudyItemDialog(
            type = selectedTab,
            onDismiss = { showAddDialog = false },
            onAddKanji = { c, m, o, k -> viewModel.addKanji(c, m, o, k) },
            onAddVocab = { w, r, m -> viewModel.addVocab(w, r, m) },
            onAddGrammar = { p, m, e -> viewModel.addGrammar(p, m, e) }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = androidx.compose.foundation.shape.CircleShape
            ) {
                Icon(androidx.compose.material.icons.Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()) // Only respect bottom padding here
        ) {
            // --- Premium Header ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = innerPadding.calculateTopPadding() + 8.dp) // Add status bar padding
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    // Back Button & Title Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(48.dp)
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), androidx.compose.foundation.shape.CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column {
                            Text(
                                text = "$level Study",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                             Text(
                                text = "Master your Japanese skills",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- Clean Tabs ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        tabs.forEachIndexed { index, (title, count) ->
                            val isSelected = selectedTab == index
                            val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(64.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(containerColor)
                                    .clickable { selectedTab = index }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = contentColor
                                    )
                                    Text(
                                        text = count.toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = contentColor.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Content ---
            Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                when (selectedTab) {
                    0 -> KanjiList(kanjiList, learnedKanji) { char, learned -> viewModel.markKanjiLearned(char, learned) }
                    1 -> VocabList(vocabList, learnedVocab) { word, learned -> viewModel.markVocabLearned(word, learned) }
                    2 -> GrammarList(grammarList, learnedGrammar) { pattern, learned -> viewModel.markGrammarLearned(pattern, learned) }
                }
            }
        }
    }
    
    // FAB is handled by Scaffold in a real app, but here we can just overlay it or use Scaffold's fab slot if we move things around.
    // Since we are already inside a Scaffold, let's use the Box to overlay the FAB at the bottom right.
    // Wait, the Scaffold is wrapping the Column. Let me use the Scaffold's floatingActionButton slot correctly.
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyLevelDetailScreenWithFab(
    level: String,
    viewModel: StudyViewModel,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }
    
    val kanjiList by viewModel.kanjiList.collectAsState()
    val vocabList by viewModel.vocabList.collectAsState()
    val grammarList by viewModel.grammarList.collectAsState()
    
    val learnedKanji by viewModel.learnedKanji.collectAsState()
    val learnedVocab by viewModel.learnedVocab.collectAsState()
    val learnedGrammar by viewModel.learnedGrammar.collectAsState()

    val tabs = listOf(
        "Kanji" to kanjiList.size,
        "Vocab" to vocabList.size,
        "Grammar" to grammarList.size
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = androidx.compose.foundation.shape.CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { innerPadding ->
         Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
             // ... Header ... (Duplicated logic for brevity of replacement, I should have just wrapped the exiting screen or modified it in place. 
             // I will modify the EXISTING content of the function StudyLevelDetailScreen instead of creating a new one to avoid duplication/errors.)
        }
    }
}
// Rethinking strategy: modifying the existing function in place.

@Composable
fun AddStudyItemDialog(
    type: Int, // 0: Kanji, 1: Vocab, 2: Grammar
    onDismiss: () -> Unit,
    onAddKanji: (String, String, String, String) -> Unit,
    onAddVocab: (String, String, String) -> Unit,
    onAddGrammar: (String, String, String) -> Unit
) {
    var field1 by remember { mutableStateOf("") }
    var field2 by remember { mutableStateOf("") }
    var field3 by remember { mutableStateOf("") }
    var field4 by remember { mutableStateOf("") } // Only for Kanji (Kunyomi)

    val title = when (type) {
        0 -> "Add Kanji"
        1 -> "Add Vocabulary"
        else -> "Add Grammar"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                when (type) {
                    0 -> { // Kanji
                        OutlinedTextField(value = field1, onValueChange = { field1 = it }, label = { Text("Character") }, singleLine = true)
                        OutlinedTextField(value = field2, onValueChange = { field2 = it }, label = { Text("Meaning") }, singleLine = true)
                        OutlinedTextField(value = field3, onValueChange = { field3 = it }, label = { Text("Onyomi (comma separated)") }, singleLine = true)
                        OutlinedTextField(value = field4, onValueChange = { field4 = it }, label = { Text("Kunyomi (comma separated)") }, singleLine = true)
                    }
                    1 -> { // Vocab
                        OutlinedTextField(value = field1, onValueChange = { field1 = it }, label = { Text("Word") }, singleLine = true)
                        OutlinedTextField(value = field2, onValueChange = { field2 = it }, label = { Text("Reading") }, singleLine = true)
                        OutlinedTextField(value = field3, onValueChange = { field3 = it }, label = { Text("Meaning") }, singleLine = true)
                    }
                    2 -> { // Grammar
                        OutlinedTextField(value = field1, onValueChange = { field1 = it }, label = { Text("Pattern") }, singleLine = true)
                        OutlinedTextField(value = field2, onValueChange = { field2 = it }, label = { Text("Meaning") }, singleLine = true)
                        OutlinedTextField(value = field3, onValueChange = { field3 = it }, label = { Text("Example") }, maxLines = 3)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when (type) {
                        0 -> onAddKanji(field1, field2, field3, field4)
                        1 -> onAddVocab(field1, field2, field3)
                        2 -> onAddGrammar(field1, field2, field3)
                    }
                    onDismiss()
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun <T> FlashcardGame(
    list: List<T>,
    onFront: (T) -> String,
    onBack: (T) -> String,
    isLearned: (T) -> Boolean,
    onToggleLearned: (T, Boolean) -> Unit,
    color: Color
) {
    if (list.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No items found", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    var currentIndex by remember { mutableStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    // Reset flip when index changes
    LaunchedEffect(currentIndex) {
        isFlipped = false
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress Bar
        LinearProgressIndicator(
            progress = (currentIndex + 1) / list.size.toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "${currentIndex + 1} / ${list.size}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Card Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val item = list[currentIndex]
            val learned = isLearned(item)
            
            FlipCard(
                frontText = onFront(item),
                backText = onBack(item),
                isFlipped = isFlipped,
                onFlip = { isFlipped = !isFlipped },
                color = color,
                isLearned = learned,
                onToggleLearned = { onToggleLearned(item, it) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous Button
            FilledIconButton(
                onClick = { if (currentIndex > 0) currentIndex-- },
                enabled = currentIndex > 0,
                modifier = Modifier.size(56.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Previous"
                )
            }
            
            // Flip Hint
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(color.copy(alpha = 0.1f))
                    .clickable { isFlipped = !isFlipped }
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = if (isFlipped) "Show Question" else "Show Answer",
                    style = MaterialTheme.typography.labelLarge,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            }

            // Next Button
            FilledIconButton(
                onClick = { if (currentIndex < list.size - 1) currentIndex++ },
                enabled = currentIndex < list.size - 1,
                modifier = Modifier.size(56.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = color, // Use theme color for primary action
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward, // Need ArrowForward
                    contentDescription = "Next"
                )
            }
        }
    }
}

@Composable
fun FlipCard(
    frontText: String,
    backText: String,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    color: Color,
    isLearned: Boolean,
    onToggleLearned: (Boolean) -> Unit
) {
    val rotation by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessVeryLow
        )
    )
    val density = LocalDensity.current.density

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize() // Maximized size
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable { onFlip() },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp, // Slightly higher elevation
            pressedElevation = 2.dp
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .graphicsLayer {
                    if (rotation > 90f) {
                        rotationY = 180f
                    }
                },
            contentAlignment = Alignment.Center
        ) {
             // Learned Toggle (Visible on both sides)
             Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onToggleLearned(!isLearned) }
                    .background(if (isLearned) color else Color.Transparent)
                    .border(1.dp, if (isLearned) Color.Transparent else color, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isLearned) "Learned" else "Mark Learned",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isLearned) Color.White else color,
                    fontWeight = FontWeight.Bold
                )
            }
            
            if (rotation <= 90f) {
                // Front Content
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = frontText,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tap to flip",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            } else {
                // Back Content
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = backText,
                        style = MaterialTheme.typography.headlineSmall,
                        color = color, // Use category color for answer
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun KanjiList(list: List<Kanji>, learnedSet: Set<String>, onLearned: (String, Boolean) -> Unit) {
    FlashcardGame(
        list = list,
        onFront = { it.character },
        onBack = { 
            "Meaning: ${it.meaning}\n\nOn: ${it.onyomi.joinToString()}\nKun: ${it.kunyomi.joinToString()}" 
        },
        isLearned = { learnedSet.contains(it.character) },
        onToggleLearned = { item, learned -> onLearned(item.character, learned) },
        color = Color(0xFFE91E63)
    )
}

@Composable
fun VocabList(list: List<Vocab>, learnedSet: Set<String>, onLearned: (String, Boolean) -> Unit) {
    FlashcardGame(
        list = list,
        onFront = { it.word },
        onBack = { "${it.reading}\n\n${it.meaning}" },
        isLearned = { learnedSet.contains(it.word) },
        onToggleLearned = { item, learned -> onLearned(item.word, learned) },
        color = Color(0xFFFF9800)
    )
}

@Composable
fun GrammarList(list: List<Grammar>, learnedSet: Set<String>, onLearned: (String, Boolean) -> Unit) {
    FlashcardGame(
        list = list,
        onFront = { it.pattern },
        onBack = { "${it.meaning}\n\nEx: ${it.example}" },
        isLearned = { learnedSet.contains(it.pattern) },
        onToggleLearned = { item, learned -> onLearned(item.pattern, learned) },
        color = Color(0xFF2196F3)
    )
}

@Composable
fun StudyCard(
    title: String,
    subtitle: String,
    details: @Composable () -> Unit,
    color: Color
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title.take(1),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (!expanded) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
            
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Divider(color = color.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(subtitle, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    details()
                }
            }
        }
    }
}
