package com.example.eventconnect.ui.booking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventconnect.data.network.BookingResponse
import com.example.eventconnect.data.network.PaymentResponse
import java.util.*

@Composable
fun ExpandableBookingCard(
    booking: BookingResponse,
    showActions: Boolean,
    onAccept: (() -> Unit)? = null,
    onReject: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onPay: (() -> Unit)? = null,
    paymentDetails: PaymentResponse? = null,
    onRefund: (() -> Unit)? = null,
    onDownloadInvoice: (() -> Unit)? = null,
    onChat: (() -> Unit)? = null,
    onTrackPreparation: (() -> Unit)? = null,
    onPredictFood: (() -> Unit)? = null,
    onAskAi: (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val status = booking.status.lowercase(Locale.getDefault())
    
    val primaryDark = Color(0xFF1A1C1E)
    val secondaryGray = Color(0xFF6B7280)
    val purpleAccent = Color(0xFF6C3EF4)
    val purpleLight = Color(0xFF9F5FFF)
    val greenStatus = Color(0xFF22C55E)
    val redStatus = Color(0xFFEF4444)
    val orangeStatus = Color(0xFFF59E0B)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(20.dp), ambientColor = Color.Black.copy(alpha = 0.05f))
            .clip(RoundedCornerShape(20.dp))
            .clickable { expanded = !expanded },
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // 1. Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = booking.event_name ?: "Event",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryDark
                    )
                    Text(
                        text = "Organizer: ${booking.caterer_name ?: "N/A"}", // Corrected context
                        fontSize = 14.sp,
                        color = secondaryGray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                val (statusText, statusColor) = when (status) {
                    "pending" -> "PENDING" to orangeStatus
                    "accepted" -> "ACCEPTED" to Color(0xFF3B82F6)
                    "paid" -> "PAID" to greenStatus
                    "rejected", "cancelled" -> status.uppercase() to redStatus
                    else -> status.uppercase() to secondaryGray
                }

                Surface(
                    color = statusColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Info Grid Section (2x2 Style)
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    BookingInfoItem("Guests", (booking.attendees ?: 0).toString(), Icons.Default.People, Modifier.weight(1f))
                    BookingInfoItem("Total Price", "₹${booking.total_price}", Icons.Default.Payments, Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    BookingInfoItem("Booking ID", "#${booking.id}", Icons.Default.Badge, Modifier.weight(1f))
                    BookingInfoItem("Date", "Oct 24, 2023", Icons.Default.Event, Modifier.weight(1f)) // Placeholder date
                }
            }

            // 3. Status Highlight for PAID
            if (status == "paid") {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(greenStatus.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = greenStatus, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Payment Confirmed & Secured", color = greenStatus, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            // Expandable Summary Section
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                        .background(Color(0xFFF9FAFB), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text("Order Summary", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = primaryDark)
                    Spacer(modifier = Modifier.height(12.dp))
                    booking.items.forEach { item ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${item.item_name} x${item.quantity}", color = secondaryGray, fontSize = 14.sp)
                            Text("₹${item.price}", fontWeight = FontWeight.SemiBold, color = primaryDark, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. ACTION BUTTONS (Clean Hierarchy)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (showActions) {
                    // Caterer Specific Layout
                    if (status == "pending") {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(Modifier.weight(1f)) {
                                ActionButton(
                                    text = "Accept",
                                    icon = Icons.Default.Check,
                                    containerColor = greenStatus,
                                    onClick = { onAccept?.invoke() }
                                )
                            }
                            Box(Modifier.weight(1f)) {
                                ActionButton(
                                    text = "Reject",
                                    icon = Icons.Default.Close,
                                    containerColor = Color.White,
                                    contentColor = redStatus,
                                    borderColor = redStatus.copy(alpha = 0.3f),
                                    onClick = { onReject?.invoke() }
                                )
                            }
                        }
                    }

                    if (status != "cancelled" && status != "rejected") {
                        // Utility Row
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(Modifier.weight(1f)) {
                                ActionButton(
                                    text = "Chat",
                                    icon = Icons.AutoMirrored.Filled.Chat,
                                    containerColor = Color.White,
                                    contentColor = purpleAccent,
                                    borderColor = purpleAccent.copy(alpha = 0.2f),
                                    onClick = { onChat?.invoke() }
                                )
                            }
                            Box(Modifier.weight(1f)) {
                                ActionButton(
                                    text = "Predict Food",
                                    icon = Icons.Default.AutoGraph,
                                    containerColor = Color.Transparent,
                                    contentColor = Color.White,
                                    isGradient = true,
                                    onClick = { onPredictFood?.invoke() }
                                )
                            }
                        }
                        
                        // Main Action
                        ActionButton(
                            text = "Update Preparation",
                            icon = Icons.Default.OutdoorGrill,
                            containerColor = purpleAccent,
                            onClick = { onTrackPreparation?.invoke() }
                        )
                    }
                } else {
                    // Organizer Specific Layout
                    if (status == "accepted") {
                        ActionButton(text = "Pay Now", containerColor = greenStatus, onClick = { onPay?.invoke() })
                    }
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.weight(1f)) {
                            ActionButton(
                                text = "Chat",
                                icon = Icons.AutoMirrored.Filled.Chat,
                                containerColor = Color.White,
                                contentColor = purpleAccent,
                                borderColor = purpleAccent.copy(alpha = 0.2f),
                                onClick = { onChat?.invoke() }
                            )
                        }
                        Box(Modifier.weight(1f)) {
                            ActionButton(
                                text = "Track",
                                icon = Icons.Default.MyLocation,
                                containerColor = purpleAccent,
                                onClick = { onTrackPreparation?.invoke() }
                            )
                        }
                    }
                    
                    ActionButton(
                        text = "Ask AI Assistant",
                        icon = Icons.Default.AutoAwesome,
                        containerColor = Color.Transparent,
                        isGradient = true,
                        onClick = { onAskAi?.invoke() }
                    )
                }
            }
        }
    }
}

@Composable
fun BookingInfoItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFFF3F4F6), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, fontSize = 11.sp, color = Color(0xFF6B7280), fontWeight = FontWeight.Medium)
            Text(text = value, fontSize = 15.sp, color = Color(0xFF1A1C1E), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    containerColor: Color = Color.Transparent,
    contentColor: Color = Color.White,
    borderColor: Color? = null,
    isGradient: Boolean = false,
    onClick: () -> Unit
)  {
    val gradient = Brush.horizontalGradient(listOf(Color(0xFF6C3EF4), Color(0xFF9F5FFF)))
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .then(
                if (borderColor != null) Modifier.border(1.dp, borderColor, RoundedCornerShape(16.dp)) else Modifier
            ),
        color = if (isGradient) Color.Transparent else containerColor,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(if (isGradient) Modifier.background(gradient) else Modifier),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                }
                Text(text = text, color = contentColor, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}
