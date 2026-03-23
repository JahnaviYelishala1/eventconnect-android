package com.example.eventconnect.ui.preparation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

@Composable
fun PreparationStatusScreen(
    bookingId: Int
) {
    val viewModel: PreparationViewModel = viewModel {
        PreparationViewModel(bookingId)
    }

    val status by viewModel.status.collectAsState()

    // Real-time auto refresh every 5 seconds
    LaunchedEffect(Unit) {
        while (true) {
            viewModel.loadStatus()
            delay(5000)
        }
    }

    val stages = listOf(
        Triple("Ingredients Ready", Icons.Default.Kitchen, "ingredients_ready"),
        Triple("Cooking Started", Icons.Default.Whatshot, "cooking_started"),
        Triple("Packing", Icons.Default.Inventory2, "packing"),
        Triple("Out for Delivery", Icons.Default.LocalShipping, "out_for_delivery"),
        Triple("Arrived", Icons.Default.CheckCircle, "arrived")
    )

    val currentIndex = stages.indexOfFirst { it.third == status }.coerceAtLeast(0)
    val progress = (currentIndex + 1f) / stages.size
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

    // Color Palette
    val backgroundColor = Color(0xFFF9FAFB)
    val primaryText = Color(0xFF1A1C1E)
    val secondaryText = Color(0xFF6B7280)
    val purpleAccent = Color(0xFF6C3EF4)
    val purpleLight = Color(0xFFA78BFA)
    val greenCompleted = Color(0xFF22C55E)
    val inactiveGray = Color(0xFFD1D5DB)

    Scaffold(
        containerColor = backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // TOP SECTION
            Column {
                Text(
                    text = "Preparation Tracking",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryText,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Track your order progress in real-time",
                    fontSize = 14.sp,
                    color = secondaryText,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // PROGRESS SECTION
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.05f))
                    .clip(RoundedCornerShape(20.dp)),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Overall Progress",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryText
                        )
                        Text(
                            text = "${(animatedProgress * 100).toInt()}%",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = purpleAccent
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Custom Rounded Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(Color(0xFFE5E7EB), CircleShape)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress)
                                .fillMaxHeight()
                                .background(
                                    brush = Brush.horizontalGradient(listOf(purpleAccent, purpleLight)),
                                    shape = CircleShape
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Estimated completion: 25 mins",
                        fontSize = 12.sp,
                        color = secondaryText
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // STEP TRACKER (Vertical Stepper)
            Column(modifier = Modifier.fillMaxWidth()) {
                stages.forEachIndexed { index, (label, icon, _) ->
                    val isCompleted = index < currentIndex
                    val isCurrent = index == currentIndex
                    val isUpcoming = index > currentIndex

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = if (index == stages.size - 1) 0.dp else 12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Left: Indicator and Line
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(40.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        color = when {
                                            isCompleted -> greenCompleted.copy(alpha = 0.15f)
                                            isCurrent -> purpleAccent.copy(alpha = 0.15f)
                                            else -> Color.Transparent
                                        },
                                        shape = CircleShape
                                    )
                                    .then(
                                        if (isUpcoming) Modifier.background(Color.White, CircleShape).shadow(1.dp, CircleShape)
                                        else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                when {
                                    isCompleted -> Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = greenCompleted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    isCurrent -> Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(purpleAccent, CircleShape)
                                            .shadow(4.dp, CircleShape, ambientColor = purpleAccent)
                                    )
                                    else -> Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(inactiveGray.copy(alpha = 0.4f), CircleShape)
                                    )
                                }
                            }

                            if (index < stages.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(48.dp)
                                        .background(
                                            if (isCompleted) greenCompleted else inactiveGray.copy(alpha = 0.3f)
                                        )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Right: Content
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .then(
                                    if (isCurrent) Modifier.shadow(8.dp, RoundedCornerShape(16.dp), spotColor = purpleAccent.copy(alpha = 0.2f))
                                    else Modifier
                                ),
                            color = if (isCurrent) Color.White else Color.Transparent,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(if (isCurrent) 16.dp else 0.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            if (isCurrent) purpleAccent.copy(alpha = 0.1f) else Color.Transparent,
                                            RoundedCornerShape(10.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        icon,
                                        contentDescription = null,
                                        tint = when {
                                            isCompleted -> greenCompleted
                                            isCurrent -> purpleAccent
                                            else -> inactiveGray
                                        },
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                    Text(
                                        text = label,
                                        fontSize = 16.sp,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isUpcoming) inactiveGray else primaryText
                                    )
                                    if (isCurrent) {
                                        Text(
                                            text = "Currently in progress...",
                                            fontSize = 12.sp,
                                            color = secondaryText,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
