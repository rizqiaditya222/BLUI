package com.kotlin.blui.data.api.request

data class CreateTransactionRequest(
    val type: String, // "income" or "expense"
    val name: String,
    val categoryId: String,
    val amount: Double,
    val date: String,
    val note: String?
)

data class UpdateTransactionRequest(
    val name: String,
    val categoryId: String,
    val amount: Double,
    val date: String,
    val note: String?
)
