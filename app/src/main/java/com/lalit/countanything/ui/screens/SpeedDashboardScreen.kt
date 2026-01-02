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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission") // We check manually
@Composable
fun SpeedDashboardScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var speedKmH by remember { mutableStateOf(0f) }
    var gForceX by remember { mutableStateOf(0f) } // Left/Right
    var gForceY by remember { mutableStateOf(0f) } // Acceleration/Braking
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

    // --- SENSORS & LOCATION ---
    DisposableEffect(hasPermission) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        
        // Location Listener
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (location.hasSpeed()) {
                    speedKmH = location.speed * 3.6f // m/s to km/h
                }
            }
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }

        // Sensor Listener (Accelerometer)
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
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                500L, 
                0f,
                locationListener
            )
        }

        val accel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) 
            ?: sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        
        sensorManager.registerListener(
            sensorListener,
            accel,
            SensorManager.SENSOR_DELAY_UI
        )

        onDispose {
            locationManager.removeUpdates(locationListener)
            sensorManager.unregisterListener(sensorListener)
        }
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Use a Dark Theme wrapper explicitly for the Dashboard vibe, or just use colors
    // Let's stick to the Cyberpunk Dark look but use M3 components
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("SPEED & MOTION", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color(0xFF00E5FF),
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.5f))

            // Speedometer Card
            SpeedometerGauge(speed = speedKmH, maxSpeed = 120f)
            
            Spacer(modifier = Modifier.height(32.dp))

            // G-Force Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                shape = CircleShape,
                modifier = Modifier.size(200.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    GForceBubble(x = gForceX, y = gForceY)
                    
                    Text(
                        "G-FORCE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = 24.dp)
                    )
                }
            }
             Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun SpeedometerGauge(speed: Float, maxSpeed: Float) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(300.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Background Arc
            drawArc(
                color = Color.DarkGray.copy(alpha = 0.3f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round)
            )

            // Progress Arc
            val progress = (speed / maxSpeed).coerceIn(0f, 1f)
            val sweep = 270f * progress

            drawArc(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFF00E5FF), Color(0xFF2979FF), Color(0xFFFF1744)) 
                ),
                startAngle = 135f,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = String.format("%.0f", speed),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 96.sp,
                    letterSpacing = (-2).sp
                ),
                color = Color.White
            )
            Text(
                text = "KM/H",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF00E5FF)
            )
        }
    }
}

@Composable
fun GForceBubble(x: Float, y: Float) {
    val maxG = 1.0f
    val animatedX by animateFloatAsState(targetValue = x)
    val animatedY by animateFloatAsState(targetValue = y)

    Canvas(modifier = Modifier.fillMaxSize().padding(32.dp)) {
        // Crosshairs
        drawLine(
            Color.Gray.copy(alpha = 0.2f),
            start = Offset(size.width / 2, 0f),
            end = Offset(size.width / 2, size.height),
            strokeWidth = 2.dp.toPx()
        )
        drawLine(
            Color.Gray.copy(alpha = 0.2f),
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = 2.dp.toPx()
        )
        
        // Center Marker
        drawCircle(Color.Gray.copy(alpha = 0.2f), radius = 4.dp.toPx())
        
        // Bubble
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.width / 2
        val xOffset = (animatedX / maxG) * radius
        val yOffset = (animatedY / maxG) * radius
        
        val bubbleCenter = center + Offset(xOffset, yOffset)

        drawCircle(
            color = Color(0xFFFF1744),
            radius = 16.dp.toPx(),
            center = bubbleCenter
        )
    }
}
