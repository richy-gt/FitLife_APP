package com.example.fitlifeapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para la respuesta de login
 * Datos que RECIBIMOS del servidor tras login exitoso
 */
data class LoginResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("firstName")
    val firstName: String,

    @SerializedName("lastName")
    val lastName: String,

    @SerializedName("accessToken")
    val accessToken: String,  // 🔑 TOKEN JWT - Lo guardamos en SessionManager

    @SerializedName("refreshToken")
    val refreshToken: String?  // Opcional - Para renovar el token
)