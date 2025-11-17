package com.kotlin.blui.domain.model

// Model untuk breakdown summary per kategori (digunakan di pie chart)
data class CategorySummary(
    val categoryId: String,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: String,
    val total: Double,
    val percentage: Double
)

