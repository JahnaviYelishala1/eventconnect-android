package com.example.eventconnect.ui.booking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CatererBookingsScreen(
    viewModel: CatererBookingsViewModel = viewModel()
) {

    val bookings by viewModel.bookings.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadBookings()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        bookings.forEach { booking ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {

                Column(Modifier.padding(16.dp)) {

                    Text("Booking ID: ${booking.id}")
                    Text("₹${booking.total_price}")
                    Text("Status: ${booking.status}")

                    Spacer(Modifier.height(8.dp))

                    if (booking.status == "pending") {

                        Row {

                            Button(
                                onClick = {
                                    viewModel.updateStatus(
                                        booking.id,
                                        "accepted"
                                    )
                                }
                            ) {
                                Text("Accept")
                            }

                            Spacer(Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    viewModel.updateStatus(
                                        booking.id,
                                        "rejected"
                                    )
                                }
                            ) {
                                Text("Reject")
                            }
                        }
                    }
                }
            }
        }
    }
}