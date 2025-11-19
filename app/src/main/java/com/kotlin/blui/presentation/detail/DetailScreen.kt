package com.kotlin.blui.presentation.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kotlin.blui.presentation.component.MonthYearPickerDialog
import com.kotlin.blui.presentation.component.Transaction
import com.kotlin.blui.presentation.component.TransactionDateGroup
import com.kotlin.blui.presentation.component.TransactionFilter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    onBack: () -> Unit = {},
    onTransactionClick: () -> Unit = {}
) {
    var selectedDate by remember { mutableStateOf("November 2025") }
    var selectedType by remember { mutableStateOf("Expense") }
    var showMonthYearPicker by remember { mutableStateOf(false) }

    val calendar = remember { Calendar.getInstance() }
    val pickerYear = remember { calendar.get(Calendar.YEAR) }
    val pickerMonthIndex = remember { calendar.get(Calendar.MONTH) }

    // Sample data dummy
    val transactionsByDate = listOf(
        "12 November 2025" to listOf(
            Transaction(Icons.Default.Restaurant, Color(0xFFFF6B6B), "Geprek Sambal Bawang", "Rp 20.000"),
            Transaction(Icons.Default.Restaurant, Color(0xFF4CAF50), "Makan Siang", "Rp 15.000")
        ),
        "11 November 2025" to listOf(
            Transaction(Icons.Default.Restaurant, Color(0xFFFF6B6B), "Kopi", "Rp 12.000")
        )
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detail") },
                colors = androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Filter Row - using reusable component
            TransactionFilter(
                selectedDate = selectedDate,
                selectedType = selectedType,
                onDateClick = { showMonthYearPicker = true },
                onTypeChange = { selectedType = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // List transaksi per tanggal
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(transactionsByDate) { (date, transactions) ->
                    TransactionDateGroup(
                        date = date,
                        transactions = transactions,
                        onTransactionClick = { onTransactionClick() }
                    )
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