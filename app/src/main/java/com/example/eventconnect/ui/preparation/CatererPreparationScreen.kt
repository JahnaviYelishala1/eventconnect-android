package com.example.eventconnect.ui.preparation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val LightBackground = Color(0xFFF9FAFB)
private val PrimaryText = Color(0xFF1A1C1E)
private val SecondaryText = Color(0xFF6B7280)
private val PurpleAccent = Color(0xFF6C3EF4)
private val PurpleGradientEnd = Color(0xFFA78BFA)
private val SuccessGreen = Color(0xFF22C55E)
private val BorderGray = Color(0xFFD1D5DB)
private val HighlightPurple = Color(0xFFF3E8FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatererPreparationScreen(
    bookingId: Int
) {
    val viewModel: PreparationViewModel = viewModel {
        PreparationViewModel(bookingId)
    }

    val status by viewModel.status.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadStatus()
    }

    val displayStages = listOf(
        "Ingredients Ready",
        "Cooking Started",
        "Packing",
        "Out for Delivery",
        "Arrived"
    )

    val internalStages = listOf(
        "ingredients_ready",
        "cooking_started",
        "packing",
        "out_for_delivery",
        "arrived"
    )

    val currentIndex = internalStages.indexOf(status).coerceAtLeast(0)

    Scaffold(
        containerColor = LightBackground,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightBackground,
                    titleContentColor = PrimaryText
                ),
                title = {
                    Text(
                        "Update Preparation Status",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            
            // Current Stage Section
            Text(
                "Current Stage:",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = SecondaryText,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = status.replace("_", " ").uppercase(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PurpleAccent
                )
            )

            Spacer(Modifier.height(32.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                itemsIndexed(displayStages) { index, stageName ->
                    val internalStatus = internalStages[index]
                    val isCompleted = index < currentIndex
                    val isActive = index == currentIndex
                    val isNext = index == currentIndex + 1
                    val isLast = index == displayStages.size - 1

                    PreparationStepRow(
                        stageName = stageName,
                        isCompleted = isCompleted,
                        isActive = isActive,
                        isNext = isNext,
                        isLast = isLast,
                        onMarkClick = {
                            viewModel.updateStatus(internalStatus)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PreparationStepRow(
    stageName: String,
    isCompleted: Boolean,
    isActive: Boolean,
    isNext: Boolean,
    isLast: Boolean,
    onMarkClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Timeline Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
            // Circle Indicator
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .then(
                        if (isCompleted || isActive) {
                            Modifier.background(
                                color = if (isCompleted) SuccessGreen else PurpleAccent,
                                shape = CircleShape
                            )
                        } else {
                            Modifier.border(2.dp, BorderGray, CircleShape)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            // Vertical Line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(if (isCompleted) SuccessGreen else BorderGray)
                )
            }
        }

        Spacer(Modifier.width(16.dp))

        // Right Side Content
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 24.dp)
                .then(
                    if (isActive) {
                        Modifier
                            .shadow(4.dp, RoundedCornerShape(16.dp))
                            .background(HighlightPurple, RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    } else {
                        Modifier.padding(vertical = 4.dp)
                    }
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stageName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                    )
                    if (isActive) {
                        Text(
                            "Currently active",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SecondaryText
                            )
                        )
                    }
                }

                if (isNext) {
                    Button(
                        onClick = onMarkClick,
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(),
                        modifier = Modifier
                            .height(36.dp)
                            .width(80.dp)
                            .shadow(2.dp, RoundedCornerShape(24.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(PurpleAccent, PurpleGradientEnd)
                                ),
                                shape = RoundedCornerShape(24.dp)
                            )
                    ) {
                        Text(
                            "Mark",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}
