package com.example.eventconnect.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eventconnect.ui.theme.AppBackgroundLight
import com.example.eventconnect.ui.theme.AppFieldBackground
import com.example.eventconnect.ui.theme.AppPurpleEnd
import com.example.eventconnect.ui.theme.AppPurpleStart
import com.example.eventconnect.ui.theme.AppSurfaceLight
import com.example.eventconnect.ui.theme.AppTextPrimary
import com.example.eventconnect.ui.theme.AppTextSecondary
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.net.URL
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    navController: NavController,
    viewModel: CreateEventViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    var eventName by remember { mutableStateOf("") }
    var guests by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var locationQuery by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("") }
    var selectedLatLng by remember { mutableStateOf<GeoPoint?>(null) }
    var submitted by remember { mutableStateOf(false) }

    val eventTypes = listOf("Corporate", "Birthday", "Conference")
    val mealStyles = listOf("Buffet", "Packed Meal", "Snacks")
    val locationTypes = listOf("Indoor", "Outdoor", "Home")
    val seasons = listOf("Summer", "Winter", "Monsoon")

    var selectedEventType by remember { mutableStateOf(eventTypes.first()) }
    var selectedMealStyle by remember { mutableStateOf(mealStyles.first()) }
    var selectedLocationType by remember { mutableStateOf(locationTypes.first()) }
    var selectedSeason by remember { mutableStateOf(seasons.first()) }

    val error by viewModel.error.collectAsState(initial = null)
    val prediction by viewModel.prediction.collectAsState(initial = null)
    val loading by viewModel.loading.collectAsState(initial = false)

    val gradient = Brush.horizontalGradient(listOf(AppPurpleStart, AppPurpleEnd))
    val pageBackground = Brush.verticalGradient(listOf(AppBackgroundLight, Color.White))
    val cardShape = RoundedCornerShape(24.dp)
    val fieldShape = RoundedCornerShape(16.dp)

    fun isInvalid(value: String) = submitted && value.isBlank()
    fun formValid() = eventName.isNotBlank() && guests.isNotBlank() && duration.isNotBlank() &&
        locationQuery.isNotBlank() && city.isNotBlank() && pincode.isNotBlank() && selectedLatLng != null

    Scaffold(
        containerColor = AppBackgroundLight,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Create Event",
                            color = AppTextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AppTextPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = AppBackgroundLight)
                )
                HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 1.dp)
            }
        },
        bottomBar = {
            Surface(
                color = AppBackgroundLight,
                shadowElevation = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        submitted = true
                        if (!formValid()) {
                            viewModel.setError("Please fill all fields")
                            return@Button
                        }
                        focusManager.clearFocus()
                        viewModel.saveEvent(
                            eventName = eventName,
                            eventType = selectedEventType,
                            guests = guests.toInt(),
                            duration = duration.toInt(),
                            mealStyle = selectedMealStyle,
                            locationType = selectedLocationType,
                            season = selectedSeason,
                            address = locationQuery,
                            city = city,
                            pincode = pincode,
                            latitude = selectedLatLng!!.latitude,
                            longitude = selectedLatLng!!.longitude
                        )
                    },
                    enabled = !loading,
                    shape = fieldShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp)
                        .background(gradient, fieldShape)
                ) {
                    if (loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text(
                            text = "Create Event",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(pageBackground)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                shape = cardShape,
                colors = CardDefaults.cardColors(containerColor = AppSurfaceLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Text(
                        "Event Details",
                        color = AppTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    ModernFilledField(
                        value = eventName,
                        onValueChange = { eventName = it },
                        label = "Event Name",
                        placeholder = "e.g. Annual Gala",
                        leadingIcon = Icons.Default.Event,
                        isError = isInvalid(eventName)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ModernFilledField(
                            value = guests,
                            onValueChange = { guests = it.filter(Char::isDigit) },
                            label = "Guests",
                            placeholder = "e.g. 50",
                            leadingIcon = Icons.Default.Groups,
                            keyboardType = KeyboardType.Number,
                            isError = isInvalid(guests),
                            modifier = Modifier.weight(1f)
                        )
                        ModernFilledField(
                            value = duration,
                            onValueChange = { duration = it.filter(Char::isDigit) },
                            label = "Duration",
                            placeholder = "Hours",
                            leadingIcon = Icons.Default.AccessTime,
                            keyboardType = KeyboardType.Number,
                            isError = isInvalid(duration),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    ModernFilledField(
                        value = locationQuery,
                        onValueChange = { locationQuery = it },
                        label = "Venue Address",
                        placeholder = "Street, Building, etc.",
                        leadingIcon = Icons.Default.AddLocationAlt
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ModernFilledField(
                            value = city,
                            onValueChange = { city = it },
                            label = "City",
                            placeholder = "City",
                            leadingIcon = Icons.Default.LocationCity,
                            isError = isInvalid(city),
                            modifier = Modifier.weight(1.2f)
                        )
                        ModernFilledField(
                            value = pincode,
                            onValueChange = { pincode = it.filter(Char::isDigit) },
                            label = "Pincode",
                            placeholder = "6 digits",
                            leadingIcon = Icons.Default.PushPin,
                            keyboardType = KeyboardType.Number,
                            isError = isInvalid(pincode),
                            modifier = Modifier.weight(0.8f)
                        )
                    }

                    ElevatedButton(
                        onClick = {
                            coroutineScope.launch {
                                focusManager.clearFocus()
                                val result = searchLocation(locationQuery)
                                if (result != null) {
                                    selectedLatLng = result
                                    viewModel.setError("")
                                } else {
                                    viewModel.setError("Location not found")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = fieldShape,
                        colors = ButtonDefaults.elevatedButtonColors(containerColor = AppFieldBackground)
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp), tint = AppPurpleEnd)
                        Spacer(Modifier.width(8.dp))
                        Text("Verify Location on Map", color = AppTextPrimary, fontWeight = FontWeight.Bold)
                    }

                    DropdownChipGroup("Event Category", eventTypes, selectedEventType) { selectedEventType = it }
                    DropdownChipGroup("Serving Style", mealStyles, selectedMealStyle) { selectedMealStyle = it }
                    DropdownChipGroup("Venue Style", locationTypes, selectedLocationType) { selectedLocationType = it }
                    DropdownChipGroup("Current Season", seasons, selectedSeason) { selectedSeason = it }
                    
                    // Added extra space at bottom of card content to prevent clipping
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            selectedLatLng?.let { point ->
                Card(
                    shape = cardShape,
                    colors = CardDefaults.cardColors(containerColor = AppSurfaceLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Selected Location", color = AppTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        AndroidView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .background(Color.LightGray, RoundedCornerShape(16.dp)),
                            factory = { ctx ->
                                Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osm", 0))
                                Configuration.getInstance().userAgentValue = ctx.packageName
                                MapView(ctx).apply {
                                    setTileSource(TileSourceFactory.MAPNIK)
                                    setMultiTouchControls(true)
                                    controller.setZoom(15.0)
                                    controller.setCenter(point)
                                }
                            },
                            update = { map ->
                                map.overlays.clear()
                                val marker = Marker(map)
                                marker.position = point
                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                map.overlays.add(marker)
                                map.controller.setCenter(point)
                                map.invalidate()
                            }
                        )
                    }
                }
            }

            prediction?.takeIf { it.isNotBlank() }?.let { value ->
                Card(
                    shape = cardShape,
                    colors = CardDefaults.cardColors(containerColor = AppSurfaceLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Food Estimation", color = AppPurpleEnd, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = value, color = AppTextPrimary, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                    }
                }
            }

            error?.takeIf { it.isNotBlank() }?.let { value ->
                Card(
                    shape = cardShape,
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = value,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun ModernFilledField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false
) {
    var isFocused by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) { // correctly uses the passed modifier (e.g. weight)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (isFocused) 4.dp else 0.dp,
                    shape = RoundedCornerShape(14.dp),
                    ambientColor = AppPurpleEnd
                ),
            shape = RoundedCornerShape(14.dp),
            color = AppFieldBackground
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isFocused = it.isFocused },
                placeholder = { Text(label, color = AppTextSecondary, fontSize = 15.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = if (isFocused) AppPurpleEnd else AppTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction = ImeAction.Next
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = AppFieldBackground,
                    unfocusedContainerColor = AppFieldBackground,
                    disabledContainerColor = AppFieldBackground,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = AppPurpleEnd,
                    focusedTextColor = AppTextPrimary,
                    unfocusedTextColor = AppTextPrimary
                ),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )
        }
    }
}

@Composable
private fun DropdownChipGroup(
    title: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = title, color = AppTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = selected == option,
                    onClick = { onSelected(option) },
                    label = { Text(option, fontSize = 13.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AppPurpleEnd.copy(alpha = 0.15f),
                        selectedLabelColor = AppPurpleEnd,
                        containerColor = AppFieldBackground,
                        labelColor = AppTextSecondary
                    ),
                    border = null,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}

suspend fun searchLocation(query: String): GeoPoint? {
    return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        try {
            val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
            val url = URL("https://nominatim.openstreetmap.org/search?q=$encodedQuery&format=json&limit=1")
            val connection = url.openConnection()
            connection.setRequestProperty("User-Agent", "EventConnectApp")
            val response = connection.getInputStream().bufferedReader().readText()
            val jsonArray = org.json.JSONArray(response)

            if (jsonArray.length() > 0) {
                val obj = jsonArray.getJSONObject(0)
                GeoPoint(obj.getDouble("lat"), obj.getDouble("lon"))
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}
