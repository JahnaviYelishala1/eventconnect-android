package com.example.eventconnect.ui.menu

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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

    val backgroundColor = Color(0xFFF8FAFC)
    val primaryPurple = Color(0xFF6C3EF4)
    val secondaryPurple = Color(0xFF7C3AED)
    val purpleGradient = Brush.horizontalGradient(listOf(secondaryPurple, primaryPurple))

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Select Menu Items",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                    )
                },
                actions = {
                    Button(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .height(40.dp),
                        enabled = selectedItems.isNotEmpty()
                                && isWithinBudget
                                && selectedDate != null
                                && !bookingLoading,
                        onClick = {
                            if (selectedItems.isEmpty()) return@Button
                            val perItemQty = maxOf(1, attendees / selectedItems.size)
                            val bookingItems = selectedItems.map {
                                BookingItemRequest(menu_id = it.id, quantity = perItemQty)
                            }
                            val request = BookingCreateRequest(
                                event_id = eventId,
                                caterer_id = catererId,
                                attendees = attendees,
                                booking_date = selectedDate!!,
                                items = bookingItems
                            )
                            viewModel.sendBookingRequest(request)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .background(
                                    if (selectedItems.isNotEmpty() && isWithinBudget && selectedDate != null)
                                        purpleGradient else Brush.linearGradient(listOf(Color.LightGray, Color.Gray))
                                )
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (bookingLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                            } else {
                                Text("Send Request", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ModernDateCard(
                    selectedDate = selectedDate,
                    onClick = { showDatePicker = true }
                )
            }

            when {
                loading -> item {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = primaryPurple)
                    }
                }
                error != null -> item {
                    Text(error ?: "Error", color = MaterialTheme.colorScheme.error)
                }
                filteredMenu.isEmpty() -> item {
                    Text("No $selectedFoodType items available.", color = Color(0xFF6B7280))
                }
                else -> {
                    val groupedMenu = filteredMenu.groupBy { it.category }
                    groupedMenu.forEach { (category, items) ->
                        item {
                            Column {
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = Color(0xFF111827)
                                    )
                                )
                                Spacer(Modifier.height(4.dp))
                                Divider(color = Color(0xFFE2E8F0), thickness = 1.dp)
                            }
                        }

                        items(items) { item ->
                            val isSelected = selectedItems.contains(item)
                            MenuItemHorizontalCard(
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
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        selectedDate = sdf.format(Date(millis))
                    }
                    showDatePicker = false
                }) {
                    Text("OK", color = primaryPurple, fontWeight = FontWeight.Bold)
                }
            },
            colors = DatePickerDefaults.colors(containerColor = Color.White)
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    todayContentColor = primaryPurple,
                    selectedDayContainerColor = primaryPurple,
                    selectedDayContentColor = Color.White
                )
            )
        }
    }

    if (bookingSuccess) {
        BookingSuccessDialog(
            onDismiss = {
                viewModel.resetBookingState()
                navController.popBackStack()
            },
            primaryPurple = primaryPurple,
            gradientStart = secondaryPurple
        )
    }

    if (bookingError != null) {
        AlertDialog(
            onDismissRequest = { viewModel.resetBookingState() },
            confirmButton = {
                Button(onClick = { viewModel.resetBookingState() }, colors = ButtonDefaults.buttonColors(containerColor = primaryPurple)) {
                    Text("OK")
                }
            },
            title = { Text("Error", fontWeight = FontWeight.Bold) },
            text = { Text(bookingError ?: "") },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showBudgetDialog) {
        AlertDialog(
            onDismissRequest = { showBudgetDialog = false },
            confirmButton = {
                Button(onClick = { showBudgetDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = primaryPurple)) {
                    Text("OK")
                }
            },
            title = { Text("Budget Exceeded", fontWeight = FontWeight.Bold) },
            text = { Text("Adding \"$exceededItemName\" exceeds your price range.") },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun BookingSuccessDialog(
    onDismiss: () -> Unit,
    primaryPurple: Color,
    gradientStart: Color
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .shadow(elevation = 24.dp, shape = RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Success Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color(0xFFDCFCE7), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = Color(0xFF22C55E),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = "Booking Sent",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827),
                        fontSize = 20.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                Text(
                    text = "Your booking request has been sent successfully.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF374151),
                        fontSize = 16.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // OK Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(48.dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp), spotColor = primaryPurple.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(listOf(gradientStart, primaryPurple))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "OK",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModernDateCard(selectedDate: String?, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        color = Color.White,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFFF5F3FF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF6C3EF4), modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text("Event Date", color = Color(0xFF6B7280), style = MaterialTheme.typography.labelMedium)
                Text(
                    selectedDate ?: "Select Date",
                    color = Color(0xFF111827),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF94A3B8))
        }
    }
}

@Composable
fun MenuItemHorizontalCard(
    item: MenuResponse,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    val elevation by animateDpAsState(if (isSelected) 8.dp else 4.dp, label = "elevation")
    val buttonColor by animateColorAsState(if (isSelected) Color(0xFF22C55E) else Color(0xFF6C3EF4), label = "buttonColor")

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation, RoundedCornerShape(20.dp)),
        color = Color.White,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Food Image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF1F5F9))
            ) {
                if (item.image_url != null) {
                    Image(
                        painter = rememberAsyncImagePainter(item.image_url),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        Icons.Default.Restaurant,
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.Center),
                        tint = Color(0xFFCBD5E1)
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            // Text Info
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        item.item_name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(if (item.food_type == "Veg") "🟢" else "🔴", fontSize = 12.sp)
                }
                
                Text(
                    "₹${item.price}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4B5563)
                    )
                )

                Spacer(Modifier.height(4.dp))
                
                Surface(
                    color = (if (item.food_type == "Veg") Color(0xFFDCFCE7) else Color(0xFFFEE2E2)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = item.food_type,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = (if (item.food_type == "Veg") Color(0xFF166534) else Color(0xFF991B1B)),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Add Button
            Button(
                onClick = onToggle,
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.height(36.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                if (isSelected) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Added", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                } else {
                    Text("Add", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}
