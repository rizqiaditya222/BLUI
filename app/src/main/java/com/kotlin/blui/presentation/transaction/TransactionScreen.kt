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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotlin.blui.presentation.component.CustomOutlinedTextField
import com.kotlin.blui.ui.theme.BlueLight
import com.kotlin.blui.ui.theme.BlueLightActive
import com.kotlin.blui.ui.theme.BluiTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    initialTransactionType: String = "Expense",
    onBack: () -> Unit = {},
    onSave: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    var transactionType by remember { mutableStateOf(initialTransactionType) }
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // Dynamic title based on mode and transaction type
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = BlueLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (transactionType == "Expense") {
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.primary,
                                                BlueLightActive
                                            )
                                        )
                                    } else {
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Transparent
                                            )
                                        )
                                    }
                                )
                                .clickable { transactionType = "Expense" },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Expense ↑",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (transactionType == "Expense") Color.White else MaterialTheme.colorScheme.primary
                            )
                        }

                        // Income Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    if (transactionType == "Income") {
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.primary,
                                                BlueLightActive
                                            )
                                        )
                                    } else {
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Transparent
                                            )
                                        )
                                    }
                                )
                                .clickable { transactionType = "Income" },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Income ↓",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (transactionType == "Income") Color.White else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

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
                        onValueChange = { category = it },
                        placeholder = "",
                        modifier = Modifier.weight(1f)
                    )

                    // Category Icon Button
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF6B6B))
                            .clickable { /* TODO: Open category picker */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(42.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(color = BlueLight)
                            .clickable { onDelete() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Hapus",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

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
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = androidx.compose.material3.DatePickerDefaults.colors(
                        containerColor = Color.White
                    )
                )
            }
        }
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