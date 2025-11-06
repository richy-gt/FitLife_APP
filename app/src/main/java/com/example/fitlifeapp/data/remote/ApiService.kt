package com.example.fitlifeapp.data.remote

import com.example.fitlifeapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // 🧩 Registro de usuario
    @POST("users/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>   // 👈 usa RegisterResponse

    // 🔐 Inicio de sesión
    @POST("users/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // 👤 Perfil del usuario autenticado (requiere token JWT)
    @GET("users/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<UserDto>

    // 🔎 Obtener usuario por ID
    @GET("users/{id}")
    suspend fun getUserById(
        @Path("id") id: String, // 👈 corregido a String
        @Header("Authorization") token: String
    ): Response<UserDto>

    // 👥 Obtener todos los usuarios (si el backend lo soporta)
    @GET("users")
    suspend fun getAllUsers(
        @Header("Authorization") token: String
    ): Response<UsersResponse>
}
