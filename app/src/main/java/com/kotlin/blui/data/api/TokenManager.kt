package com.kotlin.blui.data.api

import android.content.Context
import android.content.SharedPreferences

/**
 * TokenManager untuk menyimpan dan mengambil JWT token
 * Menggunakan SharedPreferences untuk persistent storage
 */
class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME = "blui_prefs"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
    }

    /**
     * Simpan token JWT setelah login/register
     */
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    /**
     * Ambil token JWT untuk Authorization header
     */
    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    /**
     * Simpan user ID (opsional, untuk keperluan UI)
     */
    fun saveUserId(userId: String) {
        prefs.edit().putString(KEY_USER_ID, userId).apply()
    }

    /**
     * Ambil user ID
     */
    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    /**
     * Hapus semua data saat logout
     */
    fun clearToken() {
        prefs.edit().clear().apply()
    }

    /**
     * Cek apakah user sudah login
     */
    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}

