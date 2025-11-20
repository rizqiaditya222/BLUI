package com.kotlin.blui.data.api.response

import com.google.gson.annotations.SerializedName

data class TransactionResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("userId")
    val userId: String, // ID user pemilik transaksi
    @SerializedName("type")
    val type: String, // "income" or "expense"
    @SerializedName("name")
    val name: String,
    @SerializedName("categoryId")
    val categoryId: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("date")
    val date: String, // Format: "YYYY-MM-DD"
    @SerializedName("note")
    val note: String?,
    @SerializedName("category")
    val category: CategoryResponse? // Data kategori lengkap (nama, icon, warna)
)

data class TransactionsListResponse(
    @SerializedName("transactions")
    val transactions: List<TransactionResponse>
)

// Response untuk transaksi yang di-group berdasarkan tanggal
data class TransactionsByDateResponse(
    @SerializedName("date")
    val date: String, // Format: "YYYY-MM-DD"
    @SerializedName("transactions")
    val transactions: List<TransactionResponse>,
    @SerializedName("totalIncome")
    val totalIncome: Double,
    @SerializedName("totalExpense")
    val totalExpense: Double
)

data class GroupedTransactionsResponse(
    @SerializedName("groups")
    val groups: List<TransactionsByDateResponse>
)
