package com.example.eventconnect.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.eventconnect.data.network.CatererResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatererMatchScreen(
    eventId: Int,
    viewModel: CatererMatchViewModel = viewModel()
) {

    val caterers by viewModel.caterers.collectAsState()
    val loading by viewModel.loading.collectAsState()

    var sortBy by remember { mutableStateOf("distance") }
    var vegOnly by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadMatches(eventId)
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {

        // 🔹 FILTER ROW
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            FilterChip(
                selected = vegOnly,
                onClick = {
                    vegOnly = !vegOnly
                    viewModel.loadMatches(
                        eventId,
                        vegOnly = vegOnly,
                        sortBy = sortBy
                    )
                },
                label = { Text("Veg Only") }
            )

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                TextField(
                    value = sortBy,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Sort By") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        text = { Text("Distance") },
                        onClick = {
                            sortBy = "distance"
                            expanded = false
                            viewModel.loadMatches(eventId, vegOnly = vegOnly, sortBy = sortBy)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Price") },
                        onClick = {
                            sortBy = "price"
                            expanded = false
                            viewModel.loadMatches(eventId, vegOnly = vegOnly, sortBy = sortBy)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Rating") },
                        onClick = {
                            sortBy = "rating"
                            expanded = false
                            viewModel.loadMatches(eventId, vegOnly = vegOnly, sortBy = sortBy)
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (loading) {
            CircularProgressIndicator()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(caterers) { caterer ->
                    CatererCard(caterer)
                }
            }
        }
    }
}

@Composable
fun CatererCard(caterer: CatererResponse) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column {

            Image(
                painter = rememberAsyncImagePainter(
                    caterer.image_url ?: "https://via.placeholder.com/400"
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Column(Modifier.padding(12.dp)) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        caterer.business_name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.weight(1f))

                    if (caterer.rating >= 4.5) {
                        Text(
                            "🌟 Featured",
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text("₹${caterer.price_per_plate}/plate")
                Text("Capacity: ${caterer.min_capacity} - ${caterer.max_capacity}")
                Text("⭐ ${caterer.rating}")

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { /* Book */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Book Now")
                }
            }
        }
    }
}

@Composable
fun RateCatererSheet(
    catererId: Int,
    onSubmit: () -> Unit
) {

    var rating by remember { mutableStateOf(4.0f) }
    var comment by remember { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {

        Text("Rate this Caterer")

        Slider(
            value = rating,
            onValueChange = { rating = it },
            valueRange = 1f..5f,
            steps = 3
        )

        OutlinedTextField(
            value = comment,
            onValueChange = { comment = it },
            label = { Text("Comment") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { onSubmit() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Review")
        }
    }
}
