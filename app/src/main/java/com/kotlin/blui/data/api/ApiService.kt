package com.kotlin.blui.data.api

import com.kotlin.blui.data.api.request.CreateCategoryRequest
import com.kotlin.blui.data.api.request.CreateTransactionRequest
import com.kotlin.blui.data.api.request.LoginRequest
import com.kotlin.blui.data.api.request.RegisterRequest
import com.kotlin.blui.data.api.request.UpdateTransactionRequest
import com.kotlin.blui.data.api.request.UpdateProfileRequest
import com.kotlin.blui.data.api.response.AuthResponse
import com.kotlin.blui.data.api.response.BalanceSummaryResponse
import com.kotlin.blui.data.api.response.CategoryResponse
import com.kotlin.blui.data.api.response.CategoriesListResponse
import com.kotlin.blui.data.api.response.TransactionResponse
import com.kotlin.blui.data.api.response.TransactionsListResponse
import com.kotlin.blui.data.api.response.UserResponse
import com.kotlin.blui.data.api.response.MonthlySummaryListResponse
import com.kotlin.blui.data.api.response.GroupedTransactionsResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

    // Auth
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    // Profile
    @GET("user/profile")
    suspend fun getProfile(): UserResponse

    @PUT("user/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): UserResponse

    @Multipart
    @POST("user/photo")
    suspend fun uploadPhoto(@Part photo: MultipartBody.Part): UserResponse

    // Categories
    @GET("categories")
    suspend fun getCategories(): CategoriesListResponse

    @POST("categories")
    suspend fun createCategory(@Body request: CreateCategoryRequest): CategoryResponse

    @DELETE("categories/{id}")
    suspend fun deleteCategory(@Path("id") id: String)

    // Transactions
    @GET("transactions")
    suspend fun getTransactions(
        @Query("month") month: Int?,
        @Query("year") year: Int?,
        @Query("date") date: String?, // Format: "YYYY-MM-DD" untuk filter tanggal spesifik
        @Query("startDate") startDate: String?, // Format: "YYYY-MM-DD"
        @Query("endDate") endDate: String? // Format: "YYYY-MM-DD"
    ): TransactionsListResponse

    // Mendapatkan transaksi yang di-group berdasarkan tanggal
    @GET("transactions/grouped")
    suspend fun getGroupedTransactions(
        @Query("month") month: Int?,
        @Query("year") year: Int?,
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?
    ): GroupedTransactionsResponse

    // Get single transaction by ID
    @GET("transactions/{id}")
    suspend fun getTransactionById(@Path("id") id: String): TransactionResponse

    @POST("transactions")
    suspend fun createTransaction(@Body request: CreateTransactionRequest): TransactionResponse

    @PUT("transactions/{id}")
    suspend fun updateTransaction(@Path("id") id: String, @Body request: UpdateTransactionRequest): TransactionResponse

    @DELETE("transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: String)

    // Summary / Balance
    @GET("summary")
    suspend fun getSummary(
        @Query("month") month: Int,
        @Query("year") year: Int
    ): BalanceSummaryResponse

    // Mendapatkan riwayat summary beberapa bulan
    @GET("summary/history")
    suspend fun getSummaryHistory(
        @Query("startMonth") startMonth: Int?,
        @Query("startYear") startYear: Int?,
        @Query("endMonth") endMonth: Int?,
        @Query("endYear") endYear: Int?
    ): MonthlySummaryListResponse
}