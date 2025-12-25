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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotlin.blui.domain.model.CategoryColor
import com.kotlin.blui.domain.model.CategoryTemplate
import com.kotlin.blui.domain.model.CategoryTemplates
import com.kotlin.blui.presentation.component.CategoryIcon
import com.kotlin.blui.presentation.component.CustomOutlinedTextField
import com.kotlin.blui.presentation.component.PrimaryButton

@Composable
fun AddCategory(
    onDismiss: () -> Unit = {},
    onCategorySelected: (CategoryTemplate, Color?) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val viewModel = remember { KategoryViewModel(context) }
    val uiState by viewModel.uiState.collectAsState()

    var categoryName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<CategoryTemplate?>(null) }
    var selectedColor by remember { mutableStateOf<Color?>(null) }

    val categories = CategoryTemplates.templates
    val colors = CategoryColor.getAllColors()

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
                    "Tambah Kategori",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Nama Kategori",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomOutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    placeholder = "Berikan Nama Kategori",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Pilih Icon",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(categories) { category ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                selectedCategory = category
                            }
                        ) {
                            CategoryIcon(
                                icon = category.icon,
                                color = selectedColor ?: Color.Gray,
                                circleSize = 42.dp,
                                iconSize = 20.dp,
                                backgroundColor = if (selectedCategory == category) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    Color.LightGray
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Pilih Warna",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(colors) { color ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clickable {
                                    selectedColor = color
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            // Outer ring when selected
                            if (selectedColor == color) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                            // Inner color circle
                            Box(
                                modifier = Modifier
                                    .size(if (selectedColor == color) 24.dp else 32.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Show loading or button
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    PrimaryButton(
                        text = "Simpan",
                        onClick = {
                            if (categoryName.isNotBlank() && selectedCategory != null && selectedColor != null) {
                                // Convert icon name to string (you can customize this mapping)
                                val iconName = selectedCategory!!.name.lowercase().replace(" ", "_")

                                // Convert color to hex string (lowercase, without alpha channel)
                                val colorInt = selectedColor!!.toArgb()
                                val colorHex = String.format("#%06x", 0xFFFFFF and colorInt)

                                println("AddCategory - Creating category:")
                                println("  Category Name: $categoryName")
                                println("  Icon Name: $iconName")
                                println("  Color Hex: $colorHex")

                                viewModel.createCategory(
                                    name = categoryName,
                                    icon = iconName,
                                    color = colorHex,
                                    onSuccess = {
                                        onCategorySelected(selectedCategory!!, selectedColor)
                                        onDismiss()
                                    }
                                )
                            }
                        },
                        enabled = categoryName.isNotBlank() && selectedCategory != null && selectedColor != null
                    )
                }

                uiState.errorMessage?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
