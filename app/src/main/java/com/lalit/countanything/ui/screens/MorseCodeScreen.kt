package com.lalit.countanything.ui.screens

import android.content.Context
import android.hardware.camera2.CameraManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.lalit.countanything.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lalit.countanything.ui.components.springyTouch
import com.lalit.countanything.utils.MorseCodeHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MorseCodeScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }
    var isTransmitting by remember { mutableStateOf(false) }
    var currentSignalIndex by remember { mutableStateOf(-1) }
    var isLightOn by remember { mutableStateOf(false) } 
    
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()

    // Cyberpunk Theme Colors
    val neonGreen = Color(0xFF76FF03)
    val bgDark = Color(0xFF101010)
    val panelBg = Color(0xFF1E1E1E)

    // Flashlight Controller (Preserved)
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val cameraId = try { cameraManager.cameraIdList[0] } catch (e: Exception) { null }

    fun setTorch(on: Boolean) {
        isLightOn = on
        try {
            cameraId?.let { cameraManager.setTorchMode(it, on) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    DisposableEffect(Unit) {
        onDispose { setTorch(false) }
    }

    val signals = remember(text) { MorseCodeHelper.toMorse(text) }
    
    // Transmission Loop (Preserved)
    fun startTransmission() {
        if (isTransmitting || text.isBlank()) return
        isTransmitting = true
        focusManager.clearFocus()
        
        scope.launch {
            val baseUnitMs = 150L 
            signals.forEachIndexed { index, signal ->
                if (!isTransmitting) return@launch
                currentSignalIndex = index
                listState.animateScrollToItem(index)
                
                when (signal) {
                    MorseCodeHelper.Signal.DOT -> {
                        setTorch(true); delay(baseUnitMs); setTorch(false); delay(baseUnitMs)
                    }
                    MorseCodeHelper.Signal.DASH -> {
                        setTorch(true); delay(baseUnitMs * 3); setTorch(false); delay(baseUnitMs)
                    }
                    MorseCodeHelper.Signal.SPACE_LETTER -> delay(baseUnitMs * 2) 
                    MorseCodeHelper.Signal.SPACE_WORD -> delay(baseUnitMs * 6)
                }
            }
            isTransmitting = false
            currentSignalIndex = -1
            setTorch(false)
        }
    }

    fun stopTransmission() {
        isTransmitting = false
        setTorch(false)
        currentSignalIndex = -1
    }

    // UI Animations
    val lightScale by animateFloatAsState(
        targetValue = if (isLightOn) 1.2f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
    val lightColor by animateColorAsState(
        targetValue = if (isLightOn) neonGreen else Color(0xFF1B5E20), // Bright Green vs Dark Green
        animationSpec = tween(100)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "SIGNAL_TRANSMITTER", 
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = (-0.5).sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = neonGreen)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = bgDark,
                    titleContentColor = neonGreen
                )
            )
        },
        containerColor = bgDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // 1. SIGNAL VISUALIZER (The "Light Bulb")
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Glow Halo
                if (isLightOn) {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(neonGreen.copy(alpha = 0.3f), Color.Transparent)
                                )
                            )
                    )
                }

                // The Emitter
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .graphicsLayer { scaleX = lightScale; scaleY = lightScale }
                        .shadow(elevation = if (isLightOn) 24.dp else 4.dp, shape = CircleShape)
                        .background(Color.Black, CircleShape)
                        .border(4.dp, if(isLightOn) neonGreen else Color.DarkGray, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = lightColor,
                        modifier = Modifier.size(64.dp)
                    )
                }
                
                Text(
                    "OPTICAL_OUTPUT",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
                )
            }

            // 2. TICKER TAPE PREVIEW (Tech Box)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(panelBg, RoundedCornerShape(4.dp))
                    .border(1.dp, neonGreen.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "SIGNAL_BUFFER",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = neonGreen
                        )
                        if(isTransmitting) {
                             Text(
                                "TRANSMITTING...",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyRow(
                        state = listState,
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (signals.isEmpty()) {
                            item {
                                Text(
                                    "AWAITING_INPUT...",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        itemsIndexed(signals) { index, signal ->
                            val isActive = index == currentSignalIndex
                            val color = if (isActive) neonGreen else Color.DarkGray
                            val scale by animateFloatAsState(if (isActive) 1.5f else 1f)

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .height(24.dp)
                                    .padding(horizontal = 2.dp)
                            ) {
                                when (signal) {
                                    MorseCodeHelper.Signal.DOT -> {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .graphicsLayer { scaleX = scale; scaleY = scale }
                                                .background(color, CircleShape)
                                        )
                                    }
                                    MorseCodeHelper.Signal.DASH -> {
                                        Box(
                                            modifier = Modifier
                                                .width(24.dp)
                                                .height(10.dp)
                                                .graphicsLayer { scaleX = scale; scaleY = scale }
                                                .background(color, RoundedCornerShape(2.dp))
                                        )
                                    }
                                    MorseCodeHelper.Signal.SPACE_LETTER -> Spacer(Modifier.width(8.dp))
                                    MorseCodeHelper.Signal.SPACE_WORD -> {
                                        Spacer(Modifier.width(12.dp))
                                        Box(Modifier.width(2.dp).height(16.dp).background(Color.DarkGray))
                                        Spacer(Modifier.width(12.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. INPUT FIELD
            OutlinedTextField(
                value = text,
                onValueChange = { if (!isTransmitting) text = it.uppercase() },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("ENTER_MESSAGE...", fontFamily = FontFamily.Monospace, color = Color.Gray) },
                trailingIcon = {
                    if (text.isNotEmpty()) {
                        IconButton(onClick = { if(!isTransmitting) text = "" }) {
                            Icon(Icons.Default.Clear, "Clear", tint = Color.Gray)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = neonGreen,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedTextColor = neonGreen,
                    unfocusedTextColor = Color.White,
                    cursorColor = neonGreen,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                shape = CutCornerShape(0.dp), // Hard edges for tech look
                textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 4. TRANSMIT BUTTON (Tactical Style)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                val buttonColor = if (isTransmitting) Color(0xFFD32F2F) else neonGreen // Red to Stop, Green to Go
                
                Button(
                    onClick = {
                         if (isTransmitting) stopTransmission() else startTransmission()
                    },
                    modifier = Modifier
                        .width(200.dp)
                        .height(64.dp)
                        .springyTouch(),
                    shape = CutCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                   Row(verticalAlignment = Alignment.CenterVertically) {
                       Icon(
                           imageVector = if (isTransmitting) Icons.Default.Stop else Icons.Default.PlayArrow,
                           contentDescription = null,
                           tint = Color.Black
                       )
                       Spacer(Modifier.width(12.dp))
                       Text(
                           text = if(isTransmitting) "ABORT" else "TRANSMIT",
                           fontFamily = FontFamily.Monospace,
                           fontWeight = FontWeight.Black,
                           color = Color.Black,
                           fontSize = 18.sp
                       )
                   }
                }
            }
        }
    }
}
