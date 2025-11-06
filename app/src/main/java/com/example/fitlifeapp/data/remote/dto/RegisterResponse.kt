package com.example.fitlifeapp.data.remote.dto

data class RegisterResponse(
    val message: String?,
    val user: RegisteredUser?,
    val token: String?
)

data class RegisteredUser(
    val id: String?,
    val name: String?,
    val email: String?
)
