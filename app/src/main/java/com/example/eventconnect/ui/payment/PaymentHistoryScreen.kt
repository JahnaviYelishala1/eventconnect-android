package com.example.eventconnect.ui.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Payments
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventconnect.data.network.PaymentHistoryResponse
import java.text.SimpleDateFormat
import java.util.*

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

    val backgroundGradient = Brush.verticalGradient(
        listOf(Color.White, Color(0xFFF5F3FF))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // TOP SECTION
            TopSection()

            // TRANSACTION LIST
            Box(modifier = Modifier.weight(1f)) {
                when {
                    loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF6C3EF4))
                        }
                    }
                    error != null -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = error ?: "Unknown error", color = MaterialTheme.colorScheme.error)
                        }
                    }
                    payments.isEmpty() -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No recent transactions",
                                color = Color(0xFF6B7280),
                                fontSize = 16.sp
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(payments) { payment ->
                                PaymentTransactionCard(payment)
                            }
                            item { Spacer(modifier = Modifier.height(20.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Payment History",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1A1C1E)
            )
            Text(
                text = "Your recent transactions",
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )
        }
        
        Surface(
            modifier = Modifier
                .size(44.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .clickable { /* Filter */ },
            color = Color.White
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter",
                    tint = Color(0xFF1A1C1E),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun PaymentTransactionCard(payment: PaymentHistoryResponse) {
    val primaryDark = Color(0xFF1A1C1E)
    val secondaryGray = Color(0xFF6B7280)
    val purpleAccent = Color(0xFF6C3EF4)
    val greenPaid = Color(0xFF22C55E)

    val formattedDate = payment.paid_at?.let {
        try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val formatter = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
            formatter.format(parser.parse(it)!!)
        } catch (e: Exception) {
            it
        }
    } ?: "N/A"

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(20.dp), ambientColor = Color.Black.copy(alpha = 0.05f))
            .clip(RoundedCornerShape(20.dp)),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Top Row: Booking ID and Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(purpleAccent.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = purpleAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Booking #${payment.booking_id}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryDark
                    )
                }
                Text(
                    text = "₹${payment.amount}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryDark
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Middle: Payment Method
            val paymentMethodText = if (payment.card_brand != null && payment.card_last4 != null) {
                "${payment.card_brand.uppercase()} •••• ${payment.card_last4}"
            } else {
                "Cash Payment"
            }
            
            Text(
                text = paymentMethodText,
                fontSize = 14.sp,
                color = secondaryGray,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom: Date and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedDate,
                    fontSize = 12.sp,
                    color = secondaryGray
                )
                
                Surface(
                    color = greenPaid.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = payment.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = greenPaid,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

// Extension to avoid ripple on TopSection button for cleaner look if desired, or use standard
@Composable
fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = this.then(
    Modifier.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
)
