package com.example.fitlifeapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO = Data Transfer Object
 * Este objeto representa los datos que VIAJAN entre tu app y el servidor
 */
data class UserDto(
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

    @SerializedName("image")
    val image: String? = null  // URL de imagen de perfil (opcional)
)