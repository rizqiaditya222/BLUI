package com.kotlin.blui.data.api

import okhttp3.Interceptor
import okhttp3.Response

/**
 * AuthInterceptor untuk menambahkan JWT token ke setiap request
 * Token diambil dari TokenManager dan ditambahkan ke Authorization header
 */
class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Ambil token dari TokenManager
        val token = tokenManager.getToken()

        // Jika tidak ada token, lanjutkan request tanpa header Authorization
        // (untuk endpoint register dan login)
        if (token.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }

        // Tambahkan Authorization header dengan Bearer token
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(newRequest)
    }
}

