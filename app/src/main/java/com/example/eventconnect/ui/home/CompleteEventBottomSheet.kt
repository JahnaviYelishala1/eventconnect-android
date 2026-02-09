package com.example.eventconnect.ui.home

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.eventconnect.data.network.SurplusLocationRequest
import com.example.eventconnect.utils.fetchCurrentLocation

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CompleteEventBottomSheet(
    onDismiss: () -> Unit,
    onSubmit: (Double, Double, SurplusLocationRequest?) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // 🔑 Detect if keyboard (IME) is open
    val isKeyboardOpen = WindowInsets.isImeVisible

    // 🔑 IMPORTANT: consume BACK only when keyboard is open
    BackHandler(enabled = isKeyboardOpen) {
        focusManager.clearFocus() // just hide keyboard
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val scrollState = rememberScrollState()

    var prepared by remember { mutableStateOf("") }
    var consumed by remember { mutableStateOf("") }

    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("") }

    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }
    var locating by remember { mutableStateOf(false) }

    val preparedValue = prepared.toDoubleOrNull() ?: 0.0
    val consumedValue = consumed.toDoubleOrNull() ?: 0.0
    val surplusExists = preparedValue > consumedValue

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions.values.any { it }
            if (granted) {
                locating = true
                fetchCurrentLocation(
                    context,
                    onSuccess = { lat, lng ->
                        latitude = lat
                        longitude = lng
                        locating = false
                        locationError = null
                    },
                    onFailure = {
                        locating = false
                        locationError = "Unable to fetch location. Try again."
                    }
                )
            } else {
                locating = false
                locationError = "Location permission denied"
            }
        }

    ModalBottomSheet(
        onDismissRequest = onDismiss, // ✅ works only when keyboard closed
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .imePadding()                 // keyboard-aware layout
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Text(
                text = "Complete Event",
                style = MaterialTheme.typography.headlineSmall
            )

            OutlinedTextField(
                value = prepared,
                onValueChange = { prepared = it },
                label = { Text("Food Prepared (kg)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = consumed,
                onValueChange = { consumed = it },
                label = { Text("Food Consumed (kg)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            if (surplusExists) {
                Divider()

                Text(
                    text = "Pickup location",
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Building / Street") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                OutlinedTextField(
                    value = pincode,
                    onValueChange = { pincode = it },
                    label = { Text("Pincode") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
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
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !locating
                ) {
                    Icon(Icons.Default.Place, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (locating) "Fetching location..." else "Use current location")
                }

                if (latitude != null && longitude != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        shape = MaterialTheme.shapes.large,
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        OpenStreetMapView(
                            context = context,
                            latitude = latitude!!,
                            longitude = longitude!!
                        )
                    }
                }

                locationError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }

            Button(
                onClick = {
                    val location =
                        if (surplusExists) {
                            SurplusLocationRequest(
                                address = address,
                                city = city,
                                pincode = pincode,
                                latitude = latitude,
                                longitude = longitude,
                                location_type = "Home"
                            )
                        } else null

                    onSubmit(preparedValue, consumedValue, location)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
