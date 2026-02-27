package com.example.eventconnect.ui.menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eventconnect.data.network.BookingItemRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatererMenuScreen(
    eventId: Int,
    catererId: Int,
    navController: NavController,
    viewModel: MenuViewModel = viewModel()
) {

    val menu by viewModel.menu.collectAsState()
    val loading by viewModel.loading.collectAsState()

    val selectedItems = remember { mutableStateListOf<BookingItemRequest>() }

    LaunchedEffect(Unit) {
        viewModel.loadMenu(catererId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text("Select Menu Items", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(16.dp))

        if (loading) {
            CircularProgressIndicator()
        } else {

            menu.forEach { item ->

                var quantity by remember { mutableStateOf(0) }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {

                        Text(item.item_name)
                        Spacer(Modifier.height(4.dp))
                        Text("₹${item.price}")

                        Spacer(Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Button(
                                onClick = { if (quantity > 0) quantity-- }
                            ) { Text("-") }

                            Text(
                                text = "  $quantity  ",
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Button(
                                onClick = { quantity++ }
                            ) { Text("+") }
                        }

                        LaunchedEffect(quantity) {
                            selectedItems.removeAll { it.menu_id == item.id }

                            if (quantity > 0) {
                                selectedItems.add(
                                    BookingItemRequest(
                                        menu_id = item.id,
                                        quantity = quantity
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    // Navigate to confirmation screen
                    navController.navigate("confirm_booking/$eventId/$catererId")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedItems.isNotEmpty()
            ) {
                Text("Request Booking")
            }
        }
    }
}