package com.kotlin.blui.domain.model

data class CategorySummary(
    val categoryId: String,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: String,
    val total: Double,
    val percentage: Double
)

