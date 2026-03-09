package com.example.eventconnect.ui.booking

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eventconnect.data.network.PaymentResponse
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerBookingsScreen(
    navController: NavController,
    viewModel: OrganizerBookingsViewModel = viewModel()
) {
    val bookings by viewModel.bookings.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    val paymentMap = remember { mutableStateMapOf<Int, PaymentResponse>() }
    var showRefundDialog by remember { mutableStateOf(false) }
    var selectedBookingId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(bookings) {
        bookings.forEach { booking ->
            if (booking.status == "paid" && !paymentMap.containsKey(booking.id)) {
                viewModel.getPaymentDetails(booking.id) { payment ->
                    payment?.let { paymentMap[booking.id] = it }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadBookings()
    }

    if (showRefundDialog && selectedBookingId != null) {
        AlertDialog(
            onDismissRequest = {
                showRefundDialog = false
                selectedBookingId = null
            },
            title = { Text("Confirm Refund") },
            text = { Text("Are you sure you want to refund this payment? This action cannot be undone.") },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    onClick = {
                        viewModel.refundBooking(selectedBookingId!!)
                        showRefundDialog = false
                        selectedBookingId = null
                    }
                ) { Text("Yes, Refund") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRefundDialog = false
                    selectedBookingId = null
                }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA), // Classic light grey background
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Booking Requests",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1D1F)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1D1D1F)
                ),
                modifier = Modifier.shadow(4.dp)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF1D1D1F)
                )
                error != null -> Text(
                    error ?: "Error",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Red
                )
                bookings.isEmpty() -> Text(
                    "No booking requests yet.",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(bookings) { booking ->
                        ExpandableBookingCard(
                            booking = booking,
                            showActions = false,
                            paymentDetails = paymentMap[booking.id],
                            onCancel = { viewModel.cancelBooking(booking.id) },
                            onPay = {
                                viewModel.createPaymentSession(booking.id) { url ->
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                }
                            },
                            onRefund = {
                                selectedBookingId = booking.id
                                showRefundDialog = true
                            },
                            onDownloadInvoice = {
                                viewModel.downloadInvoice(booking.id) { bytes ->
                                    val file = File(context.getExternalFilesDir(null), "invoice_${booking.id}.pdf")
                                    file.writeBytes(bytes)
                                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(uri, "application/pdf")
                                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    }
                                    context.startActivity(intent)
                                }
                            },
                            onChat = { navController.navigate("chat/${booking.id}") },
                            onTrackPreparation = { navController.navigate("preparation/${booking.id}") }
                        )
                    }
                }
            }
        }
    }
}
