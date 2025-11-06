package com.example.fitlifeapp.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlifeapp.data.local.SessionManager
import com.example.fitlifeapp.data.remote.ApiService
import com.example.fitlifeapp.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class ProfileUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val userEmail: String = "",
    val error: String? = null
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService: ApiService = RetrofitClient
        .create(application)
        .create(ApiService::class.java)

    private val sessionManager = SessionManager(application)

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    /**
     * 🔐 Cargar perfil del usuario autenticado (usa token JWT)
     */
    fun loadCurrentUser() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val token = sessionManager.getAuthToken()
                if (token.isNullOrEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No hay sesión activa. Por favor inicia sesión."
                    )
                    return@launch
                }

                val response = apiService.getProfile("Bearer $token")

                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        userName = user.name ?: "Usuario",
                        userEmail = user.email ?: "Sin correo",
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error: ${response.code()} - ${response.message()}"
                    )
                }

            } catch (e: HttpException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error HTTP: ${e.code()} - ${e.message()}"
                )
            } catch (e: IOException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Sin conexión a Internet."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Error desconocido."
                )
            }
        }
    }

    /**
     * 👤 Cargar un usuario específico (por ID)
     */
    fun loadUser(id: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val token = sessionManager.getAuthToken()
                val response = apiService.getUserById(id, "Bearer $token")

                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        userName = user.name ?: "Usuario",
                        userEmail = user.email ?: "Sin correo",
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error al obtener usuario: ${response.message()}"
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Error desconocido"
                )
            }
        }
    }
}
