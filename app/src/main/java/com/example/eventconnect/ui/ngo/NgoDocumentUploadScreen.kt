package com.example.eventconnect.ui.ngo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventconnect.data.network.NGODocumentRequest
import com.example.eventconnect.data.network.RetrofitClient
import com.example.eventconnect.ui.auth.getFirebaseIdToken
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NgoDocumentUploadScreen(
    navController: NavController
) {

    val documentTypes = listOf(
        "REG_CERT",
        "PAN",
        "80G",
        "12A",
        "TRUST_DEED"
    )

    var expanded by remember { mutableStateOf(false) }
    var selectedDocType by remember { mutableStateOf(documentTypes[0]) }
    var fileUrl by remember { mutableStateOf("") }

    var message by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Theme Colors
    val bgColor = Color(0xFFF8FAFC)
    val cardBg = Color(0xFFFFFFFF)
    val primaryPurple = Color(0xFF6C3EF4)
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)
    val inputBg = Color(0xFFF1F5F9)
    val gradientPurple = Brush.linearGradient(
        colors = listOf(Color(0xFF7C3AED), Color(0xFF6C3EF4))
    )

    Scaffold(
        containerColor = bgColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Spacer(modifier = Modifier.height(40.dp))

            // --- ILLUSTRATION / ICON ---
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(primaryPurple.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = primaryPurple
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- HEADER ---
            Text(
                text = "Upload NGO Documents",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                ),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Upload verification documents for your NGO",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = textSecondary
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- FORM CONTAINER ---
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0x1A000000)),
                shape = RoundedCornerShape(20.dp),
                color = cardBg
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    
                    // DOCUMENT TYPE DROPDOWN
                    Text(
                        text = "Document Type",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = textPrimary
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        TextField(
                            value = selectedDocType,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .menuAnchor(),
                            placeholder = { Text("Select Document Type", color = textSecondary) },
                            leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = primaryPurple) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = inputBg,
                                unfocusedContainerColor = inputBg,
                                disabledContainerColor = inputBg,
                                focusedIndicatorColor = primaryPurple,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = textPrimary,
                                unfocusedTextColor = textPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(cardBg)
                        ) {
                            documentTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type, color = textPrimary) },
                                    onClick = {
                                        selectedDocType = type
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // GOOGLE DRIVE LINK FIELD
                    Text(
                        text = "Google Drive Link",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = textPrimary
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    TextField(
                        value = fileUrl,
                        onValueChange = { fileUrl = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        placeholder = { Text("Paste Google Drive link", color = textSecondary) },
                        leadingIcon = { Icon(Icons.Default.Link, contentDescription = null, tint = primaryPurple) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = inputBg,
                            unfocusedContainerColor = inputBg,
                            disabledContainerColor = inputBg,
                            focusedIndicatorColor = primaryPurple,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- UPLOAD BUTTON ---
                    Button(
                        onClick = {
                            loading = true
                            message = null
                            getFirebaseIdToken(
                                onTokenReceived = { token ->
                                    scope.launch {
                                        try {
                                            val res = RetrofitClient.apiService.uploadNgoDocument(
                                                token = "Bearer $token",
                                                request = NGODocumentRequest(
                                                    document_type = selectedDocType,
                                                    file_url = fileUrl
                                                )
                                            )

                                            if (res.isSuccessful) {
                                                navController.navigate("ngo-home") {
                                                    popUpTo("ngo-documents") { inclusive = true }
                                                }
                                            } else {
                                                message = "Upload failed (${res.code()})"
                                            }
                                        } catch (e: Exception) {
                                            message = e.localizedMessage ?: "Unexpected error"
                                        } finally {
                                            loading = false
                                        }
                                    }
                                },
                                onError = {
                                    loading = false
                                    message = it
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp), clip = false),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        enabled = !loading && fileUrl.isNotBlank()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(gradientPurple, shape = RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    "Upload Document",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // --- MESSAGE / ERROR ---
                    message?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
