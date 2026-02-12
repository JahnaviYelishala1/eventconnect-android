package com.example.eventconnect.ui.caterer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatererBookingsScreen(
    navController: NavController,
    viewModel: CatererBookingViewModel = viewModel()
) {

    val bookings by viewModel.bookings.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadBookings()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Requests") }
            )
        }
    ) { padding ->

        when {

            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    Text(error!!)
                }
            }

            bookings.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    Text("No booking requests")
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    items(bookings) { booking ->

                        ProfessionalBookingCard(
                            booking = booking,
                            onAccept = {
                                viewModel.respondBooking(
                                    booking.booking_id,
                                    "ACCEPTED"
                                )
                            },
                            onReject = {
                                viewModel.respondBooking(
                                    booking.booking_id,
                                    "REJECTED"
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfessionalBookingCard(
    booking: com.example.eventconnect.data.network.BookingResponse,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {

    val foodQty =
        "%.1f".format(booking.estimated_food_quantity)

    Card(
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = booking.event_name,
                style = MaterialTheme.typography.titleLarge
            )

            AssistChip(
                onClick = {},
                label = { Text(booking.event_type) }
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guests: ${booking.attendees}")
                Text("Hours: ${booking.duration_hours}")
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Meal: ${booking.meal_style}")
                Text("Location: ${booking.location_type}")
            }

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Estimated Food")
                    Text("$foodQty ${booking.unit}")
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Accept")
                }

                Button(
                    onClick = onReject,
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reject")
                }
            }
        }
    }
}
