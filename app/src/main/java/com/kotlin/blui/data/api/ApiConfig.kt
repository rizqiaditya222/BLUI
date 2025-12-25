package com.kotlin.blui.data.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {

    private const val BASE_URL = "http://159.223.67.39:3000/"

    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L

    private class LoggingInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()

            println("API Request: ${request.method} ${request.url}")

            val headers = request.headers
            val headersLog = headers.names().joinToString(", ") { name ->
                val value = if (name.equals("Authorization", ignoreCase = true)) {
                    val raw = headers[name] ?: ""
                    if (raw.length > 12) raw.substring(0, 12) + "..." else raw
                } else {
                    headers[name]
                }
                "$name: $value"
            }
            println("Request Headers: $headersLog")

            request.body?.let { body ->
                val buffer = okio.Buffer()
                body.writeTo(buffer)
                println("Request Body: ${buffer.readUtf8()}")
            }

            val response = chain.proceed(request)

            println("API Response: ${response.code} ${request.url}")

            if (!response.isSuccessful) {
                val responseBody = response.peekBody(Long.MAX_VALUE)
                println("Error Response Body: ${responseBody.string()}")
            }

            return response
        }
    }

    private fun provideOkHttpClient(tokenManager: TokenManager): OkHttpClient {
        val authInterceptor = AuthInterceptor(tokenManager)

        val loggingInterceptor = LoggingInterceptor()

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }


    private fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * @param context Context untuk TokenManager
     * @return
     */
    fun getApiService(context: Context): ApiService {
        val tokenManager = TokenManager.getInstance(context)
        val okHttpClient = provideOkHttpClient(tokenManager)
        val retrofit = provideRetrofit(okHttpClient)
        return retrofit.create(ApiService::class.java)
    }

    fun getApiService(tokenManager: TokenManager): ApiService {
        val okHttpClient = provideOkHttpClient(tokenManager)
        val retrofit = provideRetrofit(okHttpClient)
        return retrofit.create(ApiService::class.java)
    }
}
