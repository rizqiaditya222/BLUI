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
    val userId: String, // ID user pemilik summary
    val month: Int, // 1-12
    val year: Int,
    val balance: Double,
    val totalIncome: Double,
    val totalExpense: Double,
    val incomeByCategory: List<CategorySummary>?, // Breakdown income per kategori
    val expenseByCategory: List<CategorySummary>? // Breakdown expense per kategori (untuk pie chart)
)

// Response untuk mendapatkan riwayat beberapa bulan
data class MonthlySummaryListResponse(
    val summaries: List<BalanceSummaryResponse>
)
