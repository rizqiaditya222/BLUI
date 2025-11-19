package com.kotlin.blui.presentation.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotlin.blui.presentation.component.CategoryIcon
import com.kotlin.blui.ui.theme.BlueLight
import com.kotlin.blui.ui.theme.BluiTheme

data class CategoryItem(
    val name: String,
    val icon: ImageVector
)

@Composable
fun Category(
    onDismiss: () -> Unit = {},
    onCategorySelected: (CategoryItem) -> Unit = {}
) {
    // Dummy data
    val categories = listOf(
        CategoryItem("Makan", Icons.Default.Restaurant),
        CategoryItem("Transport", Icons.Default.DirectionsCar),
        CategoryItem("Edukasi", Icons.Default.ShoppingCart),
        CategoryItem("Elektronik", Icons.Default.Home),
        CategoryItem("Perawatan", Icons.Default.Restaurant)
    )

    // Dark overlay background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        // Dialog box
        Box(
            modifier = Modifier
                .width(320.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .clickable(enabled = false) {}
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Pilih Kategori",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Grid Category Buttons
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.height(200.dp)
                ) {
                    items(categories) { category ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                onCategorySelected(category)
                            }
                        ) {
                            CategoryIcon(
                                icon = category.icon,
                                color = Color.Gray, // Default color, can be changed
                                circleSize = 48.dp,
                                iconSize = 24.dp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = category.name,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }

                    // Add button
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { /* TODO: Add new category */ }
                        ) {
                            CategoryIcon(
                                icon = Icons.Default.Add,
                                color = BlueLight,
                                circleSize = 48.dp,
                                iconSize = 24.dp,
                                iconTint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Remove button
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { /* TODO: Remove category */ }
                        ) {
                            CategoryIcon(
                                icon = Icons.Default.Remove,
                                color = BlueLight,
                                circleSize = 48.dp,
                                iconSize = 24.dp,
                                iconTint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CategoryPreview() {
    BluiTheme {
        Category()
    }
}
