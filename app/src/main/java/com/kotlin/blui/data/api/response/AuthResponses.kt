package com.kotlin.blui.data.api.response

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    val token: String,
    val user: UserResponse
)

data class UserResponse(
    val id: String,
    val fullName: String,
    val email: String,
    val dateOfBirth: String?,
    val photoUrl: String?
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: LoginData?
)

data class LoginData(
    val token: String,
    val user: User
)

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val data: User?
)

data class User(
    val id: String,
    val name: String,
    val email: String,
    val dateOfBirth: String? = null
)
