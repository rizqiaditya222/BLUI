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
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_DOB = "user_dob"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    /**
     * Simpan token JWT setelah login/register
     */
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, true).apply()
    }

    /**
     * Ambil token JWT untuk Authorization header
     */
    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    /**
     * Simpan user ID
     */
    fun saveUserId(userId: String) {
        prefs.edit().putString(KEY_USER_ID, userId).apply()
    }

    /**
     * Simpan user data setelah login/register
     */
    fun saveUserData(id: String, name: String, email: String, dateOfBirth: String?) {
        prefs.edit().apply {
            putString(KEY_USER_ID, id)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_DOB, dateOfBirth)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    /**
     * Ambil user ID
     */
    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)

    /**
     * Ambil user name
     */
    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)

    /**
     * Ambil user email
     */
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)

    /**
     * Ambil user date of birth
     */
    fun getUserDateOfBirth(): String? = prefs.getString(KEY_USER_DOB, null)

    /**
     * Cek apakah user sudah login
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) && !getToken().isNullOrEmpty()
    }

    /**
     * Hapus token saat logout
     */
    fun clearToken() {
        prefs.edit().clear().apply()
    }

    /**
     * Hapus semua data saat logout
     */
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
