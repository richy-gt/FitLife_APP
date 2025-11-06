package com.example.fitlifeapp.data.remote.dto

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String? = null
)
