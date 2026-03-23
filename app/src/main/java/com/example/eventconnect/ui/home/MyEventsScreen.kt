package com.example.eventconnect.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.eventconnect.data.network.EventResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyEventsScreen(
    navController: NavController,
    viewModel: MyEventsViewModel = viewModel()
) {
    val events by viewModel.events.collectAsState()
    val error by viewModel.error.collectAsState()
    val lavender = Color(0xFFF9F7FF)
    val violet = Color(0xFFF1EAFF)
    
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(lavender, violet)
    )

    var showCompleteDialog by remember { mutableStateOf<EventResponse?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadEvents()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        // Subtle animated glow effects
        AnimatedGlowEffect()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        @Suppress("DEPRECATION")
                        Text(
                            "My Events",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1A1C1E),
                            letterSpacing = (-0.5).sp
                        )
                    },
                    actions = {
                        IconButton(onClick = { /* Filter action */ }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = Color(0xFF6C3EF4)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color(0xFF1A1C1E)
                    )
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when {
                    error != null -> {
                        Column(
                            Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(error!!, color = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadEvents() },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C3EF4))
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                    events.isEmpty() -> {
                        Column(
                            Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.EventBusy,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.Gray.copy(alpha = 0.5f)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "No events created yet",
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 100.dp, start = 20.dp, end = 20.dp, top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            items(events) { event ->
                                EventGlassCard(
                                    event = event,
                                    onCompleteClick = { showCompleteDialog = event },
                                    onFindCatererClick = {
                                        navController.navigate("find-caterer/${event.id}/${event.attendees}")
                                    },
                                    onViewNgoClick = {
                                        navController.navigate("waiting-ngo/${event.surplus_request_id}")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCompleteDialog != null) {
        CompleteEventDialog(
            event = showCompleteDialog!!,
            onDismiss = { showCompleteDialog = null },
            onConfirm = { prepared, consumed ->
                val eventId = showCompleteDialog!!.id
                viewModel.completeEvent(
                    eventId = eventId,
                    foodPrepared = prepared,
                    foodConsumed = consumed,
                    surplusLocation = null
                )
                showCompleteDialog = null
                navController.navigate("send-surplus/$eventId/17.385/78.486")
            }
        )
    }
}

@Composable
fun EventGlassCard(
    event: EventResponse,
    onCompleteClick: () -> Unit,
    onFindCatererClick: () -> Unit,
    onViewNgoClick: () -> Unit
) {
    val normalizedStatus = event.status.uppercase()
    val primaryPurple = Color(0xFF6C3EF4)
    val secondaryViolet = Color(0xFF9F5FFF)
    val darkText = Color(0xFF1A1C1E)
    val darkGray = Color(0xFF4A4A4A)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = primaryPurple.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(24.dp),
        color = Color.White, // Changed from translucent to solid white for better contrast
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.7f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.event_name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkText,
                    modifier = Modifier.weight(1f)
                )

                val (statusText, statusColor) = when (normalizedStatus) {
                    "CREATED" -> "CREATED" to Color(0xFF007AFF)
                    "SURPLUS_AVAILABLE", "COMPLETED" -> "SURPLUS AVAILABLE" to Color(0xFF34C759)
                    else -> normalizedStatus to Color.Gray
                }

                Surface(
                    color = statusColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f), thickness = 0.5.dp)

            // Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem(icon = Icons.Default.Groups, value = event.attendees.toString(), label = "Guests")
                InfoItem(icon = Icons.Default.Schedule, value = "${event.duration_hours ?: 0}h", label = "Duration")
                InfoItem(icon = Icons.Default.Restaurant, value = event.meal_style, label = "Meal Type")
            }

            // Food Estimation Highlight
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF8F9FA), // Lighter background for the estimate box
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp), // Increased vertical padding
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Estimated Food",
                        fontSize = 15.sp,
                        color = darkGray,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${"%.1f".format(event.estimated_food_quantity)} ${event.unit}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = darkText
                    )
                }
            }

            // Action Buttons
            if (normalizedStatus == "CREATED") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCompleteClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, primaryPurple.copy(alpha = 0.3f))
                    ) {
                        Text("Complete", color = primaryPurple, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    Button(
                        onClick = onFindCatererClick,
                        modifier = Modifier
                            .weight(1.2f)
                            .height(52.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp), ambientColor = primaryPurple),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.horizontalGradient(listOf(primaryPurple, secondaryViolet))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Find Caterer", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                        }
                    }
                }
            } else if (event.surplus_request_id != null) {
                Button(
                    onClick = onViewNgoClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp), ambientColor = primaryPurple),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.horizontalGradient(listOf(primaryPurple, secondaryViolet))),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("View NGO / Chat", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                            Spacer(Modifier.width(8.dp))
                            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoItem(icon: ImageVector, value: String, label: String) {
    val darkText = Color(0xFF1A1C1E)
    val darkGray = Color(0xFF4A4A4A)
    
    Column(horizontalAlignment = Alignment.Start) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFF6C3EF4))
            Spacer(Modifier.width(6.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = darkText)
        }
        Text(label, fontSize = 12.sp, color = darkGray, fontWeight = FontWeight.Bold) // Darkened label text
    }
}

private fun Modifier.size(size: Int) = size(size.dp)

@Composable
private fun AnimatedGlowEffect() {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.04f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Canvas(modifier = Modifier
        .fillMaxSize()
        .blur(100.dp)) {
        drawCircle(
            color = Color(0xFF6C3EF4).copy(alpha = alpha),
            radius = 300.dp.toPx(),
            center = Offset(size.width * 0.1f, size.height * 0.2f)
        )
        drawCircle(
            color = Color(0xFF9F5FFF).copy(alpha = alpha * 0.7f),
            radius = 250.dp.toPx(),
            center = Offset(size.width * 0.9f, size.height * 0.8f)
        )
    }
}

@Composable
fun CompleteEventDialog(
    event: EventResponse,
    onDismiss: () -> Unit,
    onConfirm: (Double, Double) -> Unit
) {
    var preparedStr by remember { mutableStateOf(event.estimated_food_quantity.toString()) }
    var consumedStr by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        title = { 
            Text("Complete Event", fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E)) 
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "Please provide the final food quantities for ${event.event_name}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                OutlinedTextField(
                    value = preparedStr,
                    onValueChange = { preparedStr = it },
                    label = { Text("Food Prepared (${event.unit})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = consumedStr,
                    onValueChange = { consumedStr = it },
                    label = { Text("Food Consumed (${event.unit})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val prepared = preparedStr.toDoubleOrNull() ?: 0.0
                    val consumed = consumedStr.toDoubleOrNull() ?: 0.0
                    onConfirm(prepared, consumed)
                },
                enabled = consumedStr.isNotEmpty(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C3EF4))
            ) {
                Text("Confirm", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MyEventsScreenPreview() {
    MyEventsScreen(navController = rememberNavController())
}
