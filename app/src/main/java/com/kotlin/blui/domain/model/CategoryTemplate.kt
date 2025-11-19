package com.kotlin.blui.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Sports
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryTemplate(
    val name: String,
    val icon: ImageVector
)

object CategoryTemplates {
    val templates = listOf(
        CategoryTemplate("Makanan", Icons.Default.Restaurant),
        CategoryTemplate("Transport", Icons.Default.DirectionsCar),
        CategoryTemplate("Belanja", Icons.Default.ShoppingCart),
        CategoryTemplate("Rumah", Icons.Default.Home),
        CategoryTemplate("Kesehatan", Icons.Default.LocalHospital),
        CategoryTemplate("Pendidikan", Icons.Default.School),
        CategoryTemplate("Olahraga", Icons.Default.Sports),
        CategoryTemplate("Listrik", Icons.Default.ElectricalServices),
        CategoryTemplate("Pakaian", Icons.Default.Checkroom),
        CategoryTemplate("Bensin", Icons.Default.LocalGasStation)
    )
}

