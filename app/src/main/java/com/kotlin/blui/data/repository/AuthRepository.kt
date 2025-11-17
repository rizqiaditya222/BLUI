package com.kotlin.blui.data.repository

import android.content.Context
import com.kotlin.blui.data.api.ApiConfig
import com.kotlin.blui.data.api.ApiService
import com.kotlin.blui.data.api.TokenManager
import com.kotlin.blui.data.api.request.LoginRequest
import com.kotlin.blui.data.api.request.RegisterRequest
import com.kotlin.blui.data.api.request.UpdateProfileRequest
import com.kotlin.blui.data.api.response.AuthResponse
import com.kotlin.blui.data.api.response.UserResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * AuthRepository - Handle autentikasi dan profil user
 *
 * Contoh penggunaan:
 * ```
 * val authRepository = AuthRepository(context)
 * val result = authRepository.login("email@example.com", "password")
 * ```
 */
class AuthRepository(private val context: Context) {

    private val apiService: ApiService = ApiConfig.getApiService(context)
    private val tokenManager: TokenManager = TokenManager(context)

    /**
     * Register user baru
     * Setelah sukses, token otomatis disimpan
     */
    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        dateOfBirth: String
    ): Result<AuthResponse> {
        return try {
            val request = RegisterRequest(fullName, email, password, dateOfBirth)
            val response = apiService.register(request)

            // Simpan token dan userId setelah register sukses
            tokenManager.saveToken(response.token)
            tokenManager.saveUserId(response.user.id)

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Login user
     * Setelah sukses, token otomatis disimpan
     */
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val request = LoginRequest(email, password)
            val response = apiService.login(request)

            // Simpan token dan userId setelah login sukses
            tokenManager.saveToken(response.token)
            tokenManager.saveUserId(response.user.id)

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Logout user
     * Hapus token dan semua data user dari local storage
     */
    fun logout() {
        tokenManager.clearToken()
    }

    /**
     * Cek apakah user sudah login
     */
    fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }

    /**
     * Get profil user saat ini
     * Memerlukan token (user harus sudah login)
     */
    suspend fun getProfile(): Result<UserResponse> {
        return try {
            val response = apiService.getProfile()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update profil user
     */
    suspend fun updateProfile(
        fullName: String?,
        dateOfBirth: String?,
        photoUrl: String?
    ): Result<UserResponse> {
        return try {
            val request = UpdateProfileRequest(fullName, dateOfBirth, photoUrl)
            val response = apiService.updateProfile(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Upload foto profil
     * @param file File gambar yang akan di-upload
     */
    suspend fun uploadPhoto(file: File): Result<UserResponse> {
        return try {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val photoPart = MultipartBody.Part.createFormData("photo", file.name, requestFile)

            val response = apiService.uploadPhoto(photoPart)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}