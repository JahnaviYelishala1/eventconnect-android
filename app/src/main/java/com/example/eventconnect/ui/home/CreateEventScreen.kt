package com.example.eventconnect.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    navController: NavController,
    viewModel: CreateEventViewModel = viewModel()
) {

    var eventName by remember { mutableStateOf("") }
    var guests by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }

    val eventTypes = listOf("Wedding", "Corporate", "Birthday", "Festival")
    val mealStyles = listOf("Buffet", "Packed Meal", "Snacks Only")
    val locationTypes = listOf("Indoor Hall", "Outdoor", "Home")
    val seasons = listOf("Summer", "Winter", "Monsoon")

    var selectedEventType by remember { mutableStateOf("") }
    var selectedMealStyle by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf("") }
    var selectedSeason by remember { mutableStateOf("") }

    val error by viewModel.error.collectAsState()
    val prediction by viewModel.prediction.collectAsState()
    val loading by viewModel.loading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Event") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ---------------- EVENT DETAILS CARD ----------------
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        "Event Details",
                        style = MaterialTheme.typography.titleMedium
                    )

                    OutlinedTextField(
                        value = eventName,
                        onValueChange = { eventName = it },
                        label = { Text("Event Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    DropdownField("Event Type", eventTypes, selectedEventType) {
                        selectedEventType = it
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = guests,
                            onValueChange = { guests = it },
                            label = { Text("Guests") },
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = duration,
                            onValueChange = { duration = it },
                            label = { Text("Hours") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    DropdownField("Meal Style", mealStyles, selectedMealStyle) {
                        selectedMealStyle = it
                    }

                    DropdownField("Location Type", locationTypes, selectedLocation) {
                        selectedLocation = it
                    }

                    DropdownField("Season", seasons, selectedSeason) {
                        selectedSeason = it
                    }
                }
            }

            // ---------------- ESTIMATE BUTTON ----------------
            Button(
                onClick = {
                    viewModel.estimateFood(
                        selectedEventType,
                        guests,
                        duration,
                        selectedMealStyle,
                        selectedLocation,
                        selectedSeason
                    )
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Estimating…")
                } else {
                    Text("Estimate Food")
                }
            }

            // ---------------- RESULT CARD ----------------
            prediction?.let {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Prediction", style = MaterialTheme.typography.titleMedium)
                        Text(
                            it,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }

                Button(
                    onClick = {
                        viewModel.saveEvent(
                            eventName = eventName,
                            eventType = selectedEventType,
                            guests = guests.toInt(),
                            duration = duration.toInt(),
                            mealStyle = selectedMealStyle,
                            location = selectedLocation,
                            season = selectedSeason
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Event")
                }
            }

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
