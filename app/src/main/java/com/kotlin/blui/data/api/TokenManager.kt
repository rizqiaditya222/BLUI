package com.kotlin.blui.data.api

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences(
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

        @Volatile
        private var INSTANCE: TokenManager? = null

        fun getInstance(context: Context): TokenManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TokenManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    fun saveToken(token: String?) {
        val editor = prefs.edit()
        if (token.isNullOrEmpty()) {
            editor.remove(KEY_TOKEN)
            editor.putBoolean(KEY_IS_LOGGED_IN, false)
            println("TokenManager: saveToken called with null/empty token -> cleared")
        } else {
            editor.putString(KEY_TOKEN, token)
            editor.putBoolean(KEY_IS_LOGGED_IN, true)
            val masked = if (token.length > 12) token.substring(0, 12) + "..." else token
            println("TokenManager: saved token (masked): $masked")
        }
        editor.apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun saveUserId(userId: String) {
        prefs.edit().putString(KEY_USER_ID, userId).apply()
    }

    fun saveUserData(id: String, name: String, email: String, dateOfBirth: String?) {
        val editor = prefs.edit()
        editor.putString(KEY_USER_ID, id)
        editor.putString(KEY_USER_NAME, name)
        editor.putString(KEY_USER_EMAIL, email)
        editor.putString(KEY_USER_DOB, dateOfBirth)
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.apply()
    }

    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)

    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)

    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)

    fun getUserDateOfBirth(): String? = prefs.getString(KEY_USER_DOB, null)

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) && !getToken().isNullOrEmpty()
    }

    fun clearToken() {
        val editor = prefs.edit()
        editor.remove(KEY_TOKEN)
        editor.putBoolean(KEY_IS_LOGGED_IN, false)
        editor.apply()
        println("TokenManager: clearToken called -> token removed")
    }

    fun clearAll() {
        prefs.edit().clear().apply()
        println("TokenManager: clearAll called -> all prefs cleared")
    }
}
