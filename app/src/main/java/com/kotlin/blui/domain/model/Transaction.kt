package com.kotlin.blui.domain.model

data class Transaction(
    val id: String,
    val userId: String,
    val type: String,
    val name: String,
    val categoryId: String,
    val amount: Double,
    val date: String,
    val note: String?,
    val category: Category? = null
)
