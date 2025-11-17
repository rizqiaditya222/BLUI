package com.kotlin.blui.domain.model

import com.kotlin.blui.domain.model.CategorySummary

data class MonthlySummary(
    val userId: String,
    val month: Int,
    val year: Int,
    val balance: Double,
    val totalIncome: Double,
    val totalExpense: Double,
    val incomeByCategory: List<CategorySummary>?,
    val expenseByCategory: List<CategorySummary>?
)
