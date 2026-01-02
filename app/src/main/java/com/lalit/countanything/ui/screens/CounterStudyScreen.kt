package com.lalit.countanything.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lalit.countanything.ui.models.JpCounter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterStudyScreen(
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    // Static Data (Can be moved to ViewModel later)
    val counters = remember {
        listOf(
            JpCounter("つ", "tsu", "General/Abstract things", "Ideas, tasks, varied objects"),
            JpCounter("個", "ko", "Small objects", "Eraser, egg, ball, shadows"),
            JpCounter("人", "nin", "People", "Boy, student, teacher"),
            JpCounter("本", "hon", "Long, cylindrical objects", "Pencil, bottle, umbrella, tree, road"),
            JpCounter("枚", "mai", "Flat, thin objects", "Paper, shirt, plate, CD"),
            JpCounter("匹", "hiki", "Small animals", "Cat, dog, fish, insect, monster"),
            JpCounter("頭", "tou", "Large animals", "Elephant, whale, horse, griffin"),
            JpCounter("羽", "wa", "Birds & Rabbits", "Chicken, crane, rabbit(!)"),
            JpCounter("冊", "satsu", "Bound objects/Books", "Book, magazine, notebook"),
            JpCounter("台", "dai", "Machines/Vehicles", "Car, bike, TV, washing machine"),
            JpCounter("階", "kai", "Floors of a building", "1st floor, basement"),
            JpCounter("足", "soku", "Pairs of footwear", "Shoes, socks"),
            JpCounter("着", "chaku", "Clothes", "Suits, coats"),
            JpCounter("杯", "hai", "Cups/Glasses of drink", "Beer, water, coffee, squid/octopus(!)"),
            JpCounter("軒", "ken", "Houses/Buildings", "House, shop, ramen stand"),
            JpCounter("錠", "jou", "Pills/Tablets", "Medicine"),
            JpCounter("回", "kai", "Times/Occurrences", "Once, twice"),
            JpCounter("歳", "sai", "Age", "1 year old"),
            JpCounter("通", "tsuu", "Letters/Emails", "Mail, Line message"),
            JpCounter("客", "kyaku", "Sets of furniture", "Sofa, table and chairs")
        )
    }

    val filteredCounters = if (searchQuery.isBlank()) {
        counters
    } else {
        counters.filter {
            it.usage.contains(searchQuery, ignoreCase = true) ||
            it.examples.contains(searchQuery, ignoreCase = true) ||
            it.reading.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            Column {
                LargeTopAppBar(
                    title = { Text("Counter Dictionary") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, "Back")
                        }
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
                // Search Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search (e.g., 'Book', 'Beer', 'Dog')") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredCounters) { counter ->
                CounterCard(counter)
            }
        }
    }
}

@Composable
fun CounterCard(counter: JpCounter) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Kanji Circle
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = counter.kanji,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = counter.reading,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = counter.usage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ex: ${counter.examples}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
