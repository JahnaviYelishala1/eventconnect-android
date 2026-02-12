package com.example.eventconnect.ui.caterer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eventconnect.data.network.CatererProfileRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatererProfileScreen(
    navController: NavController,
    viewModel: CatererProfileViewModel = viewModel()
) {

    var businessName by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var minCapacity by remember { mutableStateOf("") }
    var maxCapacity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }

    var vegSupported by remember { mutableStateOf(true) }
    var nonVegSupported by remember { mutableStateOf(false) }

    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val success by viewModel.success.collectAsState()

    if (success) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Create Caterer Profile") })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = businessName,
                onValueChange = { businessName = it },
                label = { Text("Business Name") }
            )

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City") }
            )

            OutlinedTextField(
                value = minCapacity,
                onValueChange = { minCapacity = it },
                label = { Text("Min Capacity") }
            )

            OutlinedTextField(
                value = maxCapacity,
                onValueChange = { maxCapacity = it },
                label = { Text("Max Capacity") }
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price Per Plate") }
            )

            OutlinedTextField(
                value = latitude,
                onValueChange = { latitude = it },
                label = { Text("Latitude") }
            )

            OutlinedTextField(
                value = longitude,
                onValueChange = { longitude = it },
                label = { Text("Longitude") }
            )

            Row {
                Checkbox(
                    checked = vegSupported,
                    onCheckedChange = { vegSupported = it }
                )
                Text("Veg Supported")
            }

            Row {
                Checkbox(
                    checked = nonVegSupported,
                    onCheckedChange = { nonVegSupported = it }
                )
                Text("Non-Veg Supported")
            }

            Button(
                onClick = {
                    viewModel.createProfile(
                        CatererProfileRequest(
                            business_name = businessName,
                            city = city,
                            min_capacity = minCapacity.toInt(),
                            max_capacity = maxCapacity.toInt(),
                            price_per_plate = price.toDouble(),
                            veg_supported = vegSupported,
                            nonveg_supported = nonVegSupported,
                            latitude = latitude.toDouble(),
                            longitude = longitude.toDouble(),
                            services = listOf("Wedding", "Corporate")
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading
            ) {
                Text("Create Profile")
            }

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
