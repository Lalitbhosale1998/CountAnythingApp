package com.lalit.countanything.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.lalit.countanything.R
import com.lalit.countanything.ui.components.HabitHistoryCalendar
import com.lalit.countanything.ui.models.Counter
import com.lalit.countanything.ui.models.CounterType
import java.time.LocalDate
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(
    history: Map<String, Float>,
    sexualHealthHistory: Map<String, Float>,
    genericCounters: List<Counter>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    // --- 1. PREPARE DATA LIST ---
    // We flatten all history sources into a single list of items to page through
    data class HistoryPageItem(
        val title: String,
        val history: Map<String, Float>,
        val aura: Brush
    )

    // Auras
    val healthAura = Brush.verticalGradient(
        colors = listOf(Color(0xFFD32F2F), Color(0xFF7B1FA2)) // Ruby to Violet
    )
    val cigaretteAura = Brush.verticalGradient(
        colors = listOf(Color(0xFF424242), Color(0xFFFFA000)) // Smoky Gray to Deep Orange
    )
    val standardAura = Brush.verticalGradient(
        colors = listOf(Color(0xFF0D47A1), Color(0xFF00B0FF)) // Deep Blue to Neon Blue
    )

    val pages = remember(history, sexualHealthHistory, genericCounters) {
        val list = mutableListOf<HistoryPageItem>()
        
        // 1. Cigarettes
        list.add(
            HistoryPageItem(
                title = "Cigarettes",
                history = history,
                aura = cigaretteAura
            )
        )
        
        // 2. Sexual Health
        list.add(
            HistoryPageItem(
                title = "Sexual Health",
                history = sexualHealthHistory,
                aura = healthAura
            )
        )
        
        // 3. Generics
        genericCounters.forEach { counter ->
            val aura = if (counter.type == CounterType.SEXUAL_HEALTH) healthAura else standardAura
            list.add(
                HistoryPageItem(
                    title = counter.title,
                    history = counter.history,
                    aura = aura
                )
            )
        }
        list
    }

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    // --- SCAFFOLD ---
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    // Update title based on current page
                    val currentTitle = if (pages.isNotEmpty()) pages[pagerState.currentPage].title else "History"
                    Text(
                        text = currentTitle,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // --- HORIZONTAL PAGER ---
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 32.dp),
                pageSpacing = 16.dp,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                val item = pages[pageIndex]
                
                // Fun graphics layer animation for "Gallery" feel
                Card(
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // handled by border/color
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            // Calculate the absolute offset for the current page from the
                            // scroll position. We use the absolute value which allows us to mirror
                            // any effects for both directions
                            val pageOffset = (
                                (pagerState.currentPage - pageIndex) + pagerState
                                    .currentPageOffsetFraction
                            ).absoluteValue

                            // We animate the alpha, between 50% and 100%
                            alpha = lerp(
                                start = 0.5f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                             // Scale a bit
                             scaleY = lerp(
                                start = 0.85f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                             )
                        }
                ) {
                    // Reuse the existing Calendar Component
                    HabitHistoryCalendar(
                        title = item.title, // Pass title again if needed inside, though we show it in TopBar
                        history = item.history,
                        selectedDate = selectedDate,
                        onDateSelected = onDateSelected,
                        aura = item.aura
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // --- INDICATOR ---
            Row(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(8.dp)
                    )
                }
            }
        }
    }
}
