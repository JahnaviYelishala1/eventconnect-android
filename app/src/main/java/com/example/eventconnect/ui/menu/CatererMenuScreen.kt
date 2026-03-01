package com.example.eventconnect.ui.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.eventconnect.data.network.MenuResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatererMenuScreen(
    eventId: Int,
    catererId: Int,
    attendees: Int,
    selectedFoodType: String,
    navController: NavController,
    viewModel: MenuViewModel = viewModel()
) {

    val menu by viewModel.menu.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    val selectedItems = remember { mutableStateListOf<MenuResponse>() }

    LaunchedEffect(Unit) {
        viewModel.loadMenu(catererId)
    }

    /* ---------------- FILTER BASED ON FOOD TYPE ---------------- */

    val filteredMenu = when (selectedFoodType) {
        "Veg" -> menu.filter { it.food_type == "Veg" }
        "Non-Veg" -> menu.filter { it.food_type == "Non-Veg" }
        else -> menu
    }

    val totalPerPlate = selectedItems.sumOf { it.price }
    val grandTotal = totalPerPlate * attendees

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text(
            text = "Select Menu Items",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(16.dp))

        when {
            loading -> {
                CircularProgressIndicator()
            }

            error != null -> {
                Text(
                    text = error ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }

            filteredMenu.isEmpty() -> {
                Text(
                    text = "No $selectedFoodType items available."
                )
            }

            else -> {

                val categoryOrder = listOf(
                    "Starter",
                    "Soup",
                    "Main Course",
                    "Breads",
                    "Rice",
                    "Dessert",
                    "Beverage"
                )

                val groupedMenu = filteredMenu.groupBy { it.category }

                categoryOrder.forEach { category ->

                    val items = groupedMenu[category] ?: return@forEach

                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.height(8.dp))

                    items.forEach { item ->

                        val isSelected = selectedItems.contains(item)

                        MenuItemCard(
                            item = item,
                            isSelected = isSelected,
                            onToggle = {
                                if (isSelected) {
                                    selectedItems.remove(item)
                                } else {
                                    selectedItems.add(item)
                                }
                            }
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                }

                Spacer(Modifier.height(24.dp))
                Divider()
                Spacer(Modifier.height(12.dp))

                Text("Guests: $attendees")
                Text("Per Plate Total: ₹$totalPerPlate")

                Text(
                    text = "Grand Total: ₹$grandTotal",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        navController.navigate(
                            "confirm_booking/$eventId/$catererId"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedItems.isNotEmpty()
                ) {
                    Text("Request Booking")
                }
            }
        }
    }
}

@Composable
private fun MenuItemCard(
    item: MenuResponse,
    isSelected: Boolean,
    onToggle: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {

        Column(
            Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = item.item_name,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = if (item.food_type == "Veg") "🟢" else "🔴"
                )
            }

            Spacer(Modifier.height(4.dp))

            Text("₹${item.price}")

            Spacer(Modifier.height(6.dp))

            item.image_url?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )
                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick = onToggle,
                modifier = Modifier.fillMaxWidth(),
                colors = if (isSelected)
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                else
                    ButtonDefaults.buttonColors()
            ) {
                Text(
                    text = if (isSelected) "Remove" else "Add"
                )
            }
        }
    }
}