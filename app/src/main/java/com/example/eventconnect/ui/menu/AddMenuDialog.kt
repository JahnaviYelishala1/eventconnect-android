package com.example.eventconnect.ui.menu

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.example.eventconnect.data.network.MenuCreateRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMenuDialog(
    loading: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (MenuCreateRequest, Uri?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Starter") }
    var foodType by remember { mutableStateOf("Veg") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val categories = listOf("Starter", "Soup", "Main Course", "Breads", "Rice", "Dessert", "Beverage")
    val foodTypes = listOf("Veg", "Non-Veg")

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> selectedImageUri = uri }

    // Modern Color Palette
    val dialogBg = Color(0xFFFFFFFF)
    val fieldBg = Color(0xFFF1F5F9)
    val primaryPurple = Color(0xFF6C3EF4)
    val gradientStart = Color(0xFF7C3AED)
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)
    val labelColor = Color(0xFF374151)

    Dialog(onDismissRequest = { if (!loading) onDismiss() }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            color = dialogBg
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Add Menu Item",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        fontSize = 20.sp
                    )
                )

                // Item Name
                MenuInputField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Item Name",
                    placeholder = "Enter item name",
                    icon = Icons.Default.Restaurant,
                    fieldBg = fieldBg,
                    textPrimary = textPrimary,
                    primaryPurple = primaryPurple,
                    labelColor = labelColor,
                    enabled = !loading
                )

                // Price
                MenuInputField(
                    value = price,
                    onValueChange = { price = it },
                    label = "Price",
                    placeholder = "0.00",
                    icon = Icons.Default.CurrencyRupee,
                    fieldBg = fieldBg,
                    textPrimary = textPrimary,
                    primaryPurple = primaryPurple,
                    labelColor = labelColor,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    enabled = !loading
                )

                // Description
                MenuInputField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Description",
                    placeholder = "Describe the item",
                    icon = Icons.Default.Description,
                    fieldBg = fieldBg,
                    textPrimary = textPrimary,
                    primaryPurple = primaryPurple,
                    labelColor = labelColor,
                    singleLine = false,
                    maxLines = 3,
                    enabled = !loading
                )

                // Category Dropdown
                MenuDropdownField(
                    label = "Category",
                    selected = category,
                    options = categories,
                    icon = Icons.Default.Category,
                    onSelect = { category = it },
                    fieldBg = fieldBg,
                    textPrimary = textPrimary,
                    primaryPurple = primaryPurple,
                    labelColor = labelColor,
                    enabled = !loading
                )

                // Food Type Dropdown
                MenuDropdownField(
                    label = "Food Type",
                    selected = foodType,
                    options = foodTypes,
                    icon = Icons.Default.Fastfood,
                    onSelect = { foodType = it },
                    fieldBg = fieldBg,
                    textPrimary = textPrimary,
                    primaryPurple = primaryPurple,
                    labelColor = labelColor,
                    enabled = !loading
                )

                // Image Picker Section
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Item Image",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = labelColor
                        )
                    )
                    
                    if (selectedImageUri != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(selectedImageUri),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            if (!loading) {
                                Surface(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .size(32.dp)
                                        .clickable { selectedImageUri = null },
                                    shape = CircleShape,
                                    color = Color.Black.copy(alpha = 0.6f)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.padding(6.dp))
                                }
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = { imagePicker.launch("image/*") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, if (loading) Color.Gray else primaryPurple),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryPurple),
                        enabled = !loading
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (selectedImageUri == null) "Select Image" else "Change Image", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(contentColor = textSecondary),
                        enabled = !loading
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            Log.d("AddMenuDialog", "Save button clicked. Name: $name")
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
                        },
                        modifier = Modifier
                            .height(48.dp)
                            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp), spotColor = primaryPurple.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        enabled = !loading
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .background(Brush.horizontalGradient(listOf(gradientStart, primaryPurple)))
                                .padding(horizontal = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (loading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    fieldBg: Color,
    textPrimary: Color,
    primaryPurple: Color,
    labelColor: Color,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    enabled: Boolean = true
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = labelColor
            )
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            placeholder = { Text(placeholder, color = Color(0xFF6B7280)) },
            leadingIcon = { Icon(icon, contentDescription = null, tint = primaryPurple, modifier = Modifier.size(20.dp)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = fieldBg,
                unfocusedContainerColor = fieldBg,
                disabledContainerColor = fieldBg,
                focusedIndicatorColor = primaryPurple,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = textPrimary,
                unfocusedTextColor = textPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = singleLine,
            maxLines = maxLines,
            keyboardOptions = keyboardOptions,
            enabled = enabled
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuDropdownField(
    label: String,
    selected: String,
    options: List<String>,
    icon: ImageVector,
    onSelect: (String) -> Unit,
    fieldBg: Color,
    textPrimary: Color,
    primaryPurple: Color,
    labelColor: Color,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = labelColor
            )
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { if (enabled) expanded = !expanded }
        ) {
            TextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                leadingIcon = { Icon(icon, contentDescription = null, tint = primaryPurple, modifier = Modifier.size(20.dp)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = fieldBg,
                    unfocusedContainerColor = fieldBg,
                    disabledContainerColor = fieldBg,
                    focusedIndicatorColor = primaryPurple,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = textPrimary,
                    unfocusedTextColor = textPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = enabled
            )

            if (enabled) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, color = textPrimary) },
                            onClick = {
                                onSelect(option)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
