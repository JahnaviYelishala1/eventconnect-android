package com.example.eventconnect.ui.booking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatererBookingsScreen(
    navController: NavController,
    viewModel: CatererBookingsViewModel = viewModel()
) {

    val bookings by viewModel.bookings.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadBookings()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Booking Requests") })
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            when {
                loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )

                error != null -> Text(
                    error ?: "An unknown error occurred",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error
                )

                bookings.isEmpty() -> Text(
                    "No booking requests",
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(bookings) { booking ->
                        ExpandableBookingCard(
                            booking = booking,
                            showActions = true,
                            onAccept = {
                                viewModel.updateStatus(
                                    booking.id,
                                    "accepted"
                                )
                            },
                            onReject = {
                                viewModel.updateStatus(
                                    booking.id,
                                    "rejected"
                                )
                            },
                            onChat = {
                                navController.navigate("chat/${booking.id}")
                            },
                            onTrackPreparation = {
                                navController.navigate("caterer-preparation/${booking.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}