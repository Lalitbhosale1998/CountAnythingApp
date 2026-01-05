package com.lalit.countanything.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TempleHindu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lalit.countanything.R
import com.lalit.countanything.ui.components.springyTouch

data class ToolItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val description: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen(
    onToolSelected: (String) -> Unit,
    isVaultVisible: Boolean = false
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    
    val tools = remember(isVaultVisible) {
        buildList {
            add(
                ToolItem(
                    id = "speed_dashboard",
                    title = "SPEED_DASH",
                    icon = Icons.Default.Speed,
                    description = "VELOCITY // G_FORCE",
                    color = Color(0xFF00E5FF) // Cyan
                )
            )
            add(
                ToolItem(
                    id = "coin_flip",
                    title = "COIN_OPS",
                    icon = Icons.Default.MonetizationOn,
                    description = "BINARY_DECISION_UNIT",
                    color = Color(0xFFFFD700) // Gold
                )
            )
            add(
                ToolItem(
                    id = "morse_code",
                    title = "MORSE_TX",
                    icon = Icons.Default.Highlight,
                    description = "OPTICAL_TRANSMITTER",
                    color = Color(0xFF76FF03) // Lime
                )
            )
            add(
                ToolItem(
                    id = "nengo_converter",
                    title = "TIME_WARP",
                    icon = Icons.Default.CalendarMonth,
                    description = "ERA_CONVERSION_SYS",
                    color = Color(0xFFFF4081) // Pink
                )
            )
            add(
                ToolItem(
                    id = "man_yen_visualizer",
                    title = "WEALTH_VIZ",
                    icon = Icons.Default.MonetizationOn,
                    description = "ASSET_STACK_RENDER",
                    color = Color(0xFFFFAB00) // Amber
                )
            )
            add(
                ToolItem(
                    id = "shrine_guide",
                    title = "SPIRIT_GUIDE",
                    icon = Icons.Default.TempleHindu,
                    description = "ETIQUETTE_PROTOCOL",
                    color = Color(0xFFFF5252) // Red
                )
            )
            
            if (isVaultVisible) {
                add(
                    ToolItem(
                        id = "hidden_vault",
                        title = "SECURE_VAULT",
                        icon = Icons.Default.Lock,
                        description = "ENCRYPTED_STORAGE_V2",
                        color = Color(0xFF607D8B) // Blue Grey
                    )
                )
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "LAB_BENCH // v.3.0",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-1).sp
                    )
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color(0xFF101010), // Deep Dark Background
                    scrolledContainerColor = Color(0xFF151515),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF121212) // Lab Dark Mode
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            contentPadding = PaddingValues(
                start = 16.dp, 
                end = 16.dp, 
                top = innerPadding.calculateTopPadding() + 16.dp, 
                bottom = 100.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                Text(
                    "> SYSTEM_MODULES_ONLINE",
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF00E5FF),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            items(tools) { tool ->
                LabModuleCard(tool = tool, onClick = { onToolSelected(tool.id) })
            }
        }
    }
}

@Composable
fun LabModuleCard(
    tool: ToolItem,
    onClick: () -> Unit
) {
    // Blinking effect for "status light"
    val infiniteTransition = rememberInfiniteTransition(label = "blink")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "led"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .springyTouch(),
        shape = CutCornerShape(topEnd = 24.dp, bottomStart = 8.dp, bottomEnd = 8.dp, topStart = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),
        border = BorderStroke(1.dp, tool.color.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick)
        ) {
            // "Scanline" gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                tool.color.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: ID + Status Light
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ID-${tool.id.take(4).uppercase()}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    
                    // Status LED
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(tool.color.copy(alpha = alpha)) // Animated alpha
                    )
                }

                // Middle: Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .border(1.dp, tool.color.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tool.icon,
                        contentDescription = null,
                        tint = tool.color,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                // Bottom: Title & Desc
                Column {
                    Text(
                        text = tool.title,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = tool.description,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        color = tool.color.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Decorative Corner
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(24.dp)
                    .background(tool.color.copy(alpha = 0.2f))
            )
        }
    }
}
