package com.example.eventconnect.ui.booking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.eventconnect.data.network.BookingResponse
import com.example.eventconnect.data.network.PaymentResponse
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ExpandableBookingCard(
    booking: BookingResponse,
    showActions: Boolean,
    onAccept: (() -> Unit)? = null,
    onReject: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onPay: (() -> Unit)? = null,
    paymentDetails: PaymentResponse? = null
) {
    var expanded by remember { mutableStateOf(false) }

    val status = booking.status.lowercase(Locale.getDefault())

    val statusColor = when (status) {
        "pending" -> MaterialTheme.colorScheme.secondary
        "accepted" -> MaterialTheme.colorScheme.primary
        "paid" -> MaterialTheme.colorScheme.tertiary
        "rejected" -> MaterialTheme.colorScheme.error
        "cancelled" -> MaterialTheme.colorScheme.error
        "completed" -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.secondary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ----------------------------
            // Basic Booking Info
            // ----------------------------
            Text(
                text = booking.event_name ?: "Event",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(4.dp))

            Text("Caterer: ${booking.caterer_name ?: "N/A"}")
            Text("Guests: ${booking.attendees ?: 0}")

            val formattedDate = booking.booking_date?.let {
                try {
                    val parser =
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val formatter =
                        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    val date = parser.parse(it)
                    if (date != null) formatter.format(date) else it
                } catch (e: Exception) {
                    it
                }
            } ?: "N/A"

            Text("Date: $formattedDate")
            Text("Total: ₹${booking.total_price}")

            Spacer(Modifier.height(6.dp))

            AssistChip(
                onClick = {},
                label = { Text(status.uppercase(Locale.getDefault())) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = statusColor.copy(alpha = 0.2f)
                )
            )

            // ============================================================
            // ORGANIZER SIDE (showActions = false)
            // ============================================================
            if (!showActions) {

                val canCancel =
                    status == "pending" || status == "accepted"

                Spacer(Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                    // Pay Button
                    if (status == "accepted") {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onPay?.invoke() }
                        ) {
                            Text("Pay Now")
                        }
                    }

                    // Cancel Button
                    if (canCancel) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            ),
                            onClick = { onCancel?.invoke() }
                        ) {
                            Text("Cancel Request")
                        }
                    }

                    // Detailed Paid Info
                    if (status == "paid" && paymentDetails != null) {

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "Payment Completed ✅",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )

                        Text(
                            text = "₹${paymentDetails.amount} paid",
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = "${paymentDetails.card_brand?.uppercase()} •••• ${paymentDetails.card_last4}",
                            style = MaterialTheme.typography.bodySmall
                        )

                        val paidDate = try {
                            val parser = SimpleDateFormat(
                                "yyyy-MM-dd'T'HH:mm:ss",
                                Locale.getDefault()
                            )
                            val formatter = SimpleDateFormat(
                                "dd MMM yyyy, hh:mm a",
                                Locale.getDefault()
                            )
                            formatter.format(parser.parse(paymentDetails.paid_at)!!)
                        } catch (e: Exception) {
                            paymentDetails.paid_at
                        }

                        Text(
                            text = "Paid on $paidDate",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Fallback simple paid display if details not fetched yet
                    if (status == "paid" && paymentDetails == null) {
                        Text(
                            text = "Payment Completed ✅",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            // ============================================================
            // CATERER SIDE (showActions = true)
            // ============================================================
            if (showActions) {

                Spacer(Modifier.height(12.dp))

                when (status) {
                    "pending" -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(onClick = { onAccept?.invoke() }) {
                                Text("Accept")
                            }

                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                ),
                                onClick = { onReject?.invoke() }
                            ) {
                                Text("Reject")
                            }
                        }
                    }

                    "accepted" -> {
                        Text(
                            text = "Awaiting Payment from Organizer",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    "paid" -> {
                        Text(
                            text = "Payment Received ✅",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            // ============================================================
            // Expandable Items
            // ============================================================
            if (expanded) {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))

                booking.items.forEach { item ->
                    Text(
                        text = "${item.item_name} x${item.quantity} - ₹${item.price}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}