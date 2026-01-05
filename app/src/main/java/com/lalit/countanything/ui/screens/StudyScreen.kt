package com.lalit.countanything.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lalit.countanything.ui.components.springyTouch
import com.lalit.countanything.ui.models.Grammar
import com.lalit.countanything.ui.models.Kanji
import com.lalit.countanything.ui.models.Vocab
import com.lalit.countanything.ui.viewmodels.StudyViewModel

@Composable
fun StudyScreen(
    viewModel: StudyViewModel = viewModel()
) {
    var selectedLevel by remember { mutableStateOf<String?>(null) }
    
    if (selectedLevel == null) {
        VibrantDashboard(
            viewModel = viewModel,
            onLevelSelected = { selectedLevel = it }
        )
    } else {
        VibrantDetailScreen(
            level = selectedLevel!!,
            viewModel = viewModel,
            onBack = { selectedLevel = null }
        )
    }
}

@Composable
fun VibrantDashboard(
    viewModel: StudyViewModel,
    onLevelSelected: (String) -> Unit
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

    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 24.dp
    
    // Dynamic Colors
    val surfaceColor = MaterialTheme.colorScheme.surface
    val primaryColor = MaterialTheme.colorScheme.primary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    Box(modifier = Modifier.fillMaxSize().background(surfaceColor)) {
        // Dynamic Blobs Overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = primaryColor.copy(alpha = 0.05f),
                radius = 600f,
                center = Offset(size.width, 0f)
            )
            drawCircle(
                color = tertiaryColor.copy(alpha = 0.05f),
                radius = 500f,
                center = Offset(0f, size.height)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
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
                VibrantHeader(totalLearned, totalItems, progress)
            }

            // --- Stats BENTOS ---
            item {
                VibrantStatsRow(
                    kanjiCount = kanjiList.size,
                    kanjiLearned = learnedKanji.size,
                    vocabCount = vocabList.size,
                    vocabLearned = learnedVocab.size,
                    grammarCount = grammarList.size, 
                    grammarLearned = learnedGrammar.size
                )
            }

            item {
                Text(
                    "Modules",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
            }

            // --- Level Card ---
            item {
                VibrantCourseCard(
                    title = "JLPT N2",
                    tag = "Intermediate",
                    subtitle = "The Real Deal",
                    progress = progress, 
                    color1 = MaterialTheme.colorScheme.primary,
                    color2 = MaterialTheme.colorScheme.primaryContainer,
                    icon = "⚡",
                    onClick = { onLevelSelected("N2") }
                )
            }
        }
    }
}

@Composable
fun VibrantHeader(totalLearned: Int, totalItems: Int, progress: Float) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Hey, Super Star! 🌟",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$totalLearned / $totalItems XP Gained",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Donut Chart
        Box(contentAlignment = Alignment.Center) {
            val chartColor = MaterialTheme.colorScheme.tertiary
            Canvas(modifier = Modifier.size(72.dp)) {
                val stroke = 8.dp.toPx()
                
                // Track
                drawCircle(
                    color = chartColor.copy(alpha = 0.1f),
                    radius = size.minDimension/2 - stroke/2,
                    style = Stroke(width = stroke)
                )

                // Progress
                drawArc(
                    color = chartColor,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            }
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = chartColor
            )
        }
    }
}

@Composable
fun VibrantStatsRow(
    kanjiCount: Int, kanjiLearned: Int,
    vocabCount: Int, vocabLearned: Int,
    grammarCount: Int, grammarLearned: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BentoStat(
            title = "Kanji",
            learned = kanjiLearned,
            total = kanjiCount,
            color = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            icon = "字",
            modifier = Modifier.weight(1f)
        )
        BentoStat(
            title = "Vocab",
            learned = vocabLearned,
            total = vocabCount,
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            icon = "A",
            modifier = Modifier.weight(1f)
        )
        BentoStat(
            title = "Grammar",
            learned = grammarLearned,
            total = grammarCount,
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            icon = "¶",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun BentoStat(
    title: String,
    learned: Int,
    total: Int,
    color: Color,
    contentColor: Color,
    icon: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(110.dp).springyTouch(),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Flat for material feel
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(contentColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontWeight = FontWeight.Black, color = contentColor)
            }
            
            Column {
                Text(
                    "$learned",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = contentColor
                )
                Text(
                    title,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun VibrantCourseCard(
    title: String,
    tag: String,
    subtitle: String,
    progress: Float,
    color1: Color,
    color2: Color,
    icon: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .springyTouch()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
             // Gradient Background Hint
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.horizontalGradient(listOf(color1.copy(alpha = 0.05f), color2.copy(alpha = 0.05f))))
            )

            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon Box
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = color1)
                        .background(
                            Brush.linearGradient(listOf(color1, color2)),
                            RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(icon, fontSize = 28.sp, color = MaterialTheme.colorScheme.onPrimary)
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column(modifier = Modifier.weight(1f)) {
                     Box(
                        modifier = Modifier
                            .background(color1.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            tag.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = color1
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Progress
                if (progress > 0) {
                     CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(32.dp),
                        color = color1,
                        trackColor = color1.copy(alpha = 0.2f),
                        strokeWidth = 4.dp
                     )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VibrantDetailScreen(
    level: String,
    viewModel: StudyViewModel,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<Any?>(null) }
    var showListDialog by remember { mutableStateOf(false) }
    
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
    
    val themeColor = when(selectedTab) {
        0 -> MaterialTheme.colorScheme.tertiary
        1 -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.primary
    }

    if (showAddDialog) {
        AddStudyItemDialog(
            type = selectedTab,
            onDismiss = { showAddDialog = false },
            onAddKanji = { c, m, o, k, e -> viewModel.addKanji(c, m, o, k, e) },
            onAddVocab = { w, r, m, e -> viewModel.addVocab(w, r, m, e) },
            onAddGrammar = { p, m, e -> viewModel.addGrammar(p, m, e) }
        )
    }

    if (showEditDialog && editingItem != null) {
        EditStudyItemDialog(
            type = selectedTab,
            initialData = editingItem!!,
            onDismiss = { showEditDialog = false; editingItem = null },
            onUpdateKanji = { c, m, o, k, e -> viewModel.updateKanji(c, m, o, k, e) },
            onUpdateVocab = { w, r, m, e -> viewModel.updateVocab(w, r, m, e) },
            onUpdateGrammar = { p, m, e -> viewModel.updateGrammar(p, m, e) }
        )
    }

    if (showListDialog) {
        // Prepare list data
        val (title, items) = when(selectedTab) {
            0 -> "Kanji List" to kanjiList.map { it.character to it.meaning }
            1 -> "Vocab List" to vocabList.map { it.word to "${it.reading} • ${it.meaning}" }
            else -> "Grammar List" to grammarList.map { it.pattern to it.meaning }
        }
        
        StudyListDialog(
            title = title,
            items = items,
            themeColor = themeColor,
            onDismiss = { showListDialog = false }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        // Removed overlapping FAB
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
             // Subtle Atmospheric Glow
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = themeColor.copy(alpha = 0.15f),
                    radius = 900f,
                    center = Offset(size.width / 2, -100f)
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                // --- Distinct Header ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                         IconButton(
                            onClick = onBack,
                            modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                        ) {
                            Icon(Icons.Default.ArrowBack, null, tint = MaterialTheme.colorScheme.onSurface)
                        }
                        
                        Text(
                            text = level,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        // Right Side Actions
                        Row {
                            // Add Button (Moved from FAB)
                            IconButton(onClick = { showAddDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "New",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            // Show List Button
                            IconButton(onClick = { showListDialog = true }) { 
                                 Icon(
                                     imageVector = Icons.Default.List,
                                     contentDescription = "List",
                                     tint = MaterialTheme.colorScheme.onSurface 
                                 ) 
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    // --- Segmented Control Tabs ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.5f), CircleShape)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        tabs.forEachIndexed { index, (title, count) ->
                            val isSelected = selectedTab == index
                            val bgColor = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent
                            val textColor = if (isSelected) themeColor else MaterialTheme.colorScheme.onSurfaceVariant

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(CircleShape)
                                    .background(bgColor)
                                    .clickable { selectedTab = index },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title, // Just Title for cleaner look, maybe add count small?
                                    fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = textColor,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                    // Small count indicator below tabs
                    Text(
                        text = "${tabs[selectedTab].second} Items",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp)
                    )
                }
                
                // --- Game Area ---
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp) // Minimal padding
                ) {
                     when (selectedTab) {
                        0 -> VibrantFlashcardGame(kanjiList, { it.character }, { val ex = if(it.example.isNotEmpty()) "\nEx: ${it.example}" else ""; "Meaning: ${it.meaning}\nOn: ${it.onyomi}\nKun: ${it.kunyomi}$ex" }, { c -> learnedKanji.contains(c.character) }, { c, l -> viewModel.markKanjiLearned(c.character, l) }, { k -> editingItem = k; showEditDialog = true }, themeColor)
                        1 -> VibrantFlashcardGame(vocabList, { it.word }, { val ex = if(it.example.isNotEmpty()) "\nEx: ${it.example}" else ""; "${it.reading}\n${it.meaning}$ex" }, { w -> learnedVocab.contains(w.word) }, { w, l -> viewModel.markVocabLearned(w.word, l) }, { v -> editingItem = v; showEditDialog = true }, themeColor)
                        2 -> VibrantFlashcardGame(grammarList, { it.pattern }, { "${it.meaning}\nEx: ${it.example}" }, { g -> learnedGrammar.contains(g.pattern) }, { g, l -> viewModel.markGrammarLearned(g.pattern, l) }, { g -> editingItem = g; showEditDialog = true }, themeColor)
                    }
                }
            }
        }
    }
}

@Composable
fun StudyListDialog(
    title: String,
    items: List<Pair<String, String>>,
    themeColor: Color,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(title, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
        },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items) { (main, sub) ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                main,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = themeColor,
                                modifier = Modifier.width(60.dp)
                            )
                            Text(
                                sub,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = themeColor, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun <T> VibrantFlashcardGame(
    list: List<T>,
    onFront: (T) -> String,
    onBack: (T) -> String,
    isLearned: (T) -> Boolean,
    onToggleLearned: (T, Boolean) -> Unit,
    onEdit: (T) -> Unit,
    themeColor: Color
) {
     if (list.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Star, null, tint = themeColor.copy(alpha=0.3f), modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No cards yet", 
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.Bold, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    LaunchedEffect(currentIndex) { isFlipped = false }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Card Stack Effect
        Box(
            modifier = Modifier
                .weight(1f) // Takes all available space
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val item = list[currentIndex]
            val learned = isLearned(item)
            
            // Dummy card behind for "Stack" look
            if (currentIndex < list.size - 1) {
                Box(
                    modifier = Modifier
                        .fillMaxSize() // Fill box
                        .padding(top = 12.dp, start = 12.dp, end = 12.dp, bottom = 0.dp) 
                        .scale(0.96f) // Less scale down
                        .offset(y = 16.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha=0.5f), RoundedCornerShape(32.dp))
                )
            }
            
            VibrantFlipCard(
                frontText = onFront(item),
                backText = onBack(item),
                isFlipped = isFlipped,
                onFlip = { isFlipped = !isFlipped },
                themeColor = themeColor,
                isLearned = learned,
                onToggleLearned = { onToggleLearned(item, it) },
                onEdit = { onEdit(item) }
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp)) // Reduced spacer

        // Large Gamepad Controls
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
             IconButton(
                onClick = { if (currentIndex > 0) currentIndex-- },
                enabled = currentIndex > 0,
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer, CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, "Prev", tint = MaterialTheme.colorScheme.onSurface)
            }
            
            // Main Button
            Button(
                onClick = { isFlipped = !isFlipped },
                modifier = Modifier
                    .height(56.dp)
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = themeColor,
                    contentColor = MaterialTheme.colorScheme.surface // Contrast?
                ),
                shape = RoundedCornerShape(24.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp)
            ) {
                Text(
                    if (isFlipped) "SHOW QUESTION" else "REVEAL ANSWER",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = { if (currentIndex < list.size - 1) currentIndex++ },
                enabled = currentIndex < list.size - 1,
                 modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer, CircleShape)
            ) {
                Icon(Icons.Default.ArrowForward, "Next", tint = MaterialTheme.colorScheme.onSurface)
            }
        }
        
        // Progress Bar Line
        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / list.size },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
            color = themeColor,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )

        Spacer(modifier = Modifier.height(120.dp))
    }
}

@Composable
fun VibrantFlipCard(
    frontText: String,
    backText: String,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    themeColor: Color,
    isLearned: Boolean,
    onToggleLearned: (Boolean) -> Unit,
    onEdit: () -> Unit
) {
    // Content Color Logic
    val cardBg = MaterialTheme.colorScheme.surfaceContainerLow
    val textColor = MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onFlip() },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, themeColor.copy(alpha = 0.3f), RoundedCornerShape(32.dp)),
        ) {
             // Star (Learned)
             IconButton(
                onClick = { onToggleLearned(!isLearned) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(20.dp)
                    .zIndex(1f) // Ensure it's on top
                    .background(MaterialTheme.colorScheme.surface.copy(alpha=0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Learned",
                    tint = if (isLearned) themeColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                    modifier = Modifier.size(32.dp)
                )
            }
            
            // Edit Button (Top Left)
            IconButton(
                onClick = onEdit,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(20.dp)
                    .zIndex(1f)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha=0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Question Area (Takes remaining space or shared)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = frontText,
                        fontSize = 90.sp, // Slightly reduced to ensure fit with answer
                        fontWeight = FontWeight.Black,
                        color = textColor,
                        textAlign = TextAlign.Center,
                        lineHeight = 100.sp
                    )
                }

                // Answer Area (Appears below)
                AnimatedVisibility(
                    visible = isFlipped,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 48.dp, vertical = 16.dp),
                            color = themeColor.copy(alpha=0.3f),
                            thickness = 2.dp
                        )
                        
                        Text(
                            text = backText,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = themeColor // Highlight answer with theme color
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun EditStudyItemDialog(
    type: Int,
    initialData: Any, // Kanji, Vocab, or Grammar
    onDismiss: () -> Unit,
    onUpdateKanji: (String, String, String, String, String) -> Unit,
    onUpdateVocab: (String, String, String, String) -> Unit,
    onUpdateGrammar: (String, String, String) -> Unit
) {
    // Initial values logic
    var field1 by remember { mutableStateOf("") }
    var field2 by remember { mutableStateOf("") }
    var field3 by remember { mutableStateOf("") }
    var field4 by remember { mutableStateOf("") }
    var field5 by remember { mutableStateOf("") } // Example

    LaunchedEffect(initialData) {
        when (type) {
            0 -> {
                val k = initialData as Kanji
                field1 = k.character
                field2 = k.meaning
                field3 = k.onyomi.joinToString(", ")
                field4 = k.kunyomi.joinToString(", ")
                field5 = k.example
            }
            1 -> {
                val v = initialData as Vocab
                field1 = v.word
                field2 = v.reading
                field3 = v.meaning
                field5 = v.example
            }
            2 -> {
                val g = initialData as Grammar
                field1 = g.pattern
                field2 = g.meaning
                field3 = g.example
            }
        }
    }

    val title = "Edit Item"
    
    val color = when(type) {
        0 -> MaterialTheme.colorScheme.tertiary
        1 -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.primary
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        title = { Text(title, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                when (type) {
                    0 -> {
                        VibrantTextField(field1, { field1 = it }, "Character (Key)", color) 
                        VibrantTextField(field2, { field2 = it }, "Meaning", color)
                        VibrantTextField(field3, { field3 = it }, "Onyomi", color)
                        VibrantTextField(field4, { field4 = it }, "Kunyomi", color)
                        VibrantTextField(field5, { field5 = it }, "Example", color)
                    }
                    1 -> {
                        VibrantTextField(field1, { field1 = it }, "Word", color)
                        VibrantTextField(field2, { field2 = it }, "Reading", color)
                        VibrantTextField(field3, { field3 = it }, "Meaning", color)
                        VibrantTextField(field5, { field5 = it }, "Example", color)
                    }
                    2 -> {
                        VibrantTextField(field1, { field1 = it }, "Pattern", color)
                        VibrantTextField(field2, { field2 = it }, "Meaning", color)
                        VibrantTextField(field3, { field3 = it }, "Example", color)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when (type) {
                        0 -> onUpdateKanji(field1, field2, field3, field4, field5)
                        1 -> onUpdateVocab(field1, field2, field3, field5)
                        2 -> onUpdateGrammar(field1, field2, field3)
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = color)
            ) {
                Text(
                    "UPDATE", 
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun AddStudyItemDialog(
    type: Int, 
    onDismiss: () -> Unit,
    onAddKanji: (String, String, String, String, String) -> Unit,
    onAddVocab: (String, String, String, String) -> Unit,
    onAddGrammar: (String, String, String) -> Unit
) {
    var field1 by remember { mutableStateOf("") }
    var field2 by remember { mutableStateOf("") }
    var field3 by remember { mutableStateOf("") }
    var field4 by remember { mutableStateOf("") }
    var field5 by remember { mutableStateOf("") }

    val title = when (type) {
        0 -> "New Kanji"
        1 -> "New Vocab"
        else -> "New Grammar"
    }
    
    val color = when(type) {
        0 -> MaterialTheme.colorScheme.tertiary
        1 -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.primary
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        title = { Text(title, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                when (type) {
                    0 -> {
                        VibrantTextField(field1, { field1 = it }, "Character", color)
                        VibrantTextField(field2, { field2 = it }, "Meaning", color)
                        VibrantTextField(field3, { field3 = it }, "Onyomi", color)
                        VibrantTextField(field4, { field4 = it }, "Kunyomi", color)
                        VibrantTextField(field5, { field5 = it }, "Example", color)
                    }
                    1 -> {
                        VibrantTextField(field1, { field1 = it }, "Word", color)
                        VibrantTextField(field2, { field2 = it }, "Reading", color)
                        VibrantTextField(field3, { field3 = it }, "Meaning", color)
                        VibrantTextField(field5, { field5 = it }, "Example", color)
                    }
                    2 -> {
                        VibrantTextField(field1, { field1 = it }, "Pattern", color)
                        VibrantTextField(field2, { field2 = it }, "Meaning", color)
                        VibrantTextField(field3, { field3 = it }, "Example", color)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when (type) {
                        0 -> onAddKanji(field1, field2, field3, field4, field5)
                        1 -> onAddVocab(field1, field2, field3, field5)
                        2 -> onAddGrammar(field1, field2, field3)
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = color)
            ) {
                Text(
                    "SAVE IT!", 
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun VibrantTextField(value: String, onValueChange: (String) -> Unit, label: String, color: Color) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontWeight = FontWeight.Bold) },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = color,
            focusedLabelColor = color,
            cursorColor = color
        ),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
}
