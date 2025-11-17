package com.kotlin.blui.domain.model

data class TransactionsByDate(
    val date: String,
    val transactions: List<Transaction>,
    val totalIncome: Double,
    val totalExpense: Double
)

