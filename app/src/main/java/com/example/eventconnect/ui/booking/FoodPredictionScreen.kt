package com.example.eventconnect.ui.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Scale
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodPredictionScreen(
    bookingId: Int,
    viewModel: FoodPredictionViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val predictions by viewModel.predictions.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    val primaryPurple = Color(0xFF6C3EF4)
    val backgroundColor = Color(0xFFF9FAFB)
    val cardBackground = Color.White
    val titleColor = Color(0xFF1A1C1E)
    val subtitleColor = Color(0xFF6B7280)

    LaunchedEffect(Unit) {
        viewModel.predictFood(bookingId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = titleColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.White, Color(0xFFF5F3FF))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                
                Text(
                    text = "Preparation Plan",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp,
                        color = titleColor
                    )
                )

                Text(
                    text = "AI-powered quantity estimation",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = subtitleColor,
                        fontWeight = FontWeight.Medium
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (!loading && error == null && predictions.isNotEmpty()) {
                    SummarySection(predictions.size, primaryPurple)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = primaryPurple,
                            strokeWidth = 3.dp
                        )
                    } else if (error != null) {
                        ErrorState(error ?: "Unknown Error", primaryPurple) {
                            viewModel.predictFood(bookingId)
                        }
                    } else if (predictions.isEmpty()) {
                        EmptyState(subtitleColor)
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 32.dp)
                        ) {
                            items(predictions.toList()) { (name, quantity) ->
                                FoodPredictionCard(
                                    name = name,
                                    quantity = quantity,
                                    primaryColor = primaryPurple,
                                    cardBackground = cardBackground
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummarySection(itemCount: Int, primaryColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(primaryColor.copy(alpha = 0.05f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Scale,
            contentDescription = null,
            tint = primaryColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "Plan Summary",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = primaryColor,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "$itemCount dishes estimated for preparation",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF6B7280)
                )
            )
        }
    }
}

@Composable
fun FoodPredictionCard(
    name: String,
    quantity: Double,
    primaryColor: Color,
    cardBackground: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.1f),
                spotColor = primaryColor.copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(cardBackground)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(primaryColor.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937),
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = "Recommended amount",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF9CA3AF),
                        fontSize = 13.sp
                    )
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = String.format("%.2f kg", quantity),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryColor,
                    fontSize = 20.sp
                )
            )
            Surface(
                color = primaryColor.copy(alpha = 0.1f),
                shape = CircleShape
            ) {
                Text(
                    text = "Optimal",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = primaryColor,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun ErrorState(error: String, color: Color, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Oops!",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = error,
            color = Color.Gray,
            modifier = Modifier.padding(16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = color),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Try Again")
        }
    }
}

@Composable
fun EmptyState(color: Color) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Restaurant,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = color.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No predictions yet",
            style = MaterialTheme.typography.titleMedium.copy(color = color)
        )
    }
}
