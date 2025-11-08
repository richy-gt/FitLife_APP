package com.example.fitlifeapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlifeapp.data.local.SessionManager
import com.example.fitlifeapp.data.local.UserPreferences
import com.example.fitlifeapp.data.remote.ApiService
import com.example.fitlifeapp.data.remote.RetrofitClient
import com.example.fitlifeapp.data.remote.dto.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class LoginViewModel(app: Application) : AndroidViewModel(app) {

    private val apiService: ApiService = RetrofitClient
        .create(app)
        .create(ApiService::class.java)

    private val sessionManager = SessionManager(app)
    private val userPreferences = UserPreferences(app)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status

    fun login(email: String, password: String) {
        viewModelScope.launch {
            if (email.isBlank() || password.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Por favor completa todos los campos"
                )
                _status.value = "Completa los campos"
                return@launch
            }

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                isSuccess = false
            )

            try {

                val request = LoginRequest(email = email, password = password)


                val response = apiService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!


                    sessionManager.saveToken(body.token ?: "")
                    sessionManager.saveUserEmail(body.user?.email ?: email)
                    userPreferences.saveUserPassword(password)
                    sessionManager.saveLoginState(true)

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = null
                    )
                    _status.value = "ok"
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Credenciales incorrectas o error del servidor"
                    )
                    _status.value = "Error de autenticación"
                }

            } catch (e: HttpException) {
                val errorMsg = when (e.code()) {
                    401 -> "Usuario o contraseña incorrectos"
                    404 -> "Servicio no encontrado"
                    500 -> "Error en el servidor. Intenta más tarde"
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
                    errorMessage = "Sin conexión a Internet. Verifica tu red"
                )
                _status.value = "Sin conexión a Internet"

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error inesperado: ${e.localizedMessage}"
                )
                _status.value = "Error inesperado"
            }
        }
    }

    fun isLoggedIn() = sessionManager.isLoggedIn()
}
