package com.kotlin.blui.presentation.transaction

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.blui.data.repository.CategoryRepository
import com.kotlin.blui.data.repository.TransactionRepository
import com.kotlin.blui.domain.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class TransactionUiState(
    val transactionId: String? = null,
    val transactionType: String = "Expense",
    val transactionName: String = "",
    val selectedCategory: Category? = null,
    val amount: String = "",
    val date: String = "",
    val note: String = "",
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val transactionMonth: Int? = null,
    val transactionYear: Int? = null
)

class TransactionViewModel(private val context: Context) : ViewModel() {
    private val transactionRepository = TransactionRepository(context)
    private val categoryRepository = CategoryRepository(context)

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    init {
        // Set default date to today
        val today = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        _uiState.value = _uiState.value.copy(date = dateFormat.format(today.time))

        // Load categories
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            println("TransactionViewModel - Loading categories...")

            val result = categoryRepository.getCategories()
            result.onSuccess { categories ->
                println("TransactionViewModel - Categories loaded: ${categories.size} items")
                _uiState.value = _uiState.value.copy(
                    categories = categories,
                    isLoading = false
                )
            }.onFailure { exception ->
                println("TransactionViewModel - Failed to load categories: ${exception.message}")
                _uiState.value = _uiState.value.copy(
                    categories = emptyList(),
                    isLoading = false,
                    errorMessage = "Gagal memuat kategori: ${exception.message}"
                )
            }
        }
    }

    fun onTransactionTypeChange(type: String) {
        _uiState.value = _uiState.value.copy(transactionType = type)
    }

    fun onTransactionNameChange(name: String) {
        _uiState.value = _uiState.value.copy(transactionName = name, errorMessage = null)
    }

    fun onCategorySelect(category: Category) {
        _uiState.value = _uiState.value.copy(selectedCategory = category, errorMessage = null)
    }

    fun onAmountChange(amount: String) {
        // Only allow numbers and one decimal point
        val filtered = amount.filter { it.isDigit() || it == '.' }
        if (filtered.count { it == '.' } <= 1) {
            _uiState.value = _uiState.value.copy(amount = filtered, errorMessage = null)
        }
    }

    fun onDateChange(date: String) {
        _uiState.value = _uiState.value.copy(date = date, errorMessage = null)
    }

    fun onNoteChange(note: String) {
        _uiState.value = _uiState.value.copy(note = note)
    }

    fun onDeleteCategory(categoryId: String) {
        viewModelScope.launch {
            println("TransactionViewModel - Deleting category: $categoryId")

            val result = categoryRepository.deleteCategory(categoryId)

            result.onSuccess {
                println("TransactionViewModel - Category deleted successfully")
                // Immediately remove from local list for instant UI update
                val updatedCategories = _uiState.value.categories.filter { it.id != categoryId }
                _uiState.value = _uiState.value.copy(
                    categories = updatedCategories,
                    // Clear selected category if it was deleted
                    selectedCategory = if (_uiState.value.selectedCategory?.id == categoryId) {
                        null
                    } else {
                        _uiState.value.selectedCategory
                    }
                )
                println("TransactionViewModel - Categories updated, new count: ${updatedCategories.size}")
            }.onFailure { exception ->
                println("TransactionViewModel - Failed to delete category: ${exception.message}")
                exception.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    errorMessage = exception.message ?: "Gagal menghapus kategori"
                )
            }
        }
    }

    fun saveTransaction(onSuccess: () -> Unit) {
        val transactionId = _uiState.value.transactionId
        val type = _uiState.value.transactionType.lowercase()
        val name = _uiState.value.transactionName.trim()
        val category = _uiState.value.selectedCategory
        val amountStr = _uiState.value.amount.trim()
        val date = _uiState.value.date
        val note = _uiState.value.note.trim().ifBlank { null }

        // Validation
        if (!validateInput(name, category, amountStr, date)) {
            return
        }

        val amount = amountStr.toDoubleOrNull() ?: 0.0

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = if (transactionId != null) {
                // Update existing transaction
                transactionRepository.updateTransaction(
                    id = transactionId,
                    name = name,
                    categoryId = category!!.id,
                    amount = amount,
                    date = date,
                    note = note
                )
            } else {
                // Create new transaction
                transactionRepository.createTransaction(
                    type = type,
                    name = name,
                    categoryId = category!!.id,
                    amount = amount,
                    date = date,
                    note = note
                )
            }

            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    errorMessage = null
                )
                onSuccess()
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = false,
                    errorMessage = exception.message ?: "Gagal menyimpan transaksi"
                )
            }
        }
    }

    private fun validateInput(
        name: String,
        category: Category?,
        amount: String,
        date: String
    ): Boolean {
        return when {
            name.isEmpty() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Nama transaksi tidak boleh kosong")
                false
            }
            category == null -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Pilih kategori terlebih dahulu")
                false
            }
            amount.isEmpty() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Jumlah tidak boleh kosong")
                false
            }
            amount.toDoubleOrNull() == null || amount.toDouble() <= 0 -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Jumlah harus lebih dari 0")
                false
            }
            date.isEmpty() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Tanggal tidak boleh kosong")
                false
            }
            else -> true
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun loadTransaction(transactionId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = transactionRepository.getTransactionById(transactionId)
            result.onSuccess { transaction ->
                // Extract month and year from date (format: yyyy-MM-dd)
                val (month, year) = try {
                    val parts = transaction.date.split('-')
                    val y = parts.getOrNull(0)?.toIntOrNull()
                    val m = parts.getOrNull(1)?.toIntOrNull()
                    // Convert to 0-based month for Calendar
                    Pair(if (m != null) m - 1 else null, y)
                } catch (e: Exception) {
                    Pair(null, null)
                }

                _uiState.value = _uiState.value.copy(
                    transactionId = transaction.id,
                    transactionType = transaction.type,
                    transactionName = transaction.name,
                    selectedCategory = transaction.category?.let { cat ->
                        // Find the category in the local list or keep it as is
                        _uiState.value.categories.find { it.id == cat.id } ?: cat
                    },
                    amount = transaction.amount.toString(),
                    date = transaction.date,
                    note = transaction.note ?: "",
                    transactionMonth = month,
                    transactionYear = year,
                    isLoading = false
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Gagal memuat transaksi: ${exception.message}"
                )
            }
        }
    }

    fun deleteTransaction(onSuccess: () -> Unit) {
        val transactionId = _uiState.value.transactionId
        if (transactionId == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "ID transaksi tidak ditemukan")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = transactionRepository.deleteTransaction(transactionId)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    errorMessage = null
                )
                onSuccess()
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Gagal menghapus transaksi"
                )
            }
        }
    }
}