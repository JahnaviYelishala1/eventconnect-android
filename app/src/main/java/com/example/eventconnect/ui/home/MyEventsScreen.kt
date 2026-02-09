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

    var showCompleteSheet by remember { mutableStateOf(false) }
    var selectedEventId by remember { mutableStateOf<Int?>(null) }


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
                        EventCard(
                            event = event,
                            onCompleteClick = {
                                // ✅ Only allow completion for CREATED events
                                if (event.status == "CREATED") {
                                    selectedEventId = event.id
                                    showCompleteSheet = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // ✅ Bottom Sheet
    if (showCompleteSheet && selectedEventId != null) {
        CompleteEventBottomSheet(
            onDismiss = { showCompleteSheet = false },
            onSubmit = { prepared, consumed, location ->
                viewModel.completeEvent(
                    eventId = selectedEventId!!,
                    foodPrepared = prepared,
                    foodConsumed = consumed,
                    surplusLocation = location
                )
                showCompleteSheet = false
            }
        )
    }
}

/* -----------------------------------------------------
   🔹 EVENT CARD (FINAL)
----------------------------------------------------- */

@Composable
fun EventCard(
    event: EventResponse,
    onCompleteClick: () -> Unit
) {
    val foodQty = "%.1f".format(event.estimated_food_quantity)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(event.event_name, style = MaterialTheme.typography.titleLarge)

            // ✅ STATUS CHIP
            AssistChip(
                onClick = {},
                label = { Text(event.status.replace("_", " ")) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = when (event.status) {
                        "SURPLUS_AVAILABLE" ->
                            MaterialTheme.colorScheme.errorContainer
                        "COMPLETED" ->
                            MaterialTheme.colorScheme.primaryContainer
                        else ->
                            MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text(event.event_type) })
                AssistChip(onClick = {}, label = { Text(event.location_type) })
            }

            Divider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoColumn("Guests", event.attendees.toString())
                InfoColumn("Hours", event.duration_hours.toString())
                InfoColumn("Meal", event.meal_style)
            }

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.large
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Estimated Food")
                    Text(
                        "$foodQty ${event.unit}",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }

            // ✅ BUTTON CHANGES BASED ON STATUS
            when (event.status) {

                "CREATED" -> {
                    Button(
                        onClick = onCompleteClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Complete Event")
                    }
                }

                "SURPLUS_AVAILABLE" -> {
                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor =
                                MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text("Surplus Available")
                    }
                }

                "COMPLETED" -> {
                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor =
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text("Completed")
                    }
                }
            }
        }
    }
}

/* -----------------------------------------------------
   🔹 INFO COLUMN
----------------------------------------------------- */

@Composable
private fun InfoColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}
