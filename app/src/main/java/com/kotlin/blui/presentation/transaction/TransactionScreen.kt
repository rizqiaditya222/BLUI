package com.kotlin.blui.presentation.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotlin.blui.presentation.component.CategoryData
import com.kotlin.blui.presentation.component.CategoryPickerDialog
import com.kotlin.blui.presentation.component.CustomOutlinedTextField
import com.kotlin.blui.presentation.component.TransactionTypeToggle
import com.kotlin.blui.utils.IconMapper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    transactionId: String? = null,
    initialTransactionType: String = "Expense",
    onBack: () -> Unit = {},
    onSave: () -> Unit = {},
    onDelete: (month: Int?, year: Int?) -> Unit = { _, _ -> },
    onAddCategory: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel = remember { TransactionViewModel(context) }
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showDatePicker by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var deleteMode by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // Load transaction data in edit mode
    LaunchedEffect(transactionId) {
        if (isEditMode && !transactionId.isNullOrEmpty()) {
            viewModel.loadTransaction(transactionId)
        }
    }

    // Reset delete mode when categories change (after delete or load)
    LaunchedEffect(uiState.categories.size) {
        if (deleteMode && uiState.categories.isNotEmpty()) {
            // Categories list has changed, keep dialog open but data is refreshed
            println("Categories updated, count: ${uiState.categories.size}")
        }
    }

    // Set initial transaction type
    LaunchedEffect(initialTransactionType) {
        if (!isEditMode) {
            viewModel.onTransactionTypeChange(initialTransactionType)
        }
    }

    // Show error message
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    // Navigate back when success
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSave()
        }
    }

    val title = if (isEditMode) {
        "Edit ${if (uiState.transactionType == "Expense") "Expense" else "Income"}"
    } else {
        "Tambah"
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    Text(
                        "simpan",
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable {
                                viewModel.saveTransaction(onSuccess = {})
                            }
                    )
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
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Only show toggle when NOT in edit mode
                if (!isEditMode) {
                    TransactionTypeToggle(
                        selectedType = uiState.transactionType,
                        onTypeSelected = { viewModel.onTransactionTypeChange(it) }
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }

                Column {
                    Text(
                        text = "Nama",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomOutlinedTextField(
                        value = uiState.transactionName,
                        onValueChange = { viewModel.onTransactionNameChange(it) },
                        placeholder = "Contoh: Makan siang",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Amount Field
                Column {
                    Text(
                        text = "Jumlah",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomOutlinedTextField(
                        value = uiState.amount,
                        onValueChange = { viewModel.onAmountChange(it) },
                        placeholder = "0",
                        modifier = Modifier.fillMaxWidth(),
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Kategori Field with Icon Button
                Column {
                    Text(
                        text = "Kategori",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CustomOutlinedTextField(
                            value = uiState.selectedCategory?.name ?: "",
                            onValueChange = { },
                            placeholder = "Pilih kategori",
                            modifier = Modifier.weight(1f),
                            readOnly = true
                        )

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(
                                    uiState.selectedCategory?.let {
                                        Color(android.graphics.Color.parseColor(it.color))
                                    } ?: MaterialTheme.colorScheme.primary
                                )
                                .clickable { showCategoryPicker = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = uiState.selectedCategory?.let {
                                    IconMapper.mapIcon(it.icon)
                                } ?: Icons.Default.CalendarToday,
                                contentDescription = "Category Icon",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tanggal Field with Calendar Icon Button
                Column {
                    Text(
                        text = "Tanggal",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = uiState.date.let {
                                if (it.isNotEmpty()) {
                                    try {
                                        val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                        val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                        val date = apiFormat.parse(it)
                                        if (date != null) displayFormat.format(date) else it
                                    } catch (e: Exception) {
                                        it
                                    }
                                } else ""
                            },
                            onValueChange = { },
                            modifier = Modifier.weight(1f),
                            readOnly = true,
                            placeholder = { Text("Pilih tanggal") },
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White,
                                disabledBorderColor = Color(0xFFE0E0E0),
                                disabledContainerColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            enabled = false
                        )

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .clickable { showDatePicker = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Calendar",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Catatan Field
                Column {
                    Text(
                        text = "Catatan (Opsional)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomOutlinedTextField(
                        value = uiState.note,
                        onValueChange = { viewModel.onNoteChange(it) },
                        placeholder = "Tambahkan catatan...",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        singleLine = false
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Delete button in edit mode
                if (isEditMode) {
                    androidx.compose.material3.OutlinedButton(
                        onClick = {
                            viewModel.deleteTransaction(onSuccess = {
                                // Pass transaction month and year for navigation
                                onDelete(uiState.transactionMonth, uiState.transactionYear)
                            })
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(
                            text = "Hapus Transaksi",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
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

        // Date Picker Dialog
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            viewModel.onDateChange(sdf.format(Date(millis)))
                        }
                        showDatePicker = false
                    }) {
                        Text("OK", color = MaterialTheme.colorScheme.primary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Batal", color = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = androidx.compose.material3.DatePickerDefaults.colors(
                    containerColor = Color.White
                )
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = androidx.compose.material3.DatePickerDefaults.colors(
                        containerColor = Color.White
                    )
                )
            }
        }

        // Category Picker Dialog
        CategoryPickerDialog(
            show = showCategoryPicker,
            categories = uiState.categories.map { category ->
                CategoryData(
                    id = category.id,
                    name = category.name,
                    icon = IconMapper.mapIcon(category.icon), // Map icon using IconMapper
                    color = Color(android.graphics.Color.parseColor(category.color))
                )
            },
            deleteMode = deleteMode,
            onDismiss = { showCategoryPicker = false },
            onCategorySelected = { selectedCategory ->
                val category = uiState.categories.find { it.id == selectedCategory.id }
                category?.let { viewModel.onCategorySelect(it) }
                showCategoryPicker = false
            },
            onAddCategory = {
                showCategoryPicker = false
                onAddCategory()
            },
            onDeleteCategory = { categoryId ->
                viewModel.onDeleteCategory(categoryId)
            },
            onToggleDeleteMode = {
                deleteMode = !deleteMode
            }
        )
    }
}
