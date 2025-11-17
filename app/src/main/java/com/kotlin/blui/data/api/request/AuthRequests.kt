package com.kotlin.blui.data.api.request

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val dateOfBirth: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class UpdateProfileRequest(
    val fullName: String?,
    val dateOfBirth: String?,
    val photoUrl: String?
)

