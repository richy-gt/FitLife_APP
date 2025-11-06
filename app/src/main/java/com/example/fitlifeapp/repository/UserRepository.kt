package com.example.fitlifeapp.repository

import android.content.Context
import com.example.fitlifeapp.data.local.SessionManager
import com.example.fitlifeapp.data.remote.ApiService
import com.example.fitlifeapp.data.remote.RetrofitClient
import com.example.fitlifeapp.data.remote.dto.UserDto
import kotlinx.coroutines.runBlocking

class UserRepository(context: Context) {

    private val apiService: ApiService = RetrofitClient
        .create(context)
        .create(ApiService::class.java)

    private val sessionManager = SessionManager(context)

    /**
     * 👤 Obtiene un usuario por su ID
     */
    suspend fun fetchUser(id: String): Result<UserDto> {
        return try {
            val token = runBlocking { sessionManager.getAuthToken() }

            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No se encontró token. Inicia sesión."))
            }

            val response = apiService.getUserById(id, "Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error del servidor: ${response.code()}"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 📋 Obtiene todos los usuarios (si tu backend lo soporta)
     */
    suspend fun fetchAllUsers(): Result<List<UserDto>> {
        return try {
            val token = runBlocking { sessionManager.getAuthToken() }

            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No se encontró token. Inicia sesión."))
            }

            val response = apiService.getAllUsers("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.users)
            } else {
                Result.failure(Exception("Error del servidor: ${response.code()}"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
