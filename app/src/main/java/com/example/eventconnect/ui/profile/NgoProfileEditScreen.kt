package com.example.eventconnect.ui.profile

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
                Toast.makeText(
                    context,
                    "Location permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    // ---------------- LOAD EXISTING PROFILE ----------------
    LaunchedEffect(Unit) {
        getFirebaseIdToken(
            onTokenReceived = { token ->
                scope.launch {
                    try {
                        val res = RetrofitClient.apiService
                            .getNgoProfile("Bearer $token")

                        if (res.isSuccessful && res.body() != null) {
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
                        }
                    } finally {
                        loading = false
                    }
                }
            },
            onError = { loading = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Edit NGO Profile") })
        }
    ) { padding ->

        if (loading) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ---------------- PROFILE IMAGE ----------------
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clickable { imagePicker.launch("image/*") },
                contentAlignment = Alignment.BottomEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
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
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(name, { name = it }, label = { Text("NGO Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(establishedYear, { establishedYear = it }, label = { Text("Established Year") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(about, { about = it }, label = { Text("About") }, modifier = Modifier.fillMaxWidth(), maxLines = 4)
            OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(phone, { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(address, { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            ) {
                Text("Use Current Location")
            }

            Spacer(Modifier.height(12.dp))

            // ✅ OpenStreetMap instead of GoogleMap
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

            Spacer(Modifier.height(24.dp))

            Button(
                enabled = !saving,
                onClick = {
                    saving = true
                    getFirebaseIdToken(
                        onTokenReceived = { token ->
                            scope.launch {
                                try {
                                    var finalImageUrl = imageUrl

                                    if (selectedImageUri != null) {
                                        val file =
                                            uriToFile(context, selectedImageUri!!)
                                        val part =
                                            MultipartBody.Part.createFormData(
                                                "file",
                                                file.name,
                                                file.asRequestBody(
                                                    "image/*".toMediaType()
                                                )
                                            )

                                        val imgRes =
                                            RetrofitClient.apiService
                                                .uploadNgoImage(
                                                    "Bearer $token",
                                                    part
                                                )

                                        if (imgRes.isSuccessful) {
                                            finalImageUrl =
                                                imgRes.body()?.image_url
                                        }
                                    }

                                    RetrofitClient.apiService.updateNgoProfile(
                                        "Bearer $token",
                                        NgoProfileRequest(
                                            name,
                                            establishedYear,
                                            about,
                                            email,
                                            phone,
                                            address,
                                            latitude,
                                            longitude,
                                            finalImageUrl
                                        )
                                    )

                                    navController.popBackStack()
                                } finally {
                                    saving = false
                                }
                            }
                        },
                        onError = { saving = false }
                    )
                }
            ) {
                Text(if (saving) "Saving..." else "Save Profile")
            }
        }
    }
}
