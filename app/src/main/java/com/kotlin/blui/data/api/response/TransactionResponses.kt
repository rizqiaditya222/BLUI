package com.kotlin.blui.data.api.response

import com.google.gson.annotations.SerializedName

data class TransactionResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("userId")
    val userId: String,
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
    val category: CategoryResponse?
)

data class TransactionsListResponse(
    @SerializedName("transactions")
    val transactions: List<TransactionResponse>
)

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
