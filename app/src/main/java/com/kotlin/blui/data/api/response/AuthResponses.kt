package com.kotlin.blui.data.api.response

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
