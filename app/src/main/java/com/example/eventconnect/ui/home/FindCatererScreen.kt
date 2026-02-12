package com.example.eventconnect.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.eventconnect.data.network.CatererResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindCatererScreen(
    navController: NavController,
    eventId: Int,
    viewModel: FindCatererViewModel = viewModel()
) {

    val caterers by viewModel.caterers.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    var showFilter by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadCaterers(eventId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Find Caterers") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                AssistChip(
                    onClick = { showFilter = true },
                    label = { Text("Filters") }
                )

                AssistChip(
                    onClick = {
                        viewModel.loadCaterers(
                            eventId = eventId,
                            sortBy = "price_low"
                        )
                    },
                    label = { Text("Price ↑") }
                )

                AssistChip(
                    onClick = {
                        viewModel.loadCaterers(
                            eventId = eventId,
                            sortBy = "price_high"
                        )
                    },
                    label = { Text("Price ↓") }
                )
            }

            when {

                loading -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                error != null -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            error!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                caterers.isEmpty() -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No caterers found")
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(16.dp)
                    ) {
                        items(caterers) { caterer ->
                            PremiumCatererCard(
                                caterer = caterer,
                                onBookClick = {
                                    viewModel.bookCaterer(
                                        eventId,
                                        caterer.id
                                    ) {
                                        navController.popBackStack()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showFilter) {
        FilterBottomSheet(
            onApply = { min, max, rating ->
                viewModel.loadCaterers(
                    eventId = eventId,
                    minPrice = min,
                    maxPrice = max,
                    minRating = rating
                )
            },
            onDismiss = { showFilter = false }
        )
    }
}

@Composable
fun PremiumCatererCard(
    caterer: CatererResponse,
    onBookClick: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column {

            AsyncImage(
                model = caterer.image_url
                    ?: "https://via.placeholder.com/600x300",
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    caterer.business_name,
                    style = MaterialTheme.typography.titleLarge
                )

                Row(
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("₹${caterer.price_per_plate}/plate")
                    Text("⭐ ${caterer.rating}")
                }

                caterer.distance_km?.let {
                    Text("📍 ${it} km away")
                }

                Button(
                    onClick = onBookClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Book Now")
                }
            }
        }
    }
}
