package com.example.fitlifeapp.repository
import android.content.Context
import com.example.fitlifeapp.data.remote.ApiService
import com.example.fitlifeapp.data.remote.RetrofitClient
import com.example.fitlifeapp.data.remote.dto.UserDto


class UserRepository(context: Context) {

    // Crear la instancia del API Service (pasando el contexto)
    private val apiService: ApiService = RetrofitClient
        .create(context)
        .create(ApiService::class.java)

    /**
     * Obtiene un usuario de la API
     *
     * Usa Result<T> para manejar éxito/error de forma elegante
     */
    suspend fun fetchUser(id: Int = 1): Result<UserDto> {
        return try {
            // CAMBIA ESTA LÍNEA
            val user = apiService.getUserById(id) // ¡Usa la función correcta!

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}