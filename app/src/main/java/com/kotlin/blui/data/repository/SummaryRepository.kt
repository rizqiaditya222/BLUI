package com.kotlin.blui.data.repository

import android.content.Context
import com.kotlin.blui.data.api.ApiConfig
import com.kotlin.blui.data.api.ApiService
import com.kotlin.blui.data.api.response.BalanceSummaryResponse
import com.kotlin.blui.data.api.response.CategorySummary
import com.kotlin.blui.domain.model.MonthlySummary

class SummaryRepository(context: Context) {
    private val apiService: ApiService = ApiConfig.getApiService(context)

    /**
     * Get summary for a specific month and year
     */
    suspend fun getSummary(month: Int, year: Int): Result<MonthlySummary> {
        return try {
            val response = apiService.getSummary(month, year)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            println("Get Summary Error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Get summary history for multiple months
     */
    suspend fun getSummaryHistory(
        startMonth: Int? = null,
        startYear: Int? = null,
        endMonth: Int? = null,
        endYear: Int? = null
    ): Result<List<MonthlySummary>> {
        return try {
            val response = apiService.getSummaryHistory(startMonth, startYear, endMonth, endYear)
            val summaries = response.summaries.map { it.toDomain() }
            Result.success(summaries)
        } catch (e: Exception) {
            println("Get Summary History Error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // Extension function to convert BalanceSummaryResponse to Domain Model
    private fun BalanceSummaryResponse.toDomain(): MonthlySummary {
        return MonthlySummary(
            userId = userId,
            month = month,
            year = year,
            balance = balance,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            incomeByCategory = incomeByCategory?.map { it.toDomain() },
            expenseByCategory = expenseByCategory?.map { it.toDomain() }
        )
    }

    // Extension function to convert CategorySummary response to Domain Model
    private fun CategorySummary.toDomain(): com.kotlin.blui.domain.model.CategorySummary {
        return com.kotlin.blui.domain.model.CategorySummary(
            categoryId = categoryId,
            categoryName = categoryName,
            categoryIcon = categoryIcon,
            categoryColor = categoryColor,
            total = total,
            percentage = percentage
        )
    }
}
