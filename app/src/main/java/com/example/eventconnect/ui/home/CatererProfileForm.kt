package com.example.eventconnect.ui.home

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.eventconnect.data.network.CatererCreateRequest
import com.example.eventconnect.data.network.CatererProfileResponse
import com.example.eventconnect.utils.fetchCurrentLocation

// Design Color Palette
private val CreamBG = Color(0xFFF5EFE7)
private val SageGreen = Color(0xFF7A9B8E)
private val DarkText = Color(0xFF2C3E3F)

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

    val availableServices = listOf(
        "Corporate",
        "Birthday",
        "House Party",
        "Bachelor Party"
    )

    val selectedServices = remember {
        mutableStateListOf<String>().apply {
            existing?.services?.let { addAll(it) }
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
            .background(CreamBG)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // Header
        Text(
            if (existing == null) "Create Caterer Profile"
            else "Edit Caterer Profile",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            ),
            modifier = Modifier.padding(top = 8.dp).align(Alignment.Start)
        )

        // ============ PROFILE IMAGE ============ 
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(2.dp, SageGreen.copy(alpha = 0.2f), CircleShape)
                .clickable { imagePicker.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            val painter = rememberAsyncImagePainter(
                model = selectedImageUri ?: imageUrl ?: "https://via.placeholder.com/400"
            )
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Camera Icon Badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(44.dp)
                    .background(SageGreen, CircleShape)
                    .border(3.dp, CreamBG, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // ============ BUSINESS INFO SECTION ============ 
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Business Information",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DarkText
                    )
                )

                val textFieldColors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = SageGreen.copy(alpha = 0.3f),
                    focusedBorderColor = SageGreen,
                    focusedTextColor = DarkText, // Darker text color
                    unfocusedTextColor = DarkText // Darker text color
                )

                // Business Name 
                OutlinedTextField(
                    value = businessName,
                    onValueChange = { businessName = it },
                    label = { Text("Business Name", color = SageGreen) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors
                )

                // City
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City", color = SageGreen) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors
                )

                // Price 
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price per plate (₹)", color = SageGreen) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors
                )

                // Capacity Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = minCap,
                        onValueChange = { minCap = it },
                        label = { Text("Min Capacity", color = SageGreen) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors
                    )

                    OutlinedTextField(
                        value = maxCap,
                        onValueChange = { maxCap = it },
                        label = { Text("Max Capacity", color = SageGreen) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors
                    )
                }
            }
        }

        // ============ LOCATION SECTION ============ 
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Location",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = DarkText
                        )
                    )
                    Spacer(Modifier.weight(1f))
                    if (latitude != null && longitude != null) {
                        Text("Location Set ✓", color = SageGreen, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SageGreen)
                ) {
                    Text("Use Current Location", color = Color.White)
                }
            }
        }

        // ============ SERVICES & FOOD TYPE ============ 
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Services & Food Type",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DarkText
                    )
                )
                // Food Type Chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(selected = vegSupported, onClick = { vegSupported = !vegSupported }, label = { Text("Veg") })
                    FilterChip(selected = nonVegSupported, onClick = { nonVegSupported = !nonVegSupported }, label = { Text("Non-Veg") })
                }
                // Services Chips
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    availableServices.chunked(2).forEach { rowServices ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowServices.forEach { service ->
                                FilterChip(
                                    selected = selectedServices.contains(service),
                                    onClick = {
                                        if (selectedServices.contains(service)) {
                                            selectedServices.remove(service)
                                        } else {
                                            selectedServices.add(service)
                                        }
                                    },
                                    label = { Text(service) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // ============ SUBMIT BUTTON ============ 
        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 16.dp))
        }

        Button(
            onClick = {
                // Validation Logic
                if (businessName.isBlank() || city.isBlank() || price.isBlank() || minCap.isBlank() || maxCap.isBlank() || latitude == null || longitude == null) {
                    errorMessage = "Please fill all fields and set a location."
                    return@Button
                }
                if (!vegSupported && !nonVegSupported) {
                    errorMessage = "Please select at least one food type."
                    return@Button
                }
                errorMessage = null

                onSubmit(
                    CatererCreateRequest(
                        business_name = businessName,
                        city = city,
                        price_per_plate = price.toDoubleOrNull() ?: 0.0,
                        min_capacity = minCap.toIntOrNull() ?: 0,
                        max_capacity = maxCap.toIntOrNull() ?: 0,
                        veg_supported = vegSupported,
                        nonveg_supported = nonVegSupported,
                        latitude = latitude!!,
                        longitude = longitude!!,
                        services = selectedServices,
                        image_url = imageUrl ?: ""
                    ),
                    selectedImageUri
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SageGreen)
        ) {
            Text("Save Profile", color = Color.White, fontSize = 16.sp)
        }

        Spacer(Modifier.height(30.dp))
    }
}
