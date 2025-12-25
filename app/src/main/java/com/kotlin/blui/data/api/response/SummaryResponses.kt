package com.kotlin.blui.data.api.response

// Breakdown summary per kategori untuk chart
data class CategorySummary(
    val categoryId: String,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: String,
    val total: Double,
    val percentage: Double
)

data class BalanceSummaryResponse(
    val userId: String,
    val month: Int,
    val year: Int,
    val balance: Double,
    val totalIncome: Double,
    val totalExpense: Double,
    val incomeByCategory: List<CategorySummary>?,
    val expenseByCategory: List<CategorySummary>?
)

// Response untuk mendapatkan riwayat beberapa bulan
data class MonthlySummaryListResponse(
    val summaries: List<BalanceSummaryResponse>
)
