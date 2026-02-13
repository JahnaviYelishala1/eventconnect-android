package com.example.eventconnect.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun FindCatererScreen(
    navController: NavController,
    eventId: Int,
    viewModel: FindCatererViewModel = viewModel(),
    defaultMealStyle: String = "Buffet",
    defaultFoodType: String = "Both"
) {

    val caterers by viewModel.caterers.collectAsState()
    val loading by viewModel.loading.collectAsState()

    var step by remember { mutableStateOf(1) }
    var selectedFoodType by remember { mutableStateOf(defaultFoodType) }
    var selectedMealStyle by remember { mutableStateOf(defaultMealStyle) }
    var priceRange by remember { mutableStateOf(500f..2000f) }

    // ✅ Initial Load
    LaunchedEffect(eventId) {
        viewModel.loadCaterers(eventId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Matching Caterers") })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            /* ---------------- FILTER CARD ---------------- */

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFAAB19C)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {

                AnimatedContent(
                    targetState = step,
                    transitionSpec = {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(300)
                        ).togetherWith(
                            slideOutHorizontally(
                                targetOffsetX = { fullWidth -> -fullWidth },
                                animationSpec = tween(300)
                            )
                        )
                    },
                    label = "FilterAnimation"
                ) { currentStep ->

                    when (currentStep) {

                        /* ---------------- STEP 1 ---------------- */
                        1 -> FilterStep1(
                            selectedFoodType = selectedFoodType
                        ) {
                            selectedFoodType = it
                            step = 2
                        }

                        /* ---------------- STEP 2 ---------------- */
                        2 -> FilterStep2(
                            selectedMealStyle = selectedMealStyle
                        ) {
                            selectedMealStyle = it
                            step = 3
                        }

                        /* ---------------- STEP 3 ---------------- */
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
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            /* ---------------- RESULTS ---------------- */

            Text(
                text = "Caterers Found: ${caterers.size}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            if (loading) {
                CircularProgressIndicator()
            } else {
                caterers.forEach { caterer ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = caterer.business_name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text("₹${caterer.price_per_plate}")
                            Text("⭐ ${caterer.rating}")
                            Text("${caterer.distance_km} km away")
                        }
                    }
                }
            }
        }
    }
}

/* ---------------------------------------------------- */
/* ---------------- FILTER STEPS ---------------------- */
/* ---------------------------------------------------- */

/* ---------------------------------------------------- */
/* ---------------- FILTER STEP 1 --------------------- */
/* ---------------------------------------------------- */

@Composable
private fun FilterStep1(
    selectedFoodType: String,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Select Food Preference",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            FoodOptionCard(
                title = "Veg",
                selected = selectedFoodType == "Veg",
                onClick = { onSelect("Veg") }
            )

            FoodOptionCard(
                title = "Non-Veg",
                selected = selectedFoodType == "Non-Veg",
                onClick = { onSelect("Non-Veg") }
            )

            FoodOptionCard(
                title = "Both",
                selected = selectedFoodType == "Both",
                onClick = { onSelect("Both") }
            )
        }
    }
}

/* ---------------------------------------------------- */
/* ---------------- FILTER STEP 2 --------------------- */
/* ---------------------------------------------------- */

@Composable
private fun FilterStep2(
    selectedMealStyle: String,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Select Meal Style",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        listOf(
            "Buffet",
            "Live Cooking",
            "Snacks",
            "Packed Meals"
        ).forEach { style ->

            FoodOptionCard(
                title = style,
                selected = selectedMealStyle == style,
                onClick = { onSelect(style) }
            )

            Spacer(Modifier.height(12.dp))
        }
    }
}

/* ---------------------------------------------------- */
/* ---------------- FILTER STEP 3 --------------------- */
/* ---------------------------------------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterStep3(
    priceRange: ClosedFloatingPointRange<Float>,
    onRangeChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    onApply: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Price Per Plate",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        RangeSlider(
            value = priceRange,
            onValueChange = {
                onRangeChanged(
                    it.start.toInt().toFloat()..
                            it.endInclusive.toInt().toFloat()
                )
            },
            valueRange = 0f..10000f,
            steps = 19
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "₹${priceRange.start.toInt()} - ₹${priceRange.endInclusive.toInt()}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = onApply,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Apply Filters")
        }
    }
}

/* ---------------------------------------------------- */
/* ---------------- OPTION CARD ----------------------- */
/* ---------------------------------------------------- */

@Composable
fun FoodOptionCard(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .size(width = 110.dp, height = 70.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                if (selected) Color(0xFF6B7C6F)
                else Color(0xFFEDEDED)
        )
    ) {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {

            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                color = if (selected) Color.White else Color.Black
            )
        }
    }
}
