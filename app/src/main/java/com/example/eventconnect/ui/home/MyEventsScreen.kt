package com.example.eventconnect.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    var showCompleteDialog by remember { mutableStateOf<EventResponse?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadEvents()
    }

    Scaffold(
        containerColor = Color(0xFFF2F2F7),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Events",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1C1C1E)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.shadow(2.dp)
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            when {

                error != null -> {

                    Column(
                        Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(error!!, color = Color.Red)

                        Button(onClick = { viewModel.loadEvents() }) {
                            Text("Retry")
                        }

                    }

                }

                events.isEmpty() -> {

                    Text(
                        "No events created yet",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Gray
                    )

                }

                else -> {

                    LazyColumn(
                        Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        items(events) { event ->

                            EventCard(
                                navController = navController,
                                event = event,
                                onCompleteClick = { showCompleteDialog = event }
                            )

                        }

                    }

                }

            }

        }

    }

    if (showCompleteDialog != null) {

        CompleteEventDialog(

            event = showCompleteDialog!!,

            onDismiss = { showCompleteDialog = null },

            onConfirm = { prepared, consumed ->

                val eventId = showCompleteDialog!!.id

                viewModel.completeEvent(
                    eventId = eventId,
                    foodPrepared = prepared,
                    foodConsumed = consumed,
                    surplusLocation = null
                )

                showCompleteDialog = null

                navController.navigate("send-surplus/$eventId/17.385/78.486")

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

    val normalizedStatus = event.status.uppercase()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {

                Text(
                    text = event.event_name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                val statusColor = when (normalizedStatus) {
                    "CREATED" -> Color(0xFF007AFF)
                    "COMPLETED", "SURPLUS_AVAILABLE" -> Color(0xFF34C759)
                    else -> Color.Gray
                }

                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {

                    Text(
                        text = normalizedStatus.replace("_", " "),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                }

            }

            HorizontalDivider()

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {

                InfoColumn("Guests", event.attendees.toString())
                InfoColumn("Duration", "${event.duration_hours ?: 0}h")
                InfoColumn("Meal", event.meal_style)

            }

            Surface(
                color = Color(0xFFF2F2F7),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                Row(
                    Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text("Estimate Food")

                    Text(
                        "${"%.1f".format(event.estimated_food_quantity)} ${event.unit}",
                        fontWeight = FontWeight.Bold
                    )

                }

            }

            if (normalizedStatus == "CREATED") {

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    OutlinedButton(
                        onClick = onCompleteClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Complete")
                    }

                    Button(
                        onClick = {
                            navController.navigate("find_caterer/${event.id}/${event.attendees}")
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Find Caterer")
                    }

                }

            }

        }

    }

}

@Composable
fun CompleteEventDialog(
    event: EventResponse,
    onDismiss: () -> Unit,
    onConfirm: (Double, Double) -> Unit
) {

    var preparedStr by remember { mutableStateOf(event.estimated_food_quantity.toString()) }
    var consumedStr by remember { mutableStateOf("") }

    AlertDialog(

        onDismissRequest = onDismiss,

        title = { Text("Complete Event") },

        text = {

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                OutlinedTextField(
                    value = preparedStr,
                    onValueChange = { preparedStr = it },
                    label = { Text("Food Prepared (${event.unit})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                OutlinedTextField(
                    value = consumedStr,
                    onValueChange = { consumedStr = it },
                    label = { Text("Food Consumed (${event.unit})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

            }

        },

        confirmButton = {

            Button(
                onClick = {
                    val prepared = preparedStr.toDoubleOrNull() ?: 0.0
                    val consumed = consumedStr.toDoubleOrNull() ?: 0.0
                    onConfirm(prepared, consumed)
                },
                enabled = consumedStr.isNotEmpty()
            ) {
                Text("Confirm")
            }

        },

        dismissButton = {

            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }

        }

    )

}

@Composable
fun InfoColumn(label: String, value: String) {

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(label, fontSize = 12.sp, color = Color.Gray)

        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

    }

}