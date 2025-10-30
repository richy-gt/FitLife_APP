package com.example.fitlifeapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para la respuesta de lista de usuarios
 * La API devuelve un objeto con "users" y metadata de paginación
 */
data class UsersResponse(
    @SerializedName("users")
    val users: List<UserDto>,

    @SerializedName("total")
    val total: Int,  // Total de usuarios en la base de datos

    @SerializedName("skip")
    val skip: Int,   // Cuántos usuarios se saltaron (paginación)

    @SerializedName("limit")
    val limit: Int   // Límite de usuarios por página
)