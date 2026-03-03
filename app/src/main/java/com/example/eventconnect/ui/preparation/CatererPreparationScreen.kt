package com.example.eventconnect.ui.preparation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

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

    val stages = listOf(
        "ingredients_ready",
        "cooking_started",
        "packing",
        "out_for_delivery",
        "arrived"
    )

    val currentIndex = stages.indexOf(status).coerceAtLeast(0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update Preparation Status") }
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
                "Current Stage:",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = status.replace("_", " ").uppercase(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(24.dp))

            stages.forEachIndexed { index, stage ->

                val completed = index < currentIndex
                val active = index == currentIndex
                val nextStep = index == currentIndex + 1

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
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
                            contentDescription = null
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {

                        Text(
                            text = stage.replace("_", " ").uppercase(),
                            style = MaterialTheme.typography.bodyLarge
                        )

                        if (active) {
                            Text(
                                "Currently active",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // ✅ Only allow next stage update
                    if (nextStep) {
                        Button(
                            onClick = {
                                viewModel.updateStatus(stage)
                            }
                        ) {
                            Text("Mark")
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
            }
        }
    }
}