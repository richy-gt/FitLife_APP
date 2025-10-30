package com.example.fitlifeapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para la petición de login
 * Datos que ENVIAMOS al servidor
 */
data class LoginRequest(
    @SerializedName("username")
    val username: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("expiresInMins")
    val expiresInMins: Int = 30  // Token expira en 30 minutos
)