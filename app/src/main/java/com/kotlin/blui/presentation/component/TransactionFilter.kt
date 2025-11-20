package com.kotlin.blui.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun TransactionFilter(
    selectedDate: String,
    selectedType: String,
    onDateClick: () -> Unit,
    onTypeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedTypeMenu by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Month / Year filter trigger
        Row(
            modifier = Modifier.clickable { onDateClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedDate,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Select Month"
            )
        }

        // Type filter
        Row(
            modifier = Modifier.clickable { expandedTypeMenu = true },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedType,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Select Type"
            )

            DropdownMenu(
                expanded = expandedTypeMenu,
                onDismissRequest = { expandedTypeMenu = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                DropdownMenuItem(
                    text = { Text("All") },
                    onClick = {
                        onTypeChange("All")
                        expandedTypeMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Expense") },
                    onClick = {
                        onTypeChange("Expense")
                        expandedTypeMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Income") },
                    onClick = {
                        onTypeChange("Income")
                        expandedTypeMenu = false
                    }
                )
            }
        }
    }
}
