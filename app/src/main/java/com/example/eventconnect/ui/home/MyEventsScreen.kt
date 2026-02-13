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

    var showCompleteSheet by remember { mutableStateOf(false) }
    var selectedEventId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadEvents()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Events") }
            )
        }
    ) { padding ->

        when {
            error != null -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(error!!,
                        color = MaterialTheme.colorScheme.error)
                }
            }

            events.isEmpty() -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No events created yet")
                }
            }

            else -> {
                LazyColumn(
                    Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(events) { event ->
                        EventCard(
                            navController = navController,
                            event = event,
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
    onCompleteClick: () -> Unit
) {

    val foodQty =
        "%.1f".format(event.estimated_food_quantity)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(event.event_name,
                style = MaterialTheme.typography.titleLarge)

            AssistChip(
                onClick = {},
                label = { Text(event.status.replace("_", " ")) }
            )

            Divider()

            Row(
                Modifier.fillMaxWidth(),
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
                    Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {
                    Text("Estimated Food")
                    Text("$foodQty ${event.unit}")
                }
            }

            if (event.status == "CREATED") {

                Button(
                    onClick = onCompleteClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Complete Event")
                }

                Button(
                    onClick = {
                        navController.navigate(
                            "find_caterer/${event.id}?mealStyle=${event.meal_style}&foodType=Both"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Find Caterer")
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
