package com.kotlin.blui.data.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * ApiConfig - Singleton object untuk konfigurasi Retrofit
 * Menyediakan instance ApiService yang siap digunakan
 */
object ApiConfig {

    // TODO: Ganti dengan URL backend Anda
    private const val BASE_URL = "https://your-api-url.com/api/"

    // Timeout configuration
    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L

    /**
     * Logging interceptor sederhana untuk debugging
     */
    private class LoggingInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()

            // Log request
            println("API Request: ${request.method} ${request.url}")

            val response = chain.proceed(request)

            // Log response
            println("API Response: ${response.code} ${request.url}")

            return response
        }
    }

    /**
     * Membuat OkHttpClient dengan interceptor untuk logging dan authentication
     */
    private fun provideOkHttpClient(tokenManager: TokenManager): OkHttpClient {
        // Auth interceptor untuk menambahkan JWT token
        val authInterceptor = AuthInterceptor(tokenManager)

        // Logging interceptor untuk debugging
        val loggingInterceptor = LoggingInterceptor()

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Membuat instance Retrofit
     */
    private fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Membuat instance ApiService
     * Dipanggil dari Repository atau ViewModel
     *
     * @param context Context untuk TokenManager
     * @return ApiService yang siap digunakan
     */
    fun getApiService(context: Context): ApiService {
        val tokenManager = TokenManager(context)
        val okHttpClient = provideOkHttpClient(tokenManager)
        val retrofit = provideRetrofit(okHttpClient)
        return retrofit.create(ApiService::class.java)
    }

    /**
     * Alternatif: Membuat ApiService dengan TokenManager yang sudah ada
     * Berguna jika TokenManager sudah di-inject via DI (Dagger/Hilt)
     */
    fun getApiService(tokenManager: TokenManager): ApiService {
        val okHttpClient = provideOkHttpClient(tokenManager)
        val retrofit = provideRetrofit(okHttpClient)
        return retrofit.create(ApiService::class.java)
    }
}
