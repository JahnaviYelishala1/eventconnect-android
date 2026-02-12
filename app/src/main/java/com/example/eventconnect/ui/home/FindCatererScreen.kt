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

    LaunchedEffect(Unit) {
        viewModel.loadCaterers(eventId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Find Caterers") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        when {
            loading -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                }
            }

            caterers.isEmpty() -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No caterers found")
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(caterers) { caterer ->
                        CatererCard(
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


@Composable
fun CatererCard(
    caterer: CatererResponse,
    onBookClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                caterer.business_name,
                style = MaterialTheme.typography.titleLarge
            )

            Text("City: ${caterer.city}")
            Text("Price: ₹${caterer.price_per_plate} per plate")
            Text("Rating: ⭐ ${caterer.rating}")

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onBookClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Book Caterer")
            }
        }
    }
}
