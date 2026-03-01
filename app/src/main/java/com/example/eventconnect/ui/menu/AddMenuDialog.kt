package com.example.eventconnect.ui.menu

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.eventconnect.data.network.MenuCreateRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMenuDialog(
    onDismiss: () -> Unit,
    onSave: (MenuCreateRequest, Uri?) -> Unit
) {

    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var category by remember { mutableStateOf("Starter") }
    var foodType by remember { mutableStateOf("Veg") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val categories = listOf(
        "Starter",
        "Soup",
        "Main Course",
        "Breads",
        "Rice",
        "Dessert",
        "Beverage"
    )

    val foodTypes = listOf("Veg", "Non-Veg")

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {

                    onSave(
                        MenuCreateRequest(
                            item_name = name,
                            description = description,
                            price = price.toDoubleOrNull() ?: 0.0,
                            category = category,
                            food_type = foodType,
                            image_url = null
                        ),
                        selectedImageUri
                    )
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = {
            Text("Add Menu Item")
        },
        text = {

            Column {

                /* -------- Item Name -------- */

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                /* -------- Price -------- */

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                /* -------- Description -------- */

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                /* -------- Category Dropdown -------- */

                CategoryDropdown(
                    label = "Category",
                    selected = category,
                    options = categories
                ) {
                    category = it
                }

                Spacer(Modifier.height(8.dp))

                /* -------- Food Type Dropdown -------- */

                CategoryDropdown(
                    label = "Food Type",
                    selected = foodType,
                    options = foodTypes
                ) {
                    foodType = it
                }

                Spacer(Modifier.height(12.dp))

                /* -------- Image Picker -------- */

                Button(
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Select Image")
                }

                Spacer(Modifier.height(8.dp))

                selectedImageUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit
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
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}