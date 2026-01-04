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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlashOn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
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
    var isLightOn by remember { mutableStateOf(false) } // For UI visualization
    
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    // Flashlight Controller
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
        onDispose {
            setTorch(false)
        }
    }

    val signals = remember(text) { MorseCodeHelper.toMorse(text) }
    
    // Transmission Loop
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
                        setTorch(true)
                        delay(baseUnitMs)
                        setTorch(false)
                        delay(baseUnitMs)
                    }
                    MorseCodeHelper.Signal.DASH -> {
                        setTorch(true)
                        delay(baseUnitMs * 3)
                        setTorch(false)
                        delay(baseUnitMs)
                    }
                    MorseCodeHelper.Signal.SPACE_LETTER -> {
                        // Already waited 1 unit from prev char, wait 2 more = 3 total
                        delay(baseUnitMs * 2) 
                    }
                    MorseCodeHelper.Signal.SPACE_WORD -> {
                        // Already waited 1 unit, wait 6 more = 7 total
                        delay(baseUnitMs * 6)
                    }
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
        targetValue = if (isLightOn) Color(0xFFFFFF00) else Color(0xFF424242),
        animationSpec = tween(100)
    )
    val containerColor = MaterialTheme.colorScheme.background

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { 
                    Text(
                        stringResource(R.string.morse_title), 
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 40.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.cancel))
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = containerColor,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = containerColor
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
                                    colors = listOf(
                                        Color.Yellow.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }

                // The Bulb
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .graphicsLayer {
                            scaleX = lightScale
                            scaleY = lightScale
                        }
                        .shadow(elevation = if (isLightOn) 24.dp else 4.dp, shape = CircleShape)
                        .background(lightColor, CircleShape)
                        .border(4.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = if (isLightOn) Color.Black else Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // 2. TICKER TAPE PREVIEW
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        stringResource(R.string.morse_signal_preview),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
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
                                    stringResource(R.string.morse_waiting),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                        itemsIndexed(signals) { index, signal ->
                            val isActive = index == currentSignalIndex
                            val activeColor = MaterialTheme.colorScheme.primary
                            val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                            val color = if (isActive) activeColor else inactiveColor
                            
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
                                                .background(color, RoundedCornerShape(4.dp))
                                        )
                                    }
                                    MorseCodeHelper.Signal.SPACE_LETTER -> Spacer(Modifier.width(8.dp))
                                    MorseCodeHelper.Signal.SPACE_WORD -> {
                                        Spacer(Modifier.width(12.dp))
                                        Box(Modifier.width(2.dp).height(16.dp).background(MaterialTheme.colorScheme.outlineVariant))
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
                onValueChange = { if (!isTransmitting) text = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.morse_placeholder)) },
                trailingIcon = {
                    if (text.isNotEmpty()) {
                        IconButton(onClick = { if(!isTransmitting) text = "" }) {
                            Icon(Icons.Default.Clear, stringResource(R.string.morse_clear))
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 4. TRANSMIT BUTTON
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                val buttonColor = if (isTransmitting) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                
                Button(
                    onClick = {
                         if (isTransmitting) stopTransmission() else startTransmission()
                    },
                    modifier = Modifier
                        .size(96.dp)
                        .springyTouch(),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                   Icon(
                       imageVector = if (isTransmitting) Icons.Default.Clear else Icons.Default.FlashOn,
                       contentDescription = stringResource(R.string.morse_transmit),
                       modifier = Modifier.size(36.dp)
                   )
                }
            }
        }
    }
}
