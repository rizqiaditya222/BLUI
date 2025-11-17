package com.kotlin.blui.domain.model

data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val dateOfBirth: String?,
    val photoUrl: String?
)

