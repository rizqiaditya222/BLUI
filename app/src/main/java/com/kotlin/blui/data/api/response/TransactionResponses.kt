package com.kotlin.blui.data.api.response

data class TransactionResponse(
    val id: String,
    val userId: String, // ID user pemilik transaksi
    val type: String, // "income" or "expense"
    val name: String,
    val categoryId: String,
    val amount: Double,
    val date: String, // Format: "YYYY-MM-DD"
    val note: String?,
    val category: CategoryResponse? // Data kategori lengkap (nama, icon, warna)
)

data class TransactionsListResponse(
    val transactions: List<TransactionResponse>
)

// Response untuk transaksi yang di-group berdasarkan tanggal
data class TransactionsByDateResponse(
    val date: String, // Format: "YYYY-MM-DD"
    val transactions: List<TransactionResponse>,
    val totalIncome: Double,
    val totalExpense: Double
)

data class GroupedTransactionsResponse(
    val groups: List<TransactionsByDateResponse>
)
