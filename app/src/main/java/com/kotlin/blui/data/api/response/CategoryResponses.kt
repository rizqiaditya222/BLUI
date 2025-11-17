package com.kotlin.blui.data.api.response

data class CategoryResponse(
    val id: String,
    val userId: String, // ID user pemilik kategori
    val name: String,
    val icon: String,
    val color: String
)

data class CategoriesListResponse(
    val categories: List<CategoryResponse>
)
