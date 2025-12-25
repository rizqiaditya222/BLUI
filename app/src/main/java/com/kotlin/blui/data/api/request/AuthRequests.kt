package com.kotlin.blui.data.api.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    @SerializedName("date_of_birth")
    val dateOfBirth: String,
    @SerializedName("photo_url")
    val photoUrl: String? = null,
    val password: String
)

data class UpdateProfileRequest(
    @SerializedName("full_name")
    val fullName: String? = null,
    @SerializedName("date_of_birth")
    val dateOfBirth: String? = null,
    @SerializedName("photo_url")
    val photoUrl: String? = null
)
