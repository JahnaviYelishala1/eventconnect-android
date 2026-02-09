package com.example.eventconnect.ui.ngo

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Upload NGO Documents",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(24.dp))

        /* ---------------- DOCUMENT TYPE DROPDOWN ---------------- */

        Text(
            text = "Document Type",
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(Modifier.height(4.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {

            OutlinedTextField(
                value = selectedDocType,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                label = { Text("Select Document Type") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                documentTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            selectedDocType = type
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        /* ---------------- GOOGLE DRIVE LINK ---------------- */

        Text(
            text = "Google Drive Link",
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(Modifier.height(4.dp))

        OutlinedTextField(
            value = fileUrl,
            onValueChange = { fileUrl = it },
            label = { Text("Paste Google Drive link") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        /* ---------------- UPLOAD BUTTON ---------------- */

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading && fileUrl.isNotBlank(),
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
                                message = e.message ?: "Unexpected error"
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
            }
        ) {
            Text(if (loading) "Uploading..." else "Upload Document")
        }

        message?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}
