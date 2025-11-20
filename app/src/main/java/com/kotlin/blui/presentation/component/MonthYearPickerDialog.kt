package com.kotlin.blui.presentation.component

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun MonthYearPickerDialog(
    show: Boolean,
    initialYear: Int,
    initialMonth: Int,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    onMonthYearSelected: (month: Int, year: Int) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    if (!show) return

    var pickerYear by remember(initialYear) { mutableIntStateOf(initialYear) }
    var pickerMonthIndex by remember(initialMonth) { mutableIntStateOf(initialMonth) }

    val monthFormatter = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }
    val months = listOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )

    // Background overlay
    Surface(
        color = Color.Black.copy(alpha = 0.4f),
        modifier = Modifier
            .fillMaxSize()
            .clickable(enabled = true, onClick = onDismiss)
    ) {}

    // Dialog content
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        tonalElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 24.dp)
            .padding(top = 120.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Header tahun
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = { pickerYear -= 1 }) {
                    Text("-", fontSize = 20.sp)
                }
                Text(
                    text = pickerYear.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { pickerYear += 1 }) {
                    Text("+", fontSize = 20.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Grid bulan (3 kolom x 4 baris)
            val chunked = months.chunked(3)
            chunked.forEach { rowMonths ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowMonths.forEach { monthName ->
                        val monthIndex = months.indexOf(monthName)
                        val isSelected = monthIndex == pickerMonthIndex
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clickable {
                                    pickerMonthIndex = monthIndex
                                    onMonthYearSelected(monthIndex, pickerYear)
                                }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = monthName.take(3),
                                    color = if (isSelected) Color.White else Color.Black,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    repeat(3 - rowMonths.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Batal", color = MaterialTheme.colorScheme.primary)
                }
                TextButton(onClick = {
                    val cal = Calendar.getInstance().apply {
                        set(Calendar.YEAR, pickerYear)
                        set(Calendar.MONTH, pickerMonthIndex)
                    }
                    onConfirm(monthFormatter.format(cal.time))
                }) {
                    Text(
                        "OK",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
