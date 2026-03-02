package com.example.eventconnect.ui.booking

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerBookingsScreen(
    viewModel: OrganizerBookingsViewModel = viewModel()
) {
    val bookings by viewModel.bookings.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadBookings()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Booking Requests") })
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                error != null -> {
                    Text(
                        text = error ?: "Unknown error",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                bookings.isEmpty() -> {
                    Text(
                        "No booking requests yet.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(bookings) { booking ->
                            ExpandableBookingCard(
                                booking = booking,
                                showActions = false,
                                onCancel = {
                                    viewModel.cancelBooking(booking.id)
                                },
                                onPay = {
                                    viewModel.createPaymentSession(booking.id) { url ->
                                        val intent = Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(url)
                                        )
                                        context.startActivity(intent)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}