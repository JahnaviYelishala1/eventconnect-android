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
import com.example.eventconnect.data.network.EventBookingStatusResponse
import com.example.eventconnect.data.network.EventResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyEventsScreen(
    navController: NavController,
    viewModel: MyEventsViewModel = viewModel()
) {

    val events by viewModel.events.collectAsState()
    val error by viewModel.error.collectAsState()
    val bookingInfo by viewModel.bookingInfo.collectAsState()

    var showCompleteSheet by remember { mutableStateOf(false) }
    var selectedEventId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadEvents()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Events") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, null)
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
                    Text(error!!,
                        color = MaterialTheme.colorScheme.error)
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
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    items(events) { event ->

                        EventCard(
                            navController = navController,
                            event = event,
                            bookingInfo =
                                bookingInfo[event.id],
                            onCompleteClick = {
                                selectedEventId = event.id
                                showCompleteSheet = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showCompleteSheet && selectedEventId != null) {
        CompleteEventBottomSheet(
            onDismiss = { showCompleteSheet = false },
            onSubmit = { prepared, consumed, location ->
                viewModel.completeEvent(
                    selectedEventId!!,
                    prepared,
                    consumed,
                    location
                )
                showCompleteSheet = false
            }
        )
    }
}


@Composable
fun EventCard(
    navController: NavController,
    event: EventResponse,
    bookingInfo: EventBookingStatusResponse?,
    onCompleteClick: () -> Unit
) {

    val foodQty =
        "%.1f".format(event.estimated_food_quantity)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            Text(event.event_name,
                style = MaterialTheme.typography.titleLarge)

            AssistChip(
                onClick = {},
                label = { Text(event.status.replace("_", " ")) }
            )

            // 🔥 Show Caterer Name if booked
            bookingInfo?.caterer_name?.let {
                Text("Caterer: $it",
                    style = MaterialTheme.typography.bodyMedium)
            }

            Divider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {
                InfoColumn("Guests",
                    event.attendees.toString())
                InfoColumn("Hours",
                    event.duration_hours.toString())
                InfoColumn("Meal",
                    event.meal_style)
            }

            Surface(
                color =
                    MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.large
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {
                    Text("Estimated Food")
                    Text("$foodQty ${event.unit}")
                }
            }

            when (event.status) {

                "CREATED" -> {
                    Column(
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onCompleteClick,
                            modifier =
                                Modifier.fillMaxWidth()
                        ) {
                            Text("Complete Event")
                        }

                        Button(
                            onClick = {
                                navController.navigate(
                                    "find-caterer/${event.id}"
                                )
                            },
                            modifier =
                                Modifier.fillMaxWidth()
                        ) {
                            Text("Find Caterer")
                        }
                    }
                }

                "BOOKING_REQUESTED" -> {
                    AssistChip(
                        onClick = {},
                        label = {
                            Text("Waiting for Caterer Response")
                        }
                    )
                }

                "BOOKED" -> {
                    Column(
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {
                        AssistChip(
                            onClick = {},
                            label = {
                                Text("Caterer Confirmed")
                            }
                        )

                        Button(
                            onClick = onCompleteClick,
                            modifier =
                                Modifier.fillMaxWidth()
                        ) {
                            Text("Complete Event")
                        }
                    }
                }

                "SURPLUS_AVAILABLE" -> {
                    AssistChip(
                        onClick = {},
                        label = {
                            Text("Surplus Available")
                        }
                    )
                }

                "COMPLETED" -> {
                    AssistChip(
                        onClick = {},
                        label = {
                            Text("Event Completed")
                        }
                    )
                }
            }
        }
    }
}


@Composable
private fun InfoColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}
