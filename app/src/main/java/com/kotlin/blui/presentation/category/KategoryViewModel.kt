package com.kotlin.blui.presentation.category

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.blui.data.repository.CategoryRepository
import com.kotlin.blui.domain.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CategoryUiState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class KategoryViewModel(context: Context) : ViewModel() {
    private val categoryRepository = CategoryRepository(context)

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = categoryRepository.getCategories()

            result.onSuccess { categories ->
                _uiState.value = _uiState.value.copy(
                    categories = categories,
                    isLoading = false,
                    errorMessage = null
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    categories = emptyList(),
                    isLoading = false,
                    errorMessage = exception.message ?: "Gagal memuat kategori"
                )
            }
        }
    }

    fun createCategory(name: String, icon: String, color: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = categoryRepository.createCategory(name, icon, color)

            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = null)
                loadCategories() // Reload categories
                onSuccess()
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Gagal membuat kategori"
                )
            }
        }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = categoryRepository.deleteCategory(categoryId)

            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = null)
                loadCategories() // Reload categories
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Gagal menghapus kategori"
                )
            }
        }
    }
}