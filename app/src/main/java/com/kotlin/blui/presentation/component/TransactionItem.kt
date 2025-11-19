package com.kotlin.blui.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotlin.blui.ui.theme.BluiTheme

@Composable
fun TransactionItem(
    categoryIcon: ImageVector,
    categoryColor: Color,
    transactionName: String,
    amount: String,
    modifier: Modifier = Modifier,
    circleSize: Dp = 24.dp,
    iconSize: Dp = 12.dp,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryIcon(
                icon = categoryIcon,
                color = categoryColor,
                circleSize = circleSize,
                iconSize = iconSize
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = transactionName,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            )
        }
        Text(
            text = amount,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionItemPreview() {
    BluiTheme {
        TransactionItem(
            categoryIcon = Icons.Default.Restaurant,
            categoryColor = Color(0xFFFF6B6B),
            transactionName = "Geprek Sambal Bawang",
            amount = "Rp 20.000"
        )
    }
}
