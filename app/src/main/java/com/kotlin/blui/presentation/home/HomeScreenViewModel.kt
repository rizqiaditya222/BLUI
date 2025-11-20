package com.kotlin.blui.presentation.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.blui.data.repository.SummaryRepository
import com.kotlin.blui.domain.model.MonthlySummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class HomeScreenUiState(
    val currentSummary: MonthlySummary? = null,
    val selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1, // 1-12
    val selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class HomeScreenViewModel(private val context: Context) : ViewModel() {
    private val summaryRepository = SummaryRepository(context)

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()

    init {
        loadCurrentSummary()
    }

    /**
     * Load summary for the currently selected month and year
     */
    fun loadCurrentSummary() {
        val state = _uiState.value
        loadSummary(state.selectedMonth, state.selectedYear)
    }

    /**
     * Load summary for a specific month and year
     */
    private fun loadSummary(month: Int, year: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            println("HomeScreenViewModel - Loading summary for month: $month, year: $year")

            val result = summaryRepository.getSummary(month, year)
            result.onSuccess { summary ->
                println("HomeScreenViewModel - Summary loaded successfully")
                println("  Balance: ${summary.balance}")
                println("  Total Income: ${summary.totalIncome}")
                println("  Total Expense: ${summary.totalExpense}")
                println("  Expense Categories: ${summary.expenseByCategory?.size ?: 0}")

                _uiState.value = _uiState.value.copy(
                    currentSummary = summary,
                    isLoading = false
                )
            }.onFailure { exception ->
                println("HomeScreenViewModel - Failed to load summary: ${exception.message}")
                _uiState.value = _uiState.value.copy(
                    currentSummary = null,
                    isLoading = false,
                    errorMessage = "Gagal memuat ringkasan: ${exception.message}"
                )
            }
        }
    }

    /**
     * Change the selected month and reload summary
     */
    fun onMonthChange(month: Int) {
        _uiState.value = _uiState.value.copy(selectedMonth = month)
        loadSummary(month, _uiState.value.selectedYear)
    }

    /**
     * Change the selected year and reload summary
     */
    fun onYearChange(year: Int) {
        _uiState.value = _uiState.value.copy(selectedYear = year)
        loadSummary(_uiState.value.selectedMonth, year)
    }

    /**
     * Navigate to previous month
     */
    fun previousMonth() {
        val state = _uiState.value
        var newMonth = state.selectedMonth - 1
        var newYear = state.selectedYear

        if (newMonth < 1) {
            newMonth = 12
            newYear -= 1
        }

        _uiState.value = _uiState.value.copy(
            selectedMonth = newMonth,
            selectedYear = newYear
        )
        loadSummary(newMonth, newYear)
    }

    /**
     * Navigate to next month
     */
    fun nextMonth() {
        val state = _uiState.value
        var newMonth = state.selectedMonth + 1
        var newYear = state.selectedYear

        if (newMonth > 12) {
            newMonth = 1
            newYear += 1
        }

        _uiState.value = _uiState.value.copy(
            selectedMonth = newMonth,
            selectedYear = newYear
        )
        loadSummary(newMonth, newYear)
    }

    /**
     * Refresh the current summary
     */
    fun refresh() {
        loadCurrentSummary()
    }
}