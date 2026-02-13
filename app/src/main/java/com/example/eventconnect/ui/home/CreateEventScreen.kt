package com.example.eventconnect.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.net.URLEncoder
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    navController: NavController,
    viewModel: CreateEventViewModel = viewModel()
) {

    val coroutineScope = rememberCoroutineScope()

    var eventName by remember { mutableStateOf("") }
    var guests by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("") }

    var locationQuery by remember { mutableStateOf("") }
    var selectedLatLng by remember { mutableStateOf<GeoPoint?>(null) }

    val eventTypes = listOf("Corporate", "Birthday", "Conference")
    val mealStyles = listOf("Buffet", "Packed Meal", "Snacks")
    val locationTypes = listOf("Indoor", "Outdoor", "Home")
    val seasons = listOf("Summer", "Winter", "Monsoon")

    var selectedEventType by remember { mutableStateOf(eventTypes[0]) }
    var selectedMealStyle by remember { mutableStateOf(mealStyles[0]) }
    var selectedLocationType by remember { mutableStateOf(locationTypes[0]) }
    var selectedSeason by remember { mutableStateOf(seasons[0]) }

    val error by viewModel.error.collectAsState()
    val prediction by viewModel.prediction.collectAsState()
    val loading by viewModel.loading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Event") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = eventName,
                onValueChange = { eventName = it },
                label = { Text("Event Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = guests,
                onValueChange = { guests = it },
                label = { Text("Guests") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("Duration (Hours)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = locationQuery,
                onValueChange = { locationQuery = it },
                label = { Text("Search Location") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = pincode,
                onValueChange = { pincode = it },
                label = { Text("Pincode") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    coroutineScope.launch {
                        val result = searchLocation(locationQuery)
                        if (result != null) {
                            selectedLatLng = result
                        } else {
                            viewModel.setError("Location not found")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Search on Map")
            }

            selectedLatLng?.let { geoPoint ->
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    factory = { context ->
                        Configuration.getInstance().load(
                            context,
                            context.getSharedPreferences("osm", 0)
                        )
                        Configuration.getInstance().userAgentValue =
                            context.packageName

                        MapView(context).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            controller.setZoom(15.0)
                            controller.setCenter(geoPoint)
                        }
                    },
                    update = { map ->
                        map.overlays.clear()
                        val marker = Marker(map)
                        marker.position = geoPoint
                        marker.setAnchor(
                            Marker.ANCHOR_CENTER,
                            Marker.ANCHOR_BOTTOM
                        )
                        map.overlays.add(marker)
                        map.invalidate()
                    }
                )
            }

            Button(
                onClick = {

                    if (
                        eventName.isBlank() ||
                        guests.isBlank() ||
                        duration.isBlank() ||
                        city.isBlank() ||
                        pincode.isBlank() ||
                        selectedLatLng == null
                    ) {
                        viewModel.setError("Please fill all fields")
                        return@Button
                    }

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
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading
            ) {
                Text("Save Event")
            }

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            prediction?.let {
                Text(it)
            }
        }
    }
}

suspend fun searchLocation(query: String): GeoPoint? {
    return withContext(Dispatchers.IO) {
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val url = URL(
                "https://nominatim.openstreetmap.org/search?q=$encodedQuery&format=json&limit=1"
            )

            val connection = url.openConnection()
            connection.setRequestProperty("User-Agent", "EventConnectApp")

            val response = connection.getInputStream()
                .bufferedReader()
                .readText()

            val jsonArray = JSONArray(response)

            if (jsonArray.length() > 0) {
                val obj = jsonArray.getJSONObject(0)
                val lat = obj.getDouble("lat")
                val lon = obj.getDouble("lon")
                GeoPoint(lat, lon)
            } else null

        } catch (e: Exception) {
            null
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onSelected(it)
                        expanded = false
                    }
                )
            }
        }
    }
}


