package com.kotlin.blui.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotlin.blui.presentation.component.CategoryIcon
import com.kotlin.blui.presentation.component.MonthYearPickerDialog
import com.kotlin.blui.presentation.component.PieChart
import com.kotlin.blui.presentation.component.PieChartData
import com.kotlin.blui.presentation.component.Transaction
import com.kotlin.blui.presentation.component.TransactionDateGroup
import com.kotlin.blui.presentation.component.TransactionFilter
import com.kotlin.blui.ui.theme.BlueLight
import com.kotlin.blui.ui.theme.BlueLightActive
import com.kotlin.blui.ui.theme.BluiTheme
import java.util.Calendar

// Extended Transaction data with category info
data class TransactionWithCategory(
    val transaction: Transaction,
    val categoryId: String,
    val categoryName: String
)

@Composable
fun HomeScreen(
    onNavigateToTransaction: (String) -> Unit = {},
    onNavigateToDetail: () -> Unit = {}
) {
    var selectedDate by remember { mutableStateOf("November 2025") }
    var selectedType by remember { mutableStateOf("Expense") }
    var showMonthYearPicker by remember { mutableStateOf(false) }

    val calendar = remember { Calendar.getInstance() }
    val pickerYear = remember { calendar.get(Calendar.YEAR) }
    val pickerMonthIndex = remember { calendar.get(Calendar.MONTH) }

    // Sample data dummy with category info
    val transactionsWithCategory = listOf(
        "20 November 2025" to listOf(
            TransactionWithCategory(
                Transaction(Icons.Default.Restaurant, Color(0xFF4ECAF6), "Geprek Sambal Bawang", "Rp 20.000"),
                "cat1", "Makanan & Minuman"
            ),
            TransactionWithCategory(
                Transaction(Icons.Default.LocalCafe, Color(0xFF4ECAF6), "Kopi Pagi", "Rp 15.000"),
                "cat1", "Makanan & Minuman"
            )
        ),
        "19 November 2025" to listOf(
            TransactionWithCategory(
                Transaction(Icons.Default.ShoppingCart, Color(0xFFFFC658), "Belanja Bulanan", "Rp 150.000"),
                "cat3", "Lain-lain"
            ),
            TransactionWithCategory(
                Transaction(Icons.Default.Restaurant, Color(0xFF4ECAF6), "Makan Malam", "Rp 35.000"),
                "cat1", "Makanan & Minuman"
            ),
            TransactionWithCategory(
                Transaction(Icons.Default.DirectionsCar, Color(0xFFFF9CAC), "Bensin", "Rp 50.000"),
                "cat2", "Transportasi"
            )
        )
    )

    // Convert to format for TransactionDateGroup
    val transactionsByDate = transactionsWithCategory.map { (date, items) ->
        date to items.map { it.transaction }
    }

    // Calculate pie chart data from all transactions
    val pieChartData by remember(transactionsWithCategory) {
        derivedStateOf {
            val categoryMap = mutableMapOf<String, PieChartData>()

            transactionsWithCategory.forEach { (_, transactions) ->
                transactions.forEach { item ->
                    // Parse amount: remove "Rp", spaces, and dots (thousand separator)
                    val amountStr = item.transaction.amount
                        .replace("Rp", "")
                        .replace(".", "")
                        .replace(" ", "")
                        .trim()
                    val amount = amountStr.toDoubleOrNull() ?: 0.0

                    if (categoryMap.containsKey(item.categoryId)) {
                        val existing = categoryMap[item.categoryId]!!
                        categoryMap[item.categoryId] = existing.copy(
                            amount = existing.amount + amount
                        )
                    } else {
                        categoryMap[item.categoryId] = PieChartData(
                            categoryName = item.categoryName,
                            amount = amount,
                            color = item.transaction.categoryColor
                        )
                    }
                }
            }

            categoryMap.values.toList().sortedByDescending { it.amount }
        }
    }

    val gradient = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            BlueLightActive
        )
    )

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
                            "Rp. 500.000",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(
                            onClick = { onNavigateToTransaction("add") },
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
                            "Rp. 300.000",
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
                            "Rp. 200K",
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
            PieChart(
                data = pieChartData,
                modifier = Modifier.fillMaxWidth()
            )

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
                        onClick = { onNavigateToDetail() },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // List transaksi per tanggal
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(transactionsByDate) { (date, transactions) ->
                    TransactionDateGroup(date = date, transactions = transactions)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Month/Year Picker Dialog - using reusable component
        MonthYearPickerDialog(
            show = showMonthYearPicker,
            initialYear = pickerYear,
            initialMonth = pickerMonthIndex,
            onDismiss = { showMonthYearPicker = false },
            onConfirm = { dateString ->
                selectedDate = dateString
                showMonthYearPicker = false
            }
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    BluiTheme {
        HomeScreen()
    }
}