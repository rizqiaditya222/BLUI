package com.kotlin.blui.data.api

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = tokenManager.getToken()

        val newRequest = if (token.isNullOrEmpty()) {
            println("AuthInterceptor: no token found, request will be sent without Authorization header")
            originalRequest
        } else {
            val masked = if (token.length > 8) token.substring(0, 8) + "..." else token
            println("AuthInterceptor: token found (masked): $masked")
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        }

        return chain.proceed(newRequest)
    }
}
