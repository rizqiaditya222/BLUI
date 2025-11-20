package com.kotlin.blui.presentation.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kotlin.blui.presentation.component.MonthYearPickerDialog
import com.kotlin.blui.presentation.component.Transaction
import com.kotlin.blui.presentation.component.TransactionDateGroup
import com.kotlin.blui.presentation.component.TransactionFilter
import com.kotlin.blui.utils.IconMapper
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    onBack: () -> Unit = {},
    onTransactionClick: (String) -> Unit = {},
    initialMonth: Int? = null,
    initialYear: Int? = null
) {
    val context = LocalContext.current
    val viewModel = remember { DetailViewModel(context) }
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showMonthYearPicker by remember { mutableStateOf(false) }

    // Set initial month and year if provided (from delete navigation)
    LaunchedEffect(initialMonth, initialYear) {
        if (initialMonth != null && initialYear != null) {
            viewModel.onMonthYearChange(initialMonth, initialYear)
        }
    }

    val calendar = remember { Calendar.getInstance() }
    calendar.set(Calendar.MONTH, uiState.selectedMonth)
    calendar.set(Calendar.YEAR, uiState.selectedYear)

    // Format selected date for display
    val monthNames = arrayOf("Januari", "Februari", "Maret", "April", "Mei", "Juni",
                            "Juli", "Agustus", "September", "Oktober", "November", "Desember")
    val selectedDate = "${monthNames[uiState.selectedMonth]} ${uiState.selectedYear}"

    // Show error message
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Filter Row
                TransactionFilter(
                    selectedDate = selectedDate,
                    selectedType = uiState.selectedType,
                    onDateClick = { showMonthYearPicker = true },
                    onTypeChange = { viewModel.onTypeChange(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // List transaksi per tanggal
                if (uiState.transactionGroups.isEmpty() && !uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tidak ada transaksi",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.transactionGroups) { group ->
                            // Format date for display
                            val formattedDate = try {
                                val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val displayFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                                val date = apiFormat.parse(group.date)
                                if (date != null) displayFormat.format(date) else group.date
                            } catch (e: Exception) {
                                group.date
                            }

                            // Convert domain transactions to UI transactions
                            val transactions = group.transactions.map { transaction ->
                                val icon = IconMapper.mapIcon(transaction.category?.icon ?: "")
                                val color = try {
                                    Color(android.graphics.Color.parseColor(transaction.category?.color ?: "#3498DB"))
                                } catch (e: Exception) {
                                    Color(0xFF3498DB)
                                }
                                val amount = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(transaction.amount)

                                Transaction(icon, color, transaction.name, amount)
                            }

                            TransactionDateGroup(
                                date = formattedDate,
                                transactions = transactions,
                                onTransactionClick = { index ->
                                    // Get the actual transaction ID
                                    val transactionId = group.transactions.getOrNull(index)?.id
                                    transactionId?.let { onTransactionClick(it) }
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }

            // Loading indicator
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        // Month/Year Picker Dialog
        MonthYearPickerDialog(
            show = showMonthYearPicker,
            initialYear = uiState.selectedYear,
            initialMonth = uiState.selectedMonth,
            onDismiss = { showMonthYearPicker = false },
            onConfirm = { dateString ->
                showMonthYearPicker = false
            },
            onMonthYearSelected = { month, year ->
                viewModel.onMonthYearChange(month, year)
            }
        )
    }
}