package com.example.eventconnect.ui.revenue

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.components.XAxis
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.formatter.ValueFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevenueScreen(
    viewModel: RevenueViewModel = viewModel()
) {

    val revenue by viewModel.revenue.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadRevenue()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Revenue Dashboard") })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            when {
                loading -> CircularProgressIndicator()

                error != null ->
                    Text(error ?: "", color = MaterialTheme.colorScheme.error)

                revenue != null -> {

                    RevenueCard("Total Revenue", "₹${revenue!!.total_revenue}")
                    RevenueCard("This Month", "₹${revenue!!.this_month_revenue}")
                    RevenueCard("Paid Bookings", revenue!!.total_paid_bookings.toString())
                    RevenueCard("Pending Bookings", revenue!!.pending_bookings.toString())

                    Spacer(Modifier.height(24.dp))

                    Text(
                        "Monthly Revenue",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.height(12.dp))

                    RevenueBarChart(revenue!!.monthly_breakdown)
                }
            }
        }
    }
}

@Composable
fun RevenueCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge)
        }
    }
}


@Composable
fun RevenueBarChart(
    monthlyData: List<com.example.eventconnect.data.network.MonthlyRevenue>
) {

    AndroidView(
        factory = { context ->

            BarChart(context).apply {

                val entries = monthlyData.mapIndexed { index, item ->
                    BarEntry(index.toFloat(), item.revenue.toFloat())
                }

                val dataSet = BarDataSet(entries, "Revenue").apply {
                    setColor(Color.parseColor("#4CAF50"))
                    valueTextSize = 12f
                    valueTextColor = Color.WHITE
                }

                data = BarData(dataSet)

                // 🔥 Month labels from backend
                val monthLabels = monthlyData.map { it.month }

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    setDrawGridLines(false)
                    textColor = Color.WHITE
                    axisLineColor = Color.WHITE

                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val index = value.toInt()
                            return if (index in monthLabels.indices) {
                                monthLabels[index]
                            } else {
                                ""
                            }
                        }
                    }
                }

                axisLeft.apply {
                    textColor = Color.WHITE
                    axisLineColor = Color.WHITE
                    gridColor = Color.DKGRAY
                }

                axisRight.isEnabled = false
                description.isEnabled = false
                legend.textColor = Color.WHITE

                invalidate()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}