package com.example.eventconnect.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun FindCatererScreen(
    navController: NavController,
    eventId: Int,
    attendees: Int,
    viewModel: FindCatererViewModel = viewModel(),
    defaultMealStyle: String = "Buffet",
    defaultFoodType: String = "Both"
) {
    val caterers by viewModel.caterers.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    var step by remember { mutableStateOf(1) }
    var selectedFoodType by remember { mutableStateOf(defaultFoodType) }
    var selectedMealStyle by remember { mutableStateOf(defaultMealStyle) }
    var priceRange by remember { mutableStateOf(500f..2000f) }

    val primaryDark = Color(0xFF1A1C1E)
    val backgroundLavender = Color(0xFFF5F3FF)

    LaunchedEffect(eventId) {
        viewModel.loadCaterers(eventId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.White, backgroundLavender)
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(top = 24.dp, bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Matching Caterers",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = primaryDark,
                        letterSpacing = (-0.5).sp
                    )
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    /* ---------------- MAIN FILTER CARD ---------------- */
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 16.dp,
                                shape = RoundedCornerShape(28.dp),
                                ambientColor = Color(0xFF6C3EF4).copy(alpha = 0.15f)
                            ),
                        shape = RoundedCornerShape(28.dp),
                        color = Color.White
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AnimatedContent(
                                targetState = step,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(400)) togetherWith
                                            fadeOut(animationSpec = tween(400))
                                },
                                label = "FilterSteps"
                            ) { currentStep ->
                                when (currentStep) {
                                    1 -> FilterStep1(selectedFoodType) {
                                        selectedFoodType = it
                                        step = 2
                                    }
                                    2 -> FilterStep2(selectedMealStyle) {
                                        selectedMealStyle = it
                                        step = 3
                                    }
                                    3 -> FilterStep3(
                                        priceRange = priceRange,
                                        onRangeChanged = { priceRange = it }
                                    ) {
                                        viewModel.loadCaterers(
                                            eventId = eventId,
                                            vegOnly = if (selectedFoodType == "Veg") true else null,
                                            nonVegOnly = if (selectedFoodType == "Non-Veg") true else null,
                                            minPrice = priceRange.start.toDouble(),
                                            maxPrice = priceRange.endInclusive.toDouble(),
                                            mealStyle = selectedMealStyle
                                        )
                                        step = 4
                                    }
                                    4 -> ResultsSummary(caterers.size) { step = 1 }
                                }
                            }
                        }
                    }

                    if (step == 4) {
                        Spacer(Modifier.height(32.dp))
                        
                        if (loading) {
                            CircularProgressIndicator(color = Color(0xFF6C3EF4))
                        }

                        error?.let {
                            Text(it, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                        }

                        caterers.forEach { caterer ->
                            CatererItemCard(caterer) {
                                navController.navigate(
                                    "caterer_menu/$eventId/${caterer.id}/$attendees/$selectedFoodType/${priceRange.start.toInt()}/${priceRange.endInclusive.toInt()}"
                                )
                            }
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterStep1(
    selectedFoodType: String,
    onSelect: (String) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Food Preference",
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1C1E)
        )
        Spacer(Modifier.height(8.dp))
        HorizontalDivider(modifier = Modifier.width(40.dp), thickness = 3.dp, color = Color(0xFF6C3EF4))
        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf("Veg", "Non-Veg", "Both").forEach { type ->
                FoodOptionPill(
                    title = type,
                    selected = selectedFoodType == type,
                    modifier = Modifier.weight(1f),
                    onClick = { onSelect(type) }
                )
            }
        }
    }
}

@Composable
private fun FilterStep2(
    selectedMealStyle: String,
    onSelect: (String) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Select Meal Style",
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1C1E)
        )
        Spacer(Modifier.height(8.dp))
        HorizontalDivider(modifier = Modifier.width(40.dp), thickness = 3.dp, color = Color(0xFF6C3EF4))
        Spacer(Modifier.height(24.dp))

        val styles = listOf(
            MealStyleOption("Buffet", Icons.Default.Restaurant),
            MealStyleOption("Live Cooking", Icons.Default.Fireplace),
            MealStyleOption("Snacks", Icons.Default.Fastfood),
            MealStyleOption("Packed Meals", Icons.Default.Inventory2)
        )

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            styles.chunked(2).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    row.forEach { option ->
                        MealStyleCard(
                            option = option,
                            selected = selectedMealStyle == option.name,
                            modifier = Modifier.weight(1f),
                            onClick = { onSelect(option.name) }
                        )
                    }
                }
            }
        }
    }
}

data class MealStyleOption(val name: String, val icon: ImageVector)

@Composable
fun MealStyleCard(
    option: MealStyleOption,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(if (selected) 1.05f else 1f, label = "scale")
    val primaryPurple = Color(0xFF6C3EF4)
    val secondaryViolet = Color(0xFF9F5FFF)

    Surface(
        modifier = modifier
            .height(100.dp)
            .scale(scale)
            .clickable { onClick() }
            .then(
                if (selected) Modifier.shadow(12.dp, RoundedCornerShape(20.dp), ambientColor = primaryPurple.copy(alpha = 0.5f))
                else Modifier
            ),
        shape = RoundedCornerShape(20.dp),
        color = if (selected) Color.Transparent else Color(0xFFF1F5F9)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (selected) Modifier.background(Brush.horizontalGradient(listOf(primaryPurple, secondaryViolet)))
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = null,
                    tint = if (selected) Color.White else Color(0xFF6C3EF4),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = option.name,
                    fontSize = 14.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = if (selected) Color.White else Color(0xFF4B5563)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterStep3(
    priceRange: ClosedFloatingPointRange<Float>,
    onRangeChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    onApply: () -> Unit
) {
    val primaryPurple = Color(0xFF6C3EF4)
    val secondaryViolet = Color(0xFF9F5FFF)
    val primaryDark = Color(0xFF1A1C1E)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Payments, contentDescription = null, tint = primaryPurple, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Price Per Plate",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = primaryDark
            )
        }
        Spacer(Modifier.height(8.dp))
        HorizontalDivider(modifier = Modifier.width(40.dp), thickness = 3.dp, color = primaryPurple)
        Spacer(Modifier.height(32.dp))
        
        RangeSlider(
            value = priceRange,
            onValueChange = {
                onRangeChanged(it.start.toInt().toFloat()..it.endInclusive.toInt().toFloat())
            },
            valueRange = 0f..10000f,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = primaryPurple,
                inactiveTrackColor = Color(0xFFE5E7EB),
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            ),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        
        Spacer(Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "₹${priceRange.start.toInt()}",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = primaryDark
            )
            Text(
                text = " – ",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B5563)
            )
            Text(
                text = "₹${priceRange.endInclusive.toInt()}",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = primaryDark
            )
        }
        
        Spacer(Modifier.height(40.dp))
        
        Button(
            onClick = onApply,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(12.dp, RoundedCornerShape(24.dp), ambientColor = primaryPurple.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.horizontalGradient(listOf(primaryPurple, secondaryViolet))),
                contentAlignment = Alignment.Center
            ) {
                Text("Apply Filters", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun ResultsSummary(count: Int, onReset: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Celebration, contentDescription = null, tint = Color(0xFF6C3EF4), modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(16.dp))
        Text("Search Results", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = Color(0xFFE5E7EB))
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Caterers Found: $count",
            fontSize = 17.sp,
            color = Color(0xFF374151),
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(24.dp))
        TextButton(onClick = onReset) {
            Text("Modify Filters", color = Color(0xFF6C3EF4), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FoodOptionPill(
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(if (selected) 1.05f else 1f, label = "scale")
    val primaryPurple = Color(0xFF6C3EF4)
    val secondaryViolet = Color(0xFF9F5FFF)

    Surface(
        modifier = modifier
            .height(54.dp)
            .scale(scale)
            .clickable { onClick() }
            .then(
                if (selected) Modifier.shadow(10.dp, RoundedCornerShape(27.dp), ambientColor = primaryPurple)
                else Modifier
            ),
        shape = RoundedCornerShape(27.dp),
        color = if (selected) Color.Transparent else Color(0xFFF1F5F9)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (selected) Modifier.background(Brush.horizontalGradient(listOf(primaryPurple, secondaryViolet)))
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) Color.White else Color(0xFF4B5563)
            )
        }
    }
}

@Composable
fun CatererItemCard(caterer: com.example.eventconnect.data.network.CatererResponse, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(6.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = caterer.business_name,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C1E)
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("₹${caterer.price_per_plate}", fontWeight = FontWeight.Bold, color = Color(0xFF6C3EF4))
                    Spacer(Modifier.width(16.dp))
                    Text("⭐ ${caterer.rating}", color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(16.dp))
                    Text("${caterer.distance_km} km", color = Color.Gray, fontSize = 13.sp)
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF6C3EF4)
            )
        }
    }
}
