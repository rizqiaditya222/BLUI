package com.kotlin.blui.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.kotlin.blui.presentation.component.CategoryIcon
import com.kotlin.blui.presentation.component.MonthYearPickerDialog
import com.kotlin.blui.presentation.component.PieChart
import com.kotlin.blui.presentation.component.PieChartData
import com.kotlin.blui.presentation.component.Transaction
import com.kotlin.blui.presentation.component.TransactionDateGroup
import com.kotlin.blui.presentation.component.TransactionFilter
import com.kotlin.blui.ui.theme.BlueLight
import com.kotlin.blui.ui.theme.BlueLightActive
import com.kotlin.blui.utils.IconMapper
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel,
    onNavigateToTransaction: () -> Unit = {},
    onNavigateToDetail: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    val monthNames = listOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )

    var selectedDate by remember {
        mutableStateOf("${monthNames[uiState.selectedMonth - 1]} ${uiState.selectedYear}")
    }
    var selectedType by remember { mutableStateOf("Expense") }
    var showMonthYearPicker by remember { mutableStateOf(false) }

    // Update selectedDate when month/year changes
    selectedDate = "${monthNames[uiState.selectedMonth - 1]} ${uiState.selectedYear}"

    val gradient = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            BlueLightActive
        )
    )

    // Calculate pie chart data based on selected type
    val pieChartData by remember(uiState.currentSummary, selectedType) {
        derivedStateOf {
            val categoryData = if (selectedType == "Income") {
                uiState.currentSummary?.incomeByCategory
            } else {
                uiState.currentSummary?.expenseByCategory
            }

            categoryData?.map { category ->
                PieChartData(
                    categoryName = category.categoryName,
                    amount = category.total,
                    color = parseColor(category.categoryColor)
                )
            } ?: emptyList()
        }
    }

    // Convert category summaries to transaction list for display
    val transactionsByDate by remember(uiState.currentSummary, selectedType) {
        derivedStateOf {
            val categoryData = if (selectedType == "Income") {
                uiState.currentSummary?.incomeByCategory
            } else {
                uiState.currentSummary?.expenseByCategory
            }

            if (categoryData?.isNotEmpty() == true) {
                // Group by current date as we only have summary data
                listOf(
                    "12 ${monthNames[uiState.selectedMonth - 1]} ${uiState.selectedYear}" to categoryData.map { category ->
                        Transaction(
                            categoryIcon = IconMapper.getIconForCategory(category.categoryIcon),
                            categoryColor = parseColor(category.categoryColor),
                            name = category.categoryName,
                            amount = formatCurrency(category.total)
                        )
                    }
                )
            } else {
                emptyList()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Halo, Elgin!", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.padding(vertical = 8.dp))

            // Balance Box with Gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush = gradient)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(color = BlueLight)
                        ) {
                            Text(
                                "Balance",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = formatCurrency(uiState.currentSummary?.balance ?: 0.0),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(
                            onClick = onNavigateToTransaction,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                    ) {
                        CategoryIcon(
                            icon = Icons.Default.Add,
                            color = Color.White,
                            circleSize = 48.dp,
                            iconSize = 24.dp,
                            iconTint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.padding(vertical = 4.dp))

            // Income and Expense Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Income Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = MaterialTheme.colorScheme.primary)
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(color = BlueLight)
                                .padding(horizontal = 10.dp, vertical = 0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Income",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                "Income",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.padding(vertical = 4.dp))
                        Text(
                            text = formatCurrency(uiState.currentSummary?.totalIncome ?: 0.0),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(horizontal = 4.dp))

                // Expense Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = MaterialTheme.colorScheme.primary)
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(color = BlueLight)
                                .padding(horizontal = 10.dp, vertical = 0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Expense",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                "Expense",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.padding(vertical = 4.dp))
                        Text(
                            text = formatCurrency(uiState.currentSummary?.totalExpense ?: 0.0),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Row - using reusable component
            TransactionFilter(
                selectedDate = selectedDate,
                selectedType = selectedType,
                onDateClick = { showMonthYearPicker = true },
                onTypeChange = { selectedType = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Pie Chart
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                PieChart(
                    data = pieChartData,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Detail section header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Detail",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Lihat Semua",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable(
                        onClick = onNavigateToDetail,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // List transaksi per tanggal
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            } else if (transactionsByDate.isNotEmpty()) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(transactionsByDate) { (date, transactions) ->
                        TransactionDateGroup(date = date, transactions = transactions)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tidak ada data",
                        color = Color.Gray,
                        modifier = Modifier.padding(32.dp)
                    )
                }
            }
        }

        // Month/Year Picker Dialog - using reusable component
        if (showMonthYearPicker) {
            MonthYearPickerDialog(
                show = showMonthYearPicker,
                initialYear = uiState.selectedYear,
                initialMonth = uiState.selectedMonth - 1,
                onDismiss = { showMonthYearPicker = false },
                onConfirm = { dateString ->
                    showMonthYearPicker = false
                },
                onMonthYearSelected = { month, year ->
                    viewModel.onMonthChange(month + 1)
                    viewModel.onYearChange(year)
                }
            )
        }
    }
}

fun parseColor(colorString: String): Color {
    return try {
        Color(colorString.toColorInt())
    } catch (_: Exception) {
        Color.Gray
    }
}

fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(amount)
}