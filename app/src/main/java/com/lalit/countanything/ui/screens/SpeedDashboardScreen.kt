package com.lalit.countanything.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun SpeedDashboardScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var speedKmH by remember { mutableStateOf(0f) }
    var gForceX by remember { mutableStateOf(0f) }
    var gForceY by remember { mutableStateOf(0f) }
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasPermission = isGranted
    }

    // --- SENSORS & LOCATION SETUP (Preserved) ---
    DisposableEffect(hasPermission) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (location.hasSpeed()) {
                    speedKmH = location.speed * 3.6f
                }
            }
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }

        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    if (it.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
                         gForceX = -it.values[0] / 9.81f 
                         gForceY = it.values[1] / 9.81f 
                    } else if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                         gForceX = -it.values[0] / 9.8f
                         gForceY = it.values[1] / 9.8f
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (hasPermission) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200L, 0f, locationListener)
        }

        val accel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) 
            ?: sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        
        sensorManager.registerListener(sensorListener, accel, SensorManager.SENSOR_DELAY_GAME)

        onDispose {
            locationManager.removeUpdates(locationListener)
            sensorManager.unregisterListener(sensorListener)
        }
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // --- CYBERPUNK LAB UI ---
    val neonCyan = Color(0xFF00E5FF)
    val bgDark = Color(0xFF101010)
    val panelBg = Color(0xFF1A1A1A)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "VELOCITY_SENSOR // 01", 
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = (-0.5).sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = neonCyan)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = bgDark,
                    titleContentColor = neonCyan
                )
            )
        },
        containerColor = bgDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- TOP HEADER INFO ---
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TechReadout(label = "SENSOR_STATUS", value = "ONLINE")
                TechReadout(label = "GPS_SIGNAL", value = if (hasPermission) "LOCKED" else "SEARCHING")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- SPEEDOMETER GAUGE ---
            Box(contentAlignment = Alignment.Center) {
                CyberGauges(speed = speedKmH, maxSpeed = 160f, color = neonCyan)
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "%.0f".format(speedKmH),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = (-4).sp
                    )
                    Text(
                        text = "KM/H",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                        color = neonCyan.copy(alpha = 0.7f),
                        letterSpacing = 2.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))

            // --- G-FORCE VISUALIZER ---
            Text(
                "ACCEL_GRAVITY_VECTOR",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = neonCyan.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(CutCornerShape(20.dp))
                    .border(1.dp, neonCyan.copy(alpha = 0.3f), CutCornerShape(20.dp))
                    .background(panelBg)
            ) {
                GForceGrid(x = gForceX, y = gForceY, color = neonCyan)
                
                // Decorative corners
                Box(Modifier.size(10.dp).background(neonCyan).align(Alignment.TopStart))
                Box(Modifier.size(10.dp).background(neonCyan).align(Alignment.BottomEnd))
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TechReadout(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            color = Color(0xFF00E5FF),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CyberGauges(speed: Float, maxSpeed: Float, color: Color) {
    Canvas(modifier = Modifier.size(300.dp)) {
        val cx = size.width / 2
        val cy = size.height / 2
        val radius = size.width / 2 - 20f

        // 1. Background Track (Dashed)
        drawArc(
            color = Color.DarkGray.copy(alpha = 0.3f),
            startAngle = 135f,
            sweepAngle = 270f,
            useCenter = false,
            style = Stroke(width = 40f, cap = StrokeCap.Butt)
        )
        
        // 2. Ticks
        for (i in 0..40) {
            val angle = 135f + (270f * (i / 40f))
            val rad = Math.toRadians(angle.toDouble())
            val startR = radius + 20f
            val endR = radius + if (i % 5 == 0) 35f else 20f
            
            val start = Offset(
                x = cx + cos(rad).toFloat() * startR,
                y = cy + sin(rad).toFloat() * startR
            )
            val end = Offset(
                x = cx + cos(rad).toFloat() * endR,
                y = cy + sin(rad).toFloat() * endR
            )
            val isActive = (i / 40f) <= (speed / maxSpeed)
            
            drawLine(
                color = if (isActive) color else Color.DarkGray,
                start = start,
                end = end,
                strokeWidth = if (i % 5 == 0) 4f else 2f
            )
        }

        // 3. Active Arc (Glitchy effect)
        val progress = (speed / maxSpeed).coerceIn(0f, 1f)
        val sweep = 270f * progress
        
        drawArc(
            brush = Brush.sweepGradient(
                listOf(Color.Transparent, color, color),
                center = Offset(cx, cy)
            ),
            startAngle = 135f,
            sweepAngle = sweep,
            useCenter = false,
            style = Stroke(width = 15f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun GForceGrid(x: Float, y: Float, color: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val cx = size.width / 2
        val cy = size.height / 2
        val fullR = size.width / 2

        // Grid Lines
        val step = size.width / 8
        for (i in 1..7) {
            drawLine(
                color = Color.White.copy(alpha = 0.05f),
                start = Offset(i * step, 0f),
                end = Offset(i * step, size.height),
                strokeWidth = 1f
            )
            drawLine(
                color = Color.White.copy(alpha = 0.05f),
                start = Offset(0f, i * step),
                end = Offset(size.width, i * step),
                strokeWidth = 1f
            )
        }

        // Concentric Circles
        drawCircle(color = color.copy(alpha = 0.1f), radius = fullR * 0.33f, style = Stroke(2f))
        drawCircle(color = color.copy(alpha = 0.1f), radius = fullR * 0.66f, style = Stroke(2f))
        
        // Crosshairs
        drawLine(color = color.copy(alpha = 0.3f), start = Offset(cx, 0f), end = Offset(cx, size.height), strokeWidth = 2f)
        drawLine(color = color.copy(alpha = 0.3f), start = Offset(0f, cy), end = Offset(size.width, cy), strokeWidth = 2f)

        // The 'Dot' (Target)
        val targetX = (cx + (x * 120)).coerceIn(0f, size.width)
        val targetY = (cy + (y * 120)).coerceIn(0f, size.height)

        drawLine(
            color = color,
            start = Offset(cx, cy),
            end = Offset(targetX, targetY),
            strokeWidth = 2f
        )
        
        drawCircle(
            color = color,
            radius = 6.dp.toPx(),
            center = Offset(targetX, targetY)
        )
        drawCircle(
            color = Color.White,
            radius = 2.dp.toPx(),
            center = Offset(targetX, targetY)
        )
    }
}
