package com.kotlin.blui.data.api.response

import com.google.gson.annotations.SerializedName

data class CategoryResponse(
    val id: String,
    val userId: String,
    val name: String,
    val icon: String,
    val color: String,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

data class CategoriesListResponse(
    val categories: List<CategoryResponse>
)
