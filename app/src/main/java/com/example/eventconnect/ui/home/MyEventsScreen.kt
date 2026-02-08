package com.example.eventconnect.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eventconnect.data.network.EventResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyEventsScreen(
    navController: NavController,
    viewModel: MyEventsViewModel = viewModel()
) {
    val events by viewModel.events.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadEvents()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Events") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        when {
            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                }
            }

            events.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No events created yet")
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(events) { event ->
                        EventCard(event)
                    }
                }
            }
        }
    }
}

/* -----------------------------------------------------
   🔹 BEAUTIFUL EVENT CARD (FINAL)
----------------------------------------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCard(event: EventResponse) {

    val foodQty = "%.1f".format(event.estimated_food_quantity)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ---------- TITLE ----------
            Text(
                text = event.event_name,
                style = MaterialTheme.typography.titleLarge
            )

            // ---------- TAGS ----------
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = {},
                    label = { Text(event.event_type) }
                )
                AssistChip(
                    onClick = {},
                    label = { Text(event.location_type) }
                )
            }

            Divider()

            // ---------- DETAILS ROW ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoColumn("Guests", event.attendees.toString())
                InfoColumn("Hours", event.duration_hours.toString())
                InfoColumn("Meal", event.meal_style)
            }

            // ---------- FOOD HIGHLIGHT ----------
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Estimated Food",
                        style = MaterialTheme.typography.labelLarge
                    )

                    Text(
                        text = "$foodQty ${event.unit}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }
    }
}


/* -----------------------------------------------------
   🔹 SMALL REUSABLE DETAIL ITEM
----------------------------------------------------- */

@Composable
private fun DetailItem(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun InfoColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
