package com.example.eventconnect.ui.preparation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreparationStatusScreen(
    bookingId: Int
) {

    val viewModel: PreparationViewModel = viewModel {
        PreparationViewModel(bookingId)
    }

    val status by viewModel.status.collectAsState()

    // ✅ Real-time auto refresh every 5 seconds
    LaunchedEffect(Unit) {
        while (true) {
            viewModel.loadStatus()
            delay(5000)
        }
    }

    val stages = listOf(
        "ingredients_ready",
        "cooking_started",
        "packing",
        "out_for_delivery",
        "arrived"
    )

    val currentIndex = stages.indexOf(status).coerceAtLeast(0)
    val progress = (currentIndex + 1f) / stages.size

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        label = "progress"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preparation Tracking") }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Text(
                "Live Progress",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(16.dp))

            // ✅ Progress Bar
            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(24.dp))

            // ✅ Vertical Stepper
            stages.forEachIndexed { index, stage ->

                val completed = index <= currentIndex
                val active = index == currentIndex

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    if (completed) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {

                        Text(
                            text = stage.replace("_", " ").uppercase(),
                            style = MaterialTheme.typography.bodyLarge,
                            color =
                                if (active)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface
                        )

                        if (active) {
                            Text(
                                text = "Currently in progress...",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
            }
        }
    }
}