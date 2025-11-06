package com.example.fitlifeapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlifeapp.data.local.SessionManager
import com.example.fitlifeapp.data.local.UserPreferences
import com.example.fitlifeapp.data.remote.ApiService
import com.example.fitlifeapp.data.remote.RetrofitClient
import com.example.fitlifeapp.data.remote.dto.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class RegisterUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class RegisterViewModel(app: Application) : AndroidViewModel(app) {

    private val apiService: ApiService = RetrofitClient
        .createPublic() // 👈 cliente sin token
        .create(ApiService::class.java)

    private val sessionManager = SessionManager(app)
    private val userPreferences = UserPreferences(app)

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status

    fun register(email: String, password: String, name: String? = null) {
        viewModelScope.launch {
            if (email.isBlank() || password.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Por favor completa todos los campos"
                )
                _status.value = "Campos vacíos"
                return@launch
            }

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                isSuccess = false
            )

            try {
                // 🧩 Construir la solicitud
                val request = RegisterRequest(
                    email = email,
                    password = password,
                    name = name
                )

                // 📡 Llamada al backend
                val response = apiService.register(request)

                if (response.isSuccessful && response.body() != null) {
                    val registerData = response.body()!!

                    // ✅ Guarda token y datos del usuario
                    sessionManager.saveToken(registerData.token ?: "")
                    val userEmail = registerData.user?.email ?: email
                    userPreferences.saveUserEmail(userEmail)
                    userPreferences.saveLoginState(true)

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                    _status.value = "ok"
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error en el registro. Intenta nuevamente"
                    )
                    _status.value = "Error en registro"
                }

            } catch (e: HttpException) {
                val errorMsg = when (e.code()) {
                    400 -> "Correo ya registrado o datos inválidos"
                    409 -> "Este usuario ya existe"
                    500 -> "Error en el servidor"
                    else -> "Error de conexión (${e.code()})"
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = errorMsg
                )
                _status.value = errorMsg

            } catch (e: IOException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Sin conexión a Internet"
                )
                _status.value = "Sin conexión"

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error inesperado: ${e.localizedMessage}"
                )
                _status.value = "Error inesperado"
            }
        }
    }
}
