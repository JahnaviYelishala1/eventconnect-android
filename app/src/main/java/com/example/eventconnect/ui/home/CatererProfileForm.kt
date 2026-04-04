package com.example.eventconnect.ui.home

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.eventconnect.data.network.CatererCreateRequest
import com.example.eventconnect.data.network.CatererProfileResponse
import com.example.eventconnect.utils.fetchCurrentLocation

// Modern Color Palette
private val BackgroundColor = Color(0xFFF8FAFC)
private val CardBackground = Color(0xFFFFFFFF)
private val PrimaryPurple = Color(0xFF6C63FF)
private val GradientStart = Color(0xFF6C63FF)
private val GradientEnd = Color(0xFF8B5CF6)
private val SecondaryAccent = Color(0xFF10B981)
private val RedAccent = Color(0xFFEF4444)
private val TextPrimary = Color(0xFF111827)
private val TextSecondary = Color(0xFF6B7280)
private val FieldBg = Color(0xFFF1F5F9)
private val BorderColor = Color(0xFFE5E7EB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatererProfileForm(
    existing: CatererProfileResponse? = null,
    onSubmit: (CatererCreateRequest, Uri?) -> Unit
) {
    val context = LocalContext.current

    var businessName by remember { mutableStateOf(existing?.business_name ?: "") }
    var city by remember { mutableStateOf(existing?.city ?: "") }
    var price by remember { mutableStateOf(existing?.price_per_plate?.toString() ?: "") }
    var minCap by remember { mutableStateOf(existing?.min_capacity?.toString() ?: "") }
    var maxCap by remember { mutableStateOf(existing?.max_capacity?.toString() ?: "") }

    var imageUrl by remember { mutableStateOf(existing?.image_url) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var vegSupported by remember { mutableStateOf(existing?.veg_supported ?: true) }
    var nonVegSupported by remember { mutableStateOf(existing?.nonveg_supported ?: true) }

    var latitude by remember { mutableStateOf(existing?.latitude) }
    var longitude by remember { mutableStateOf(existing?.longitude) }

    val availableServices = listOf("Corporate", "Birthday", "House Party", "Bachelor Party")

    val selectedServices = remember {
        mutableStateListOf<String>().apply {
            existing?.services?.let { services ->
                addAll(services.map { it.service_type })
            }
        }
    }

    val availableMealStyles = listOf("Buffet", "Live Cooking", "Snacks", "Full Course", "Catering")

    val selectedMealStyles = remember {
        mutableStateListOf<String>().apply {
            existing?.meal_styles?.let { addAll(it) }
        }
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> selectedImageUri = uri }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions.values.any { it }
            if (granted) {
                fetchCurrentLocation(
                    context = context,
                    onSuccess = { lat, lng ->
                        latitude = lat
                        longitude = lng
                        Toast.makeText(context, "Location Updated!", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

    Column(
        Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Title
        Text(
            text = if (existing == null) "Create Caterer Profile" else "Edit Caterer Profile",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            ),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 8.dp)
        )

        // Profile Image Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, BorderColor, CircleShape)
                    .clickable { imagePicker.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                val painter = rememberAsyncImagePainter(
                    model = selectedImageUri ?: imageUrl ?: "https://via.placeholder.com/150"
                )
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .size(32.dp)
                        .background(PrimaryPurple, CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Text(
                "Upload Business Logo",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontWeight = FontWeight.Medium)
            )
        }

        // Business Information Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Business Information",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        fontSize = 18.sp
                    )
                )

                ModernInputField(
                    value = businessName,
                    onValueChange = { businessName = it },
                    label = "Business Name",
                    placeholder = "Enter business name"
                )

                ModernInputField(
                    value = city,
                    onValueChange = { city = it },
                    label = "City",
                    placeholder = "Enter city"
                )

                ModernInputField(
                    value = price,
                    onValueChange = { price = it },
                    label = "Price per plate (₹)",
                    placeholder = "0.00"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ModernInputField(
                        value = minCap,
                        onValueChange = { minCap = it },
                        label = "Min Capacity",
                        placeholder = "0",
                        modifier = Modifier.weight(1f)
                    )
                    ModernInputField(
                        value = maxCap,
                        onValueChange = { maxCap = it },
                        label = "Max Capacity",
                        placeholder = "0",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Location Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Location",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        fontSize = 18.sp
                    )
                )

                Button(
                    onClick = {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.horizontalGradient(listOf(GradientStart, GradientEnd))),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Use Current Location", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                if (latitude != null && longitude != null) {
                    Text("Location Set ✓", color = SecondaryAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }

        // Services & Food Type Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Services & Food Type",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        fontSize = 18.sp
                    )
                )

                // Food Type Chips
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FoodTypeChip(label = "Veg", selected = vegSupported, color = SecondaryAccent) { vegSupported = !vegSupported }
                    FoodTypeChip(label = "Non-Veg", selected = nonVegSupported, color = RedAccent) { nonVegSupported = !nonVegSupported }
                }

                // Service Options - Switched to Column for stability
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableServices.forEach { service ->
                        ModernSelectableChip(
                            label = service,
                            selected = selectedServices.contains(service),
                            onClick = {
                                if (selectedServices.contains(service)) selectedServices.remove(service)
                                else selectedServices.add(service)
                            }
                        )
                    }
                }

                // Meal Styles Section
                Text(
                    "Meal Styles",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        fontSize = 18.sp
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(availableMealStyles) { mealStyle ->
                        ModernSelectableChip(
                            label = mealStyle,
                            selected = selectedMealStyles.contains(mealStyle),
                            onClick = {
                                if (selectedMealStyles.contains(mealStyle)) selectedMealStyles.remove(mealStyle)
                                else selectedMealStyles.add(mealStyle)
                            }
                        )
                    }
                }
            }
        }

        // Error Message
        errorMessage?.let {
            Text(it, color = RedAccent, modifier = Modifier.padding(horizontal = 16.dp), fontSize = 14.sp)
        }

        // Submit Button
        Button(
            onClick = {
                if (businessName.isBlank() || city.isBlank() || price.isBlank() || minCap.isBlank() || maxCap.isBlank() || latitude == null || longitude == null) {
                    errorMessage = "Please fill all fields and set a location."
                    return@Button
                }
                if (!vegSupported && !nonVegSupported) {
                    errorMessage = "Please select at least one food type."
                    return@Button
                }
                errorMessage = null

                val lat = latitude ?: return@Button
                val lng = longitude ?: return@Button

                onSubmit(
                    CatererCreateRequest(
                        business_name = businessName,
                        city = city,
                        price_per_plate = price.toDoubleOrNull() ?: 0.0,
                        min_capacity = minCap.toIntOrNull() ?: 0,
                        max_capacity = maxCap.toIntOrNull() ?: 0,
                        veg_supported = vegSupported,
                        nonveg_supported = nonVegSupported,
                        latitude = lat,
                        longitude = lng,
                        services = selectedServices,
                        meal_styles = selectedMealStyles,
                        image_url = imageUrl ?: ""
                    ),
                    selectedImageUri
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(8.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.horizontalGradient(listOf(GradientStart, GradientEnd))),
                contentAlignment = Alignment.Center
            ) {
                Text("Create Profile", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(30.dp))
    }
}

@Composable
fun ModernInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            placeholder = { Text(placeholder, color = TextSecondary) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = FieldBg,
                unfocusedContainerColor = FieldBg,
                disabledContainerColor = FieldBg,
                focusedIndicatorColor = PrimaryPurple,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
    }
}

@Composable
fun FoodTypeChip(
    label: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable { onClick() }
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, if (selected) color else BorderColor, RoundedCornerShape(24.dp)),
        color = if (selected) color.copy(alpha = 0.1f) else Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (selected) color else Color.Gray)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = label,
                color = if (selected) color else TextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ModernSelectableChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable { onClick() }
            .clip(RoundedCornerShape(12.dp)),
        color = if (selected) PrimaryPurple else FieldBg
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            color = if (selected) Color.White else Color(0xFF374151),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}
