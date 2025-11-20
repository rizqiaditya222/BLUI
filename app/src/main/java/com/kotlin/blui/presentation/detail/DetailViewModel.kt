package com.kotlin.blui.presentation.detail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.blui.data.repository.TransactionRepository
import com.kotlin.blui.domain.model.TransactionsByDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class DetailUiState(
    val transactionGroups: List<TransactionsByDate> = emptyList(),
    val selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH),
    val selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val selectedType: String = "All", // "All", "Expense", "Income"
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class DetailViewModel(private val context: Context) : ViewModel() {
    private val transactionRepository = TransactionRepository(context)

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            // Normalize month (Calendar is 0-based). Ensure within 0..11 before +1
            val rawMonth = _uiState.value.selectedMonth
            val normalizedMonthIndex = rawMonth.coerceIn(0, 11)
            val month1Based = normalizedMonthIndex + 1
            val year = _uiState.value.selectedYear

            println("DetailViewModel - Loading transactions (safe)...")
            println("  Raw Month Index: $rawMonth -> Normalized: $normalizedMonthIndex -> 1-based: $month1Based")
            println("  Year: $year")
            println("  Type Filter: ${_uiState.value.selectedType}")

            val result = transactionRepository.getGroupedTransactionsSafe(
                month = month1Based,
                year = year
            )

            result.onSuccess { groups ->
                println("DetailViewModel - Safe groups loaded: ${groups.size} groups")

                // Filter by type if not "All"
                val filteredGroups = if (_uiState.value.selectedType == "All") {
                    groups
                } else {
                    groups.map { group ->
                        group.copy(
                            transactions = group.transactions.filter { tx ->
                                tx.type.equals(_uiState.value.selectedType, ignoreCase = true)
                            }
                        )
                    }.filter { it.transactions.isNotEmpty() }
                }

                _uiState.value = _uiState.value.copy(
                    transactionGroups = filteredGroups,
                    isLoading = false
                )
            }.onFailure { exception ->
                println("DetailViewModel - Failed to load transactions (safe): ${exception.message}")
                exception.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    transactionGroups = emptyList(),
                    isLoading = false,
                    errorMessage = exception.message ?: "Gagal memuat transaksi"
                )
            }
        }
    }

    fun onMonthYearChange(month: Int, year: Int) {
        _uiState.value = _uiState.value.copy(
            selectedMonth = month,
            selectedYear = year
        )
        loadTransactions()
    }

    fun onTypeChange(type: String) {
        _uiState.value = _uiState.value.copy(selectedType = type)
        loadTransactions()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
