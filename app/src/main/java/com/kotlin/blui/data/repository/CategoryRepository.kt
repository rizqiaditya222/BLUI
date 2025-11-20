package com.kotlin.blui.data.repository

import android.content.Context
import com.kotlin.blui.data.api.ApiConfig
import com.kotlin.blui.data.api.ApiService
import com.kotlin.blui.data.api.request.CreateCategoryRequest
import com.kotlin.blui.data.api.response.CategoryResponse
import com.kotlin.blui.domain.model.Category

class CategoryRepository(context: Context) {
    private val apiService: ApiService = ApiConfig.getApiService(context)

    /**
     * Get all categories for current user
     */
    suspend fun getCategories(): Result<List<Category>> {
        return try {
            val response = apiService.getCategories()
            val categories = response.categories.map { it.toDomain() }
            Result.success(categories)
        } catch (e: Exception) {
            println("Get Categories Error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Create new category
     */
    suspend fun createCategory(
        name: String,
        icon: String,
        color: String
    ): Result<Category> {
        return try {
            val request = CreateCategoryRequest(
                name = name,
                icon = icon,
                color = color
            )

            // Debug logging
            println("Create Category Request:")
            println("  name: $name")
            println("  icon: $icon")
            println("  color: $color")

            val response = apiService.createCategory(request)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            println("Create Category Error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Delete category by ID
     */
    suspend fun deleteCategory(categoryId: String): Result<Unit> {
        return try {
            apiService.deleteCategory(categoryId)
            Result.success(Unit)
        } catch (e: Exception) {
            println("Delete Category Error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // Extension function to convert CategoryResponse to Domain Model
    private fun CategoryResponse.toDomain(): Category {
        return Category(
            id = id,
            userId = userId,
            name = name,
            icon = icon,
            color = color
        )
    }
}