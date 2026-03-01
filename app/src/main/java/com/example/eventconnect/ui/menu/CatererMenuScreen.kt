package com.example.eventconnect.ui.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.eventconnect.data.network.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatererMenuScreen(
    eventId: Int,
    catererId: Int,
    attendees: Int,
    selectedFoodType: String,
    minPrice: Int,
    maxPrice: Int,
    navController: NavController,
    viewModel: MenuViewModel = viewModel()
) {

    val menu by viewModel.menu.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    val bookingLoading by viewModel.bookingLoading.collectAsState()
    val bookingSuccess by viewModel.bookingSuccess.collectAsState()
    val bookingError by viewModel.bookingError.collectAsState()

    var showBudgetDialog by remember { mutableStateOf(false) }
    var exceededItemName by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<String?>(null) }

    val selectedItems = remember { mutableStateListOf<MenuResponse>() }

    LaunchedEffect(Unit) {
        viewModel.loadMenu(catererId)
    }

    val filteredMenu = when (selectedFoodType) {
        "Veg" -> menu.filter { it.food_type == "Veg" }
        "Non-Veg" -> menu.filter { it.food_type == "Non-Veg" }
        else -> menu
    }

    val totalPerPlate = selectedItems.sumOf { it.price }
    val isWithinBudget = totalPerPlate in minPrice.toDouble()..maxPrice.toDouble()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Menu Items") },
                actions = {
                    TextButton(
                        enabled = selectedItems.isNotEmpty()
                                && isWithinBudget
                                && selectedDate != null
                                && !bookingLoading,
                        onClick = {

                            val bookingItems = selectedItems.map {
                                BookingItemRequest(
                                    menu_id = it.id,
                                    quantity = attendees
                                )
                            }

                            val request = BookingCreateRequest(
                                event_id = eventId,
                                caterer_id = catererId,
                                attendees = attendees,
                                booking_date = selectedDate!!,
                                items = bookingItems
                            )

                            viewModel.sendBookingRequest(request)
                        }
                    ) {
                        if (bookingLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Send Request")
                        }
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            /* -------- DATE SELECTION -------- */

            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    selectedDate ?: "Select Event Date"
                )
            }

            Spacer(Modifier.height(16.dp))

            when {
                loading -> CircularProgressIndicator()

                error != null ->
                    Text(error ?: "", color = MaterialTheme.colorScheme.error)

                filteredMenu.isEmpty() ->
                    Text("No $selectedFoodType items available.")

                else -> {

                    val groupedMenu = filteredMenu.groupBy { it.category }

                    groupedMenu.forEach { (category, items) ->

                        Text(
                            category,
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
                                        if (totalPerPlate + item.price <= maxPrice) {
                                            selectedItems.add(item)
                                        } else {
                                            exceededItemName = item.item_name
                                            showBudgetDialog = true
                                        }
                                    }
                                }
                            )
                        }

                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    /* -------- DATE PICKER -------- */

    if (showDatePicker) {

        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        selectedDate = sdf.format(Date(millis))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    /* -------- SUCCESS DIALOG -------- */

    if (bookingSuccess) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                Button(onClick = {
                    viewModel.resetBookingState()
                    navController.popBackStack()
                }) {
                    Text("OK")
                }
            },
            title = { Text("Booking Sent") },
            text = {
                Text("Your booking request has been sent successfully.")
            }
        )
    }

    if (bookingError != null) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                Button(onClick = {
                    viewModel.resetBookingState()
                }) {
                    Text("OK")
                }
            },
            title = { Text("Error") },
            text = { Text(bookingError ?: "") }
        )
    }

    if (showBudgetDialog) {
        AlertDialog(
            onDismissRequest = { showBudgetDialog = false },
            confirmButton = {
                Button(onClick = { showBudgetDialog = false }) {
                    Text("OK")
                }
            },
            title = { Text("Budget Exceeded") },
            text = {
                Text("Adding \"$exceededItemName\" exceeds your price range.")
            }
        )
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
        Column(Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(item.item_name, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.width(8.dp))
                Text(if (item.food_type == "Veg") "🟢" else "🔴")
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
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onToggle,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isSelected) "Remove" else "Add")
            }
        }
    }
}