package com.example.eventconnect.ui.payment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentHistoryScreen(
    viewModel: PaymentHistoryViewModel = viewModel()
) {

    val payments by viewModel.payments.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPayments()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Payment History") })
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            when {
                loading -> CircularProgressIndicator()

                error != null ->
                    Text(error ?: "", color = MaterialTheme.colorScheme.error)

                payments.isEmpty() ->
                    Text("No payments yet")

                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(payments) { payment ->
                        PaymentCard(payment)
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentCard(payment: com.example.eventconnect.data.network.PaymentHistoryResponse) {

    val formattedDate = payment.paid_at?.let {
        try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            formatter.format(parser.parse(it)!!)
        } catch (e: Exception) {
            it
        }
    } ?: "N/A"

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(
                text = "Booking #${payment.booking_id}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(4.dp))

            Text("₹${payment.amount}")
            Text("${payment.card_brand?.uppercase()} •••• ${payment.card_last4}")
            Text("Paid on $formattedDate")
        }
    }
}