package com.example.eventconnect.ui.profile

import android.Manifest
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.eventconnect.data.network.*
import com.example.eventconnect.ui.auth.getFirebaseIdToken
import com.example.eventconnect.utils.fetchCurrentLocation
import com.example.eventconnect.utils.uriToFile
import com.example.eventconnect.ui.home.OpenStreetMapView
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MediaType.Companion.toMediaType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NgoProfileEditScreen(navController: NavController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var establishedYear by remember { mutableStateOf("") }
    var about by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var imageUrl by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }

    var loading by remember { mutableStateOf(true) }
    var saving by remember { mutableStateOf(false) }
    var profileExists by remember { mutableStateOf(false) }

    // Colors
    val backgroundColor = Color(0xFFF8FAFC)
    val cardColor = Color(0xFFFFFFFF)
    val primaryPurple = Color(0xFF6C3EF4)
    val secondaryPurple = Color(0xFF7C3AED)
    val lightPurple = Color(0xFFEDE9FE)
    val darkText = Color(0xFF111827)
    val secondaryText = Color(0xFF6B7280)
    val fieldBackground = Color(0xFFF1F5F9)
    val purpleGradient = Brush.horizontalGradient(listOf(secondaryPurple, primaryPurple))

    // ---------------- IMAGE PICKER ----------------
    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> selectedImageUri = uri }

    // ---------------- LOCATION PERMISSION ----------------
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
                    },
                    onFailure = { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    // ---------------- LOAD EXISTING PROFILE ----------------
    LaunchedEffect(Unit) {
        getFirebaseIdToken(
            onTokenReceived = { token ->
                scope.launch {
                    try {
                        val authHeader = "Bearer $token"
                        val res = RetrofitClient.apiService.getNgoProfile(authHeader)
                        Log.d("NgoProfileEditScreen", "GET /api/ngos/profile code=${res.code()} body=${res.body()}")

                        if (res.isSuccessful && res.body() != null) {
                            profileExists = true
                            val p = res.body()!!
                            name = p.name
                            establishedYear = p.establishedYear ?: ""
                            about = p.about
                            email = p.email
                            phone = p.phone
                            address = p.address
                            latitude = p.latitude
                            longitude = p.longitude
                            imageUrl = p.imageUrl
                        } else if (res.code() == 404) {
                            profileExists = false
                            Log.d("NgoProfileEditScreen", "NGO profile not found (404), opening create profile mode")
                        } else {
                            val errorBody = res.errorBody()?.string().orEmpty()
                            Log.e("NgoProfileEditScreen", "Failed to load NGO profile: code=${res.code()} error=$errorBody")
                        }
                    } catch (e: Exception) {
                        Log.e("NgoProfileEditScreen", "Exception while loading NGO profile", e)
                    } finally {
                        loading = false
                    }
                }
            },
            onError = { loading = false }
        )
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit NGO Profile",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = darkText
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = darkText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->

        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryPurple)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ---------------- PROFILE IMAGE ----------------
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .shadow(elevation = 8.dp, shape = CircleShape)
                        .clickable { imagePicker.launch("image/*") },
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(fieldBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            selectedImageUri != null ->
                                Image(
                                    painter = rememberAsyncImagePainter(selectedImageUri),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            imageUrl != null ->
                                Image(
                                    painter = rememberAsyncImagePainter(imageUrl),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            else ->
                                Icon(
                                    Icons.Default.Business,
                                    contentDescription = null,
                                    modifier = Modifier.size(50.dp),
                                    tint = secondaryText
                                )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(primaryPurple, CircleShape)
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
                Spacer(Modifier.height(8.dp))
                Text(
                    "Upload NGO Logo",
                    style = MaterialTheme.typography.labelMedium.copy(color = secondaryText)
                )
            }

            // ---------------- FORM CONTAINER ----------------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp), ambientColor = Color.LightGray),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ModernTextField(name, { name = it }, "NGO Name", Icons.Default.Business)
                    ModernTextField(establishedYear, { establishedYear = it }, "Established Year", Icons.Default.CalendarToday)
                    ModernTextField(about, { about = it }, "About NGO", Icons.Default.Description, singleLine = false, minLines = 3)
                    ModernTextField(email, { email = it }, "Email", Icons.Default.Email)
                    ModernTextField(phone, { phone = it }, "Phone", Icons.Default.Phone)
                    ModernTextField(address, { address = it }, "Address", Icons.Default.LocationOn)

                    Spacer(Modifier.height(8.dp))

                    // ---------------- LOCATION SECTION ----------------
                    Button(
                        onClick = {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        },
                        modifier = Modifier.height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = lightPurple),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MyLocation, contentDescription = null, tint = primaryPurple, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Use Current Location", color = primaryPurple, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    // ---------------- MAP SECTION ----------------
                    if (latitude != null && longitude != null) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Text(
                                "Location Preview",
                                style = MaterialTheme.typography.labelMedium.copy(color = secondaryText, fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Box {
                                    OpenStreetMapView(
                                        context = context,
                                        latitude = latitude!!,
                                        longitude = longitude!!
                                    )
                                    Icon(
                                        Icons.Default.Place,
                                        contentDescription = null,
                                        tint = Color.Red,
                                        modifier = Modifier.size(32.dp).align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // ---------------- SAVE BUTTON ----------------
            Button(
                enabled = !saving,
                onClick = {
                    saving = true
                    getFirebaseIdToken(
                        onTokenReceived = { token ->
                            scope.launch {
                                try {
                                    val authHeader = "Bearer $token"
                                    var finalImageUrl = imageUrl
                                    if (selectedImageUri != null) {
                                        val file = uriToFile(context, selectedImageUri!!)
                                        val part = MultipartBody.Part.createFormData(
                                            "file",
                                            file.name,
                                            file.asRequestBody("image/*".toMediaType())
                                        )
                                        val imgRes = RetrofitClient.apiService.uploadNgoImage(authHeader, part)
                                        Log.d("NgoProfileEditScreen", "POST /api/ngos/upload-image code=${imgRes.code()} body=${imgRes.body()}")
                                        if (imgRes.isSuccessful) {
                                            finalImageUrl = imgRes.body()?.image_url
                                        } else {
                                            val uploadError = imgRes.errorBody()?.string().orEmpty()
                                            Log.e("NgoProfileEditScreen", "Failed to upload NGO image: code=${imgRes.code()} error=$uploadError")
                                        }
                                    }

                                    val request = NgoProfileRequest(
                                        name, establishedYear, about, email, phone, address,
                                        latitude, longitude, finalImageUrl
                                    )

                                    val saveRes = if (profileExists) {
                                        RetrofitClient.apiService.updateNgoProfile(authHeader, request)
                                    } else {
                                        RetrofitClient.apiService.saveNgoProfile(authHeader, request)
                                    }

                                    val method = if (profileExists) "PUT" else "POST"
                                    Log.d("NgoProfileEditScreen", "$method /api/ngos/profile code=${saveRes.code()} body=${saveRes.body()}")

                                    if (saveRes.isSuccessful) {
                                        profileExists = true
                                        val successMessage = if (method == "PUT") "Profile Updated Successfully" else "Profile Created Successfully"
                                        Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    } else {
                                        val errorBody = saveRes.errorBody()?.string().orEmpty()
                                        Log.e("NgoProfileEditScreen", "Failed to save NGO profile: code=${saveRes.code()} error=$errorBody")
                                        Toast.makeText(context, "Error saving profile", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Log.e("NgoProfileEditScreen", "Exception while saving NGO profile", e)
                                    Toast.makeText(context, "Error updating profile", Toast.LENGTH_SHORT).show()
                                } finally {
                                    saving = false
                                }
                            }
                        },
                        onError = { saving = false }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(purpleGradient),
                    contentAlignment = Alignment.Center
                ) {
                    if (saving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
                    } else {
                        Text(
                            "Save Profile",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF1F5F9),
                unfocusedContainerColor = Color(0xFFF1F5F9),
                disabledContainerColor = Color(0xFFF1F5F9),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color(0xFF111827),
                unfocusedTextColor = Color(0xFF111827)
            ),
            leadingIcon = { Icon(icon, contentDescription = null, tint = Color(0xFF6C3EF4)) },
            singleLine = singleLine,
            minLines = minLines
        )
    }
}
