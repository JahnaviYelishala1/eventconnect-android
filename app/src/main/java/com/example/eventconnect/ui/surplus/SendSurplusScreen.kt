package com.example.eventconnect.ui.surplus

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendSurplusScreen(
    navController: NavController,
    eventId: Int,
    latitude: Double,
    longitude: Double,
    viewModel: SurplusViewModel = viewModel()
) {

    var description by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    val success by viewModel.success.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Donate Surplus Food") }) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Food Description") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Food Image URL") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.sendAlert(
                        eventId,
                        description,
                        imageUrl,
                        latitude,
                        longitude
                    )
                }
            ) {
                Text("Send Alert to NGOs")
            }

            if (success) {
                LaunchedEffect(true) {
                    navController.navigate("waiting-ngo")
                }
                Text(
                    "Alert sent to nearby NGOs!",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}