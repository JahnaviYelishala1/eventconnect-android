package com.example.eventconnect.ui.home

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    val primaryPurple = Color(0xFF6C3EF4)
    val secondaryPurple = Color(0xFFA78BFA)
    val titleColor = Color(0xFF1A1C1E)
    val bodyColor = Color(0xFF4B5563)
    val inputLabelColor = Color(0xFF374151)
    val inputTextAlpha = 1f
    val inputBg = Color(0xFFF9FAFB)
    val borderColor = Color(0xFFE5E7EB)

    val isKeyboardOpen = WindowInsets.isImeVisible

    BackHandler(enabled = isKeyboardOpen) {
        focusManager.clearFocus()
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
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = Color(0xFFE5E7EB))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column {
                Text(
                    text = "Complete Event",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = titleColor,
                        fontSize = 26.sp
                    )
                )
                Text(
                    text = "Provide final food quantities to close this event.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = bodyColor,
                        fontSize = 15.sp
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ModernTextField(
                    value = prepared,
                    onValueChange = { prepared = it },
                    label = "Food Prepared (kg)",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    labelColor = inputLabelColor,
                    backgroundColor = inputBg,
                    borderColor = borderColor,
                    activeColor = primaryPurple
                )

                ModernTextField(
                    value = consumed,
                    onValueChange = { consumed = it },
                    label = "Food Consumed (kg)",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    labelColor = inputLabelColor,
                    backgroundColor = inputBg,
                    borderColor = borderColor,
                    activeColor = primaryPurple
                )
            }

            if (surplusExists) {
                Surface(
                    color = primaryPurple.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Surplus Detected!",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = primaryPurple
                            )
                        )
                        Text(
                            text = "There is a surplus of ${String.format("%.2f", preparedValue - consumedValue)} kg. Please provide a pickup location.",
                            style = MaterialTheme.typography.bodySmall.copy(color = bodyColor)
                        )
                    }
                }

                Text(
                    text = "Pickup Location",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = titleColor,
                        fontSize = 20.sp
                    )
                )

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    ModernTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = "Building / Street",
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        labelColor = inputLabelColor,
                        backgroundColor = inputBg,
                        borderColor = borderColor,
                        activeColor = primaryPurple
                    )

                    ModernTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = "City",
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        labelColor = inputLabelColor,
                        backgroundColor = inputBg,
                        borderColor = borderColor,
                        activeColor = primaryPurple
                    )

                    ModernTextField(
                        value = pincode,
                        onValueChange = { pincode = it },
                        label = "Pincode",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        labelColor = inputLabelColor,
                        backgroundColor = inputBg,
                        borderColor = borderColor,
                        activeColor = primaryPurple
                    )
                }

                OutlinedButton(
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
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryPurple),
                    border = androidx.compose.foundation.BorderStroke(1.dp, primaryPurple.copy(alpha = 0.3f)),
                    enabled = !locating
                ) {
                    if (locating) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = primaryPurple)
                    } else {
                        Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text("Use current location", fontWeight = FontWeight.Bold)
                    }
                }

                if (latitude != null && longitude != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFF3F4F6))
                    ) {
                        OpenStreetMapView(
                            context = context,
                            latitude = latitude!!,
                            longitude = longitude!!
                        )
                    }
                }

                locationError?.let {
                    Text(
                        it, 
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Cancel", color = Color(0xFF6B7280), fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        val location = if (surplusExists) {
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
                    modifier = Modifier
                        .weight(1.5f)
                        .height(54.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = primaryPurple.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(listOf(primaryPurple, secondaryPurple))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Submit Report", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    labelColor: Color,
    backgroundColor: Color,
    borderColor: Color,
    activeColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = labelColor
            ),
            modifier = Modifier.padding(start = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = activeColor,
                unfocusedBorderColor = borderColor,
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                cursorColor = activeColor,
                focusedLabelColor = activeColor,
                unfocusedLabelColor = labelColor.copy(alpha = 0.6f)
            ),
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = Color(0xFF111827),
                fontWeight = FontWeight.Medium
            )
        )
    }
}
