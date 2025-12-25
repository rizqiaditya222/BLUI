package com.kotlin.blui.data.repository

import android.content.Context
import com.kotlin.blui.data.api.ApiConfig
import com.kotlin.blui.data.api.ApiService
import com.kotlin.blui.data.api.request.CreateTransactionRequest
import com.kotlin.blui.data.api.request.UpdateTransactionRequest
import com.kotlin.blui.data.api.response.TransactionResponse
import com.kotlin.blui.domain.model.Category
import com.kotlin.blui.domain.model.Transaction
import com.kotlin.blui.domain.model.TransactionsByDate

class TransactionRepository(context: Context) {
    private val apiService: ApiService = ApiConfig.getApiService(context)

    suspend fun getTransactions(
        month: Int? = null,
        year: Int? = null,
        date: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): Result<List<Transaction>> {
        return try {
            val response = apiService.getTransactions(month, year, date, startDate, endDate)
            val transactions = response.transactions.map { it.toDomain() }
            Result.success(transactions)
        } catch (e: Exception) {
            println("Get Transactions Error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getGroupedTransactions(
        month: Int? = null,
        year: Int? = null,
        startDate: String? = null,
        endDate: String? = null
    ): Result<List<TransactionsByDate>> {
        return try {
            val response = apiService.getGroupedTransactions(month, year, startDate, endDate)
            val grouped = response.groups.map { group ->
                TransactionsByDate(
                    date = group.date,
                    transactions = group.transactions.map { it.toDomain() },
                    totalIncome = group.totalIncome,
                    totalExpense = group.totalExpense
                )
            }
            Result.success(grouped)
        } catch (e: Exception) {
            println("Get Grouped Transactions Error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getTransactionById(id: String): Result<Transaction> {
        return try {
            println("Get Transaction By ID: $id")
            val response = apiService.getTransactionById(id)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            println("Get Transaction By ID Error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun createTransaction(
        type: String,
        name: String,
        categoryId: String,
        amount: Double,
        date: String,
        note: String?
    ): Result<Transaction> {
        return try {
            val request = CreateTransactionRequest(
                type = type,
                name = name,
                categoryId = categoryId,
                amount = amount,
                date = date,
                note = note
            )

            println("Create Transaction Request:")
            println("  type: $type")
            println("  name: $name")
            println("  categoryId: $categoryId")
            println("  amount: $amount")
            println("  date: $date")
            println("  note: $note")

            val response = apiService.createTransaction(request)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            println("Create Transaction Error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun updateTransaction(
        id: String,
        name: String,
        categoryId: String,
        amount: Double,
        date: String,
        note: String?
    ): Result<Transaction> {
        return try {
            val request = UpdateTransactionRequest(
                name = name,
                categoryId = categoryId,
                amount = amount,
                date = date,
                note = note
            )
            val response = apiService.updateTransaction(id, request)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            println("Update Transaction Error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun deleteTransaction(transactionId: String): Result<Unit> {
        return try {
            apiService.deleteTransaction(transactionId)
            Result.success(Unit)
        } catch (e: Exception) {
            println("Delete Transaction Error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getGroupedTransactionsSafe(
        month: Int? = null,
        year: Int? = null
    ): Result<List<TransactionsByDate>> {
        fun localGroup(transactions: List<Transaction>): List<TransactionsByDate> = transactions
            .groupBy { it.date }
            .map { (date, txs) ->
                val totalIncome = txs.filter { it.type.equals("income", ignoreCase = true) }.sumOf { it.amount }
                val totalExpense = txs.filter { it.type.equals("expense", ignoreCase = true) }.sumOf { it.amount }
                TransactionsByDate(
                    date = date,
                    transactions = txs,
                    totalIncome = totalIncome,
                    totalExpense = totalExpense
                )
            }
            .sortedByDescending { it.date }

        return try {
            println("getGroupedTransactionsSafe - Attempt 1: grouped endpoint with month/year Month=$month Year=$year")
            val response1 = apiService.getGroupedTransactions(month, year, null, null)
            val grouped1 = response1.groups.map { group ->
                TransactionsByDate(
                    date = group.date,
                    transactions = group.transactions.map { it.toDomain() },
                    totalIncome = group.totalIncome,
                    totalExpense = group.totalExpense
                )
            }
            Result.success(grouped1)
        } catch (e1: Exception) {
            println("getGroupedTransactionsSafe - Attempt 1 failed: ${e1.message}")
            e1.printStackTrace()

            if (month != null && year != null) {
                try {
                    val cal = java.util.Calendar.getInstance().apply {
                        set(java.util.Calendar.YEAR, year)
                        set(java.util.Calendar.MONTH, month - 1)
                        set(java.util.Calendar.DAY_OF_MONTH, 1)
                    }
                    val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    val startDate = dateFormat.format(cal.time)
                    val endCal = (cal.clone() as java.util.Calendar).apply {
                        set(java.util.Calendar.DAY_OF_MONTH, getActualMaximum(java.util.Calendar.DAY_OF_MONTH))
                    }
                    val endDate = dateFormat.format(endCal.time)
                    println("getGroupedTransactionsSafe - Attempt 2: grouped endpoint with startDate/endDate $startDate .. $endDate")
                    val response2 = apiService.getGroupedTransactions(null, null, startDate, endDate)
                    val grouped2 = response2.groups.map { group ->
                        TransactionsByDate(
                            date = group.date,
                            transactions = group.transactions.map { it.toDomain() },
                            totalIncome = group.totalIncome,
                            totalExpense = group.totalExpense
                        )
                    }
                    return Result.success(grouped2)
                } catch (e2: Exception) {
                    println("getGroupedTransactionsSafe - Attempt 2 failed: ${e2.message}")
                    e2.printStackTrace()
                }
            }

            return try {
                println("getGroupedTransactionsSafe - Attempt 3: fallback flat transactions + local grouping")
                val flat = apiService.getTransactions(month, year, null, null, null)
                val transactions = flat.transactions.map { it.toDomain() }
                Result.success(localGroup(transactions))
            } catch (e3: Exception) {
                println("getGroupedTransactionsSafe - Fallback failed: ${e3.message}")
                e3.printStackTrace()
                Result.failure(e3)
            }
        }
    }

    private fun TransactionResponse.toDomain(): Transaction {
        return Transaction(
            id = id,
            userId = userId,
            type = type,
            name = name,
            categoryId = categoryId,
            amount = amount,
            date = date,
            note = note,
            category = category?.let {
                Category(
                    id = it.id,
                    userId = it.userId,
                    name = it.name,
                    icon = it.icon,
                    color = it.color
                )
            }
        )
    }
}