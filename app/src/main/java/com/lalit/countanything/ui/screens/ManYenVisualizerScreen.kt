package com.lalit.countanything.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lalit.countanything.R
import java.lang.StringBuilder

// Theme Colors
private val NeonAmber = Color(0xFFFFAB00)
private val DarkAmber = Color(0xFFE65100)
private val BgDark = Color(0xFF101010)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManYenVisualizerScreen(
    onBack: () -> Unit
) {
    var rawInput by remember { mutableStateOf("") }
    val amount = rawInput.toLongOrNull() ?: 0L
    val jpText = convertToJpUnits(amount)
    
    // Visualization Data
    val manCount = amount / 10000
    val bundles = manCount / 100
    val singles = manCount % 100

    Scaffold(
        containerColor = BgDark,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "ASSET_RENDERER // VIZ", 
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = (-0.5).sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = NeonAmber)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BgDark,
                    titleContentColor = NeonAmber
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // --- Tech Header ---
            TechHeader(amount = amount)

            Spacer(modifier = Modifier.height(24.dp))

            // --- Input Section ---
            OutlinedTextField(
                value = rawInput,
                onValueChange = { if (it.all { c -> c.isDigit() } && it.length < 16) rawInput = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonAmber,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonAmber,
                    unfocusedBorderColor = NeonAmber.copy(alpha = 0.5f),
                    cursorColor = NeonAmber,
                     focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                singleLine = true,
                placeholder = { 
                    Text(
                        "ENTER_VALUE...", 
                        fontFamily = FontFamily.Monospace, 
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    ) 
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- The Readout (Tech Card) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1E1E), CutCornerShape(topStart = 0.dp, bottomEnd = 24.dp))
                    .border(1.dp, NeonAmber.copy(alpha = 0.3f), CutCornerShape(topStart = 0.dp, bottomEnd = 24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "> CALCULATED_ASSET_VALUE_JP",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = jpText,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Default // Keep Kanji readable
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Box(Modifier.fillMaxWidth(0.5f).height(1.dp).background(NeonAmber.copy(alpha = 0.3f)))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "> CONVERTED_TO_INDIAN_SYSTEM",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = convertToIndianUnits(amount),
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF9933),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // --- Visualization ---
            Text(
                "> HOLOGRAM_PROJECTION_MATRIX",
                fontFamily = FontFamily.Monospace,
                color = NeonAmber.copy(alpha = 0.6f),
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .border(1.dp, NeonAmber.copy(alpha = 0.2f))
                    .background(Color(0xFF0F0F0F))
                    .padding(8.dp)
            ) {
                 if (manCount == 0L) {
                     Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                         Text(
                             "[NO DATA STREAM]", 
                             fontFamily = FontFamily.Monospace, 
                             color = Color.DarkGray
                         )
                     }
                 } else {
                     LazyVerticalGrid(
                         columns = GridCells.Adaptive(minSize = 60.dp),
                         horizontalArrangement = Arrangement.spacedBy(4.dp),
                         verticalArrangement = Arrangement.spacedBy(4.dp)
                     ) {
                         // Full Bundles
                         items(bundles.toInt().coerceAtMost(1000)) { 
                             TechMoneyBundle(isFullStack = true)
                         }
                         // Single Man bills
                         if (singles > 0) {
                             item {
                                 TechMoneyBundle(isFullStack = false, count = singles.toInt())
                             }
                         }
                         
                         if (bundles > 1000) {
                             item {
                                 Box(
                                     modifier = Modifier.size(60.dp),
                                     contentAlignment = Alignment.Center
                                 ) {
                                     Text("+${bundles - 1000} OVERFLOW", fontFamily = FontFamily.Monospace, color = NeonAmber, fontSize = 8.sp, textAlign = TextAlign.Center)
                                 }
                             }
                         }
                     }
                 }
            }
        }
    }
}

@Composable
fun TechHeader(amount: Long) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
             Text(
                "SYSTEM_READY",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = NeonAmber
            )
            Text(
                "VOLTAGE: STABLE",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun TechMoneyBundle(isFullStack: Boolean, count: Int = 1) {
    // Wireframe Colors
    val WireColor = NeonAmber.copy(alpha = 0.8f)
    val FillColor = NeonAmber.copy(alpha = 0.1f)
    
    val width = 60.dp
    val stackHeight = if (isFullStack) 25.dp else 4.dp + (count * 0.5).dp
    
    Box(
        modifier = Modifier
            .width(width)
            .height(50.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = stackHeight.toPx()
            
            // Isometric Offset
            val isoX = 10f
            val isoY = 10f
            
            val baseX = 10f
            val baseY = size.height - 10f
            
            // Paths
            val pathSide = Path().apply {
                moveTo(baseX + w - 20f, baseY)
                lineTo(baseX + w - 20f, baseY - h)
                lineTo(baseX + w - 20f + isoX, baseY - h - isoY)
                lineTo(baseX + w - 20f + isoX, baseY - isoY)
                close()
            }
            
            val pathTop = Path().apply {
                moveTo(baseX, baseY - h)
                lineTo(baseX + w - 20f, baseY - h)
                lineTo(baseX + w - 20f + isoX, baseY - h - isoY)
                lineTo(baseX + isoX, baseY - h - isoY)
                close()
            }

            val pathFront = Path().apply {
                moveTo(baseX, baseY)
                lineTo(baseX, baseY - h)
                lineTo(baseX + w - 20f, baseY - h)
                lineTo(baseX + w - 20f, baseY)
                close()
            }
            
            // Draw Fills
            drawPath(pathSide, FillColor)
            drawPath(pathTop, FillColor)
            drawPath(pathFront, FillColor)
            
            // Draw Wireframes
            drawPath(pathSide, WireColor, style = Stroke(width = 1.5f))
            drawPath(pathTop, WireColor, style = Stroke(width = 1.5f))
            drawPath(pathFront, WireColor, style = Stroke(width = 1.5f))

            // Tech Details
            if (isFullStack) {
                // Draw Band as a solid neon strip
                val bandX = baseX + 20f
                drawLine(
                    color = NeonAmber,
                    start = androidx.compose.ui.geometry.Offset(bandX, baseY - h + 5f),
                    end = androidx.compose.ui.geometry.Offset(bandX, baseY - 5f),
                    strokeWidth = 10f
                )
            }
        }
        
        // Label
        Text(
             if (isFullStack) "100" else "$count",
             fontSize = 9.sp,
             fontFamily = FontFamily.Monospace,
             color = NeonAmber,
             fontWeight = FontWeight.Bold,
             modifier = Modifier.align(Alignment.BottomCenter).offset(y = 4.dp),
        )
    }
}

fun convertToJpUnits(amount: Long): String {
    if (amount == 0L) return "0円"
    if (amount < 10000) return "%,d".format(amount) + "円"

    val man = 10000L
    val oku = 100000000L
    val cho = 1000000000000L

    val sb = StringBuilder()
    val c = amount / cho
    if (c > 0) sb.append("${c}兆")
    val o = (amount % cho) / oku
    if (o > 0) sb.append("${o}億")
    val m = (amount % oku) / man
    if (m > 0) sb.append("${m}万")
    val r = amount % man
    if (r > 0) sb.append(r)
    sb.append("円")
    return sb.toString()
}

fun convertToIndianUnits(amount: Long): String {
    if (amount == 0L) return "₹0"
    val crore = 10000000L
    val lakh = 100000L
    val thousand = 1000L
    val sb = StringBuilder("₹")
    val cr = amount / crore
    if (cr > 0) sb.append("$cr Cr ")
    val l = (amount % crore) / lakh
    if (l > 0) sb.append("$l Lakh ")
    val th = (amount % lakh) / thousand
    if (th > 0) sb.append("$th K ")
    val rem = amount % thousand
    if (rem > 0) sb.append(rem)
    return sb.toString().trim()
}
