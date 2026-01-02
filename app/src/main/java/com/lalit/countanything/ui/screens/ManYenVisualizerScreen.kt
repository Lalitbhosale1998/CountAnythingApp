package com.lalit.countanything.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.DecimalFormat

// Theme Colors
private val BankGreen = Color(0xFF1B5E20)
private val Gold = Color(0xFFFFD700)
private val MoneyPaper = Color(0xFFFBE9D0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManYenVisualizerScreen(
    onBack: () -> Unit
) {
    var rawInput by remember { mutableStateOf("") }
    
    // Parse input
    val amount = rawInput.toLongOrNull() ?: 0L
    
    // Conversion Logic
    val jpText = convertToJpUnits(amount)
    
    // Visualization Data
    // 1 Bill = 1 Man (10,000)
    // 1 Bundle = 100 Man (1 Million)
    val manCount = amount / 10000
    val bundles = manCount / 100
    val singles = manCount % 100

    Scaffold(
        containerColor = Color(0xFF121212), // Dark Vault
        topBar = {
            TopAppBar(
                title = { Text("Man-Yen Visualizer", color = Gold, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Gold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0A0A0A)
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
            // --- Input Section ---
            OutlinedTextField(
                value = rawInput,
                onValueChange = { if (it.all { c -> c.isDigit() } && it.length < 16) rawInput = it },
                label = { Text("Amount (Yen)", color = Gold.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gold,
                    unfocusedBorderColor = Gold.copy(alpha = 0.5f),
                    cursorColor = Gold
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- The Readout ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BankGreen),
                border = androidx.compose.foundation.BorderStroke(2.dp, Gold)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "JAPANESE READING",
                        style = MaterialTheme.typography.labelMedium,
                        color = Gold.copy(alpha = 0.8f),
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = jpText,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Gold.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "INDIAN READING",
                        style = MaterialTheme.typography.labelMedium,
                        color = Gold.copy(alpha = 0.8f),
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = convertToIndianUnits(amount),
                        style = MaterialTheme.typography.headlineMedium, // Slightly smaller than JP
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF9933), // Saffron-ish
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // --- Visualization ---
            Text(
                "VISUALIZATION (Stacks of ¥1,000,000)",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F0F0F))
                    .padding(8.dp)
            ) {
                 if (manCount == 0L) {
                     Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                         Text("Enter amount to visualize", color = Color.Gray)
                     }
                 } else {
                     LazyVerticalGrid(
                         columns = GridCells.Adaptive(minSize = 60.dp),
                         horizontalArrangement = Arrangement.spacedBy(4.dp),
                         verticalArrangement = Arrangement.spacedBy(4.dp)
                     ) {
                         // Full Bundles (1 Million Yen each)
                         items(bundles.toInt().coerceAtMost(1000)) { 
                             MoneyBundle(isFullStack = true)
                         }
                         // Single Man bills
                         if (singles > 0) {
                             item {
                                 MoneyBundle(isFullStack = false, count = singles.toInt())
                             }
                         }
                         
                         if (bundles > 1000) {
                             item {
                                 Box(
                                     modifier = Modifier.size(60.dp),
                                     contentAlignment = Alignment.Center
                                 ) {
                                     Text("+${bundles - 1000} more...", color = Color.Gray, fontSize = 10.sp, textAlign = TextAlign.Center)
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
fun MoneyBundle(isFullStack: Boolean, count: Int = 1) {
    // Colors
    val PaperBase = Color(0xFFEADDCA) // Fresh bill beige
    val PaperShadow = Color(0xFFC7B299) // Darker beige for side
    val InkGreen = Color(0xFF384E38) // Dark green ink
    val BandRed = Color(0xFFB71C1C) // Japanese bill band red
    
    // Dimensions
    val width = 60.dp
    // If full stack (100 bills), it's thick. If single bills, thin.
    // 100 bills ~ 1cm. Scaled up for visibility.
    val stackHeight = if (isFullStack) 25.dp else 4.dp + (count * 0.5).dp
    
    Box(
        modifier = Modifier
            .width(width)
            .height(50.dp), // Container
        contentAlignment = Alignment.BottomCenter
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = stackHeight.toPx()
            
            // Isometric Offset (Shift Right-Up)
            val isoX = 10f
            val isoY = 10f
            
            val baseX = 10f
            val baseY = size.height - 10f
            
            // 1. Draw Side Face (Thickness)
            val pathSide = Path().apply {
                moveTo(baseX + w - 20f, baseY)
                lineTo(baseX + w - 20f, baseY - h)
                lineTo(baseX + w - 20f + isoX, baseY - h - isoY)
                lineTo(baseX + w - 20f + isoX, baseY - isoY)
                close()
            }
            drawPath(pathSide, PaperShadow)
            
            // 2. Draw Front Face (The stack edge facing us)
            drawRect(
                color = PaperBase,
                topLeft = androidx.compose.ui.geometry.Offset(baseX, baseY - h),
                size = androidx.compose.ui.geometry.Size(w - 20f, h)
            )

            // 3. Draw Top Face (The bill surface)
            val pathTop = Path().apply {
                moveTo(baseX, baseY - h)
                lineTo(baseX + w - 20f, baseY - h)
                lineTo(baseX + w - 20f + isoX, baseY - h - isoY)
                lineTo(baseX + isoX, baseY - h - isoY)
                close()
            }
            drawPath(pathTop, PaperBase.copy(alpha = 0.9f)) // Slightly lighter
            
            // 4. Details (Band or Ink)
            if (isFullStack) {
                // Draw Red Band on Front
                drawRect(
                    color = BandRed,
                    topLeft = androidx.compose.ui.geometry.Offset(baseX + 10f, baseY - h + 5f),
                    size = androidx.compose.ui.geometry.Size(15f, h - 10f)
                )
                // Band on Top
                 val pathBandTop = Path().apply {
                    moveTo(baseX + 10f, baseY - h)
                    lineTo(baseX + 25f, baseY - h)
                    lineTo(baseX + 25f + isoX, baseY - h - isoY)
                    lineTo(baseX + 10f + isoX, baseY - h - isoY)
                    close()
                }
                drawPath(pathBandTop, BandRed)
                
                // Tiny "100万円" text simulation on the band
                val bandTextY = baseY - h + (h / 2)
                 drawRect(
                    color = Color.White.copy(alpha = 0.8f),
                    topLeft = androidx.compose.ui.geometry.Offset(baseX + 12f, bandTextY),
                    size = androidx.compose.ui.geometry.Size(11f, 2f)
                )
            } else {
                // Fake "Yukichi" Circle on top
                drawCircle(
                    color = InkGreen.copy(alpha = 0.2f),
                    radius = 8f,
                    center = androidx.compose.ui.geometry.Offset(baseX + 25f, baseY - h - 5f)
                )
            }
            
            // 5. Borders / Outlines for sharpness
            drawPath(pathSide, Color.Black.copy(alpha = 0.2f), style = Stroke(width = 1f))
            drawRect(
                color = Color.Black.copy(alpha = 0.2f),
                topLeft = androidx.compose.ui.geometry.Offset(baseX, baseY - h),
                size = androidx.compose.ui.geometry.Size(w - 20f, h),
                style = Stroke(width = 1f)
            )
            drawPath(pathTop, Color.Black.copy(alpha = 0.2f), style = Stroke(width = 1f))
        }
        
        // Label
        Text(
             if (isFullStack) "100万" else "${count}万",
             fontSize = 9.sp,
             color = Color.White,
             fontWeight = FontWeight.Bold,
             modifier = Modifier.align(Alignment.BottomCenter).offset(y = 4.dp),
             style = androidx.compose.ui.text.TextStyle(shadow = androidx.compose.ui.graphics.Shadow(Color.Black, blurRadius = 4f))
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

    // Trillions (Cho)
    val c = amount / cho
    if (c > 0) sb.append("${c}兆")
    
    // Billions (Oku)
    val o = (amount % cho) / oku
    if (o > 0) sb.append("${o}億")
    
    // Myriads (Man)
    val m = (amount % oku) / man
    if (m > 0) sb.append("${m}万")
    
    // Remainder
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
    if (th > 0) sb.append("$th Thousand ")
    
    val rem = amount % thousand
    if (rem > 0) sb.append(rem)
    
    return sb.toString().trim()
}
