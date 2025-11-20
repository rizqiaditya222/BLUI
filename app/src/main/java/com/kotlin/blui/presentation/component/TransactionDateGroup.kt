package com.kotlin.blui.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Transaction(
    val categoryIcon: ImageVector,
    val categoryColor: Color,
    val name: String,
    val amount: String
)

@Composable
fun TransactionDateGroup(
    date: String,
    transactions: List<Transaction>,
    modifier: Modifier = Modifier,
    onTransactionClick: (Int) -> Unit = {}
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = date,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        transactions.forEachIndexed { index, transaction ->
            TransactionItem(
                categoryIcon = transaction.categoryIcon,
                categoryColor = transaction.categoryColor,
                transactionName = transaction.name,
                amount = transaction.amount,
                onClick = { onTransactionClick(index) }
            )
        }
    }
}
