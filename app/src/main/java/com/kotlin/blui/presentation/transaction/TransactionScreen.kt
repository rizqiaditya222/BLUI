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
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotlin.blui.presentation.component.CategoryData
import com.kotlin.blui.presentation.component.CategoryPickerDialog
import com.kotlin.blui.presentation.component.CustomOutlinedTextField
import com.kotlin.blui.presentation.component.DeleteButton
import com.kotlin.blui.presentation.component.TransactionTypeToggle
import com.kotlin.blui.ui.theme.BluiTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    modifier: Modifier = Modifier,
    isEditMode: Boolean = true,
    initialTransactionType: String = "Income",
    onBack: () -> Unit = {},
    onSave: () -> Unit = {},
    onDelete: () -> Unit = {},
    onAddCategory: () -> Unit = {}
) {
    var transactionType by remember { mutableStateOf(initialTransactionType) }
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var selectedCategoryIcon by remember { mutableStateOf(Icons.Default.Restaurant) }
    var selectedCategoryColor by remember { mutableStateOf(Color(0xFFFF6B6B)) }
    var date by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var categoryDeleteMode by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // Sample categories
    val categories = remember {
        listOf(
            CategoryData("1", "Makanan & Minuman", Icons.Default.Restaurant, Color(0xFF4ECAF6)),
            CategoryData("2", "Transportasi", Icons.Default.DirectionsCar, Color(0xFFFF9CAC)),
            CategoryData("3", "Belanja", Icons.Default.ShoppingCart, Color(0xFFFFC658)),
            CategoryData("4", "Kopi", Icons.Default.LocalCafe, Color(0xFF4CAF50))
        )
    }

    val title = if (isEditMode) {
        "Edit ${if (transactionType == "Expense") "Expense" else "Income"}"
    } else {
        "Tambah"
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
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
                            .clickable { onSave() }
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Only show toggle when NOT in edit mode
            if (!isEditMode) {
                TransactionTypeToggle(
                    selectedType = transactionType,
                    onTypeSelected = { transactionType = it }
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
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "",
                    modifier = Modifier.fillMaxWidth()
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
                        value = category,
                        onValueChange = { },
                        placeholder = "",
                        modifier = Modifier.weight(1f),
                        readOnly = true
                    )

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(selectedCategoryColor)
                            .clickable {
                                showCategoryPicker = true
                                categoryDeleteMode = false
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = selectedCategoryIcon,
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
                        value = date,
                        onValueChange = { },
                        modifier = Modifier.weight(1f),
                        readOnly = true,
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

                    // Calendar Icon Button
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

            // Catatan Field - Using CustomOutlinedTextField with multiline
            Column {
                Text(
                    text = "Catatan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                CustomOutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    placeholder = "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    singleLine = false
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Only show delete button when in edit mode
            if (isEditMode) {
                DeleteButton(onClick = onDelete, text ="Hapus")
            }
        }

        // Date Picker Dialog
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            date = sdf.format(Date(millis))
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
            categories = categories,
            deleteMode = categoryDeleteMode,
            onDismiss = {
                showCategoryPicker = false
                categoryDeleteMode = false
            },
            onCategorySelected = { selectedCategory ->
                category = selectedCategory.name
                selectedCategoryIcon = selectedCategory.icon
                selectedCategoryColor = selectedCategory.color
                showCategoryPicker = false
                categoryDeleteMode = false
            },
            onAddCategory = {
                showCategoryPicker = false
                onAddCategory()
            },
            onDeleteCategory = { categoryId ->
                // TODO: Handle delete category
                println("Delete category: $categoryId")
            },
            onToggleDeleteMode = {
                categoryDeleteMode = !categoryDeleteMode
            }
        )
    }
}

@Preview(showBackground = true, name = "Add Mode")
@Composable
fun TransactionScreenAddPreview() {
    BluiTheme {
        TransactionScreen(isEditMode = false)
    }
}

@Preview(showBackground = true, name = "Edit Mode - Expense")
@Composable
fun TransactionScreenEditExpensePreview() {
    BluiTheme {
        TransactionScreen(isEditMode = true, initialTransactionType = "Expense")
    }
}

@Preview(showBackground = true, name = "Edit Mode - Income")
@Composable
fun TransactionScreenEditIncomePreview() {
    BluiTheme {
        TransactionScreen(isEditMode = true, initialTransactionType = "Income")
    }
}