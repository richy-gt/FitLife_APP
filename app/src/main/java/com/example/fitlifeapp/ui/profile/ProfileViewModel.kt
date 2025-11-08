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

    init {
        // Load initial data and then fetch from network
        loadInitialDataFromSession()
    }

    private fun loadInitialDataFromSession() {
        viewModelScope.launch {
            val userEmail = sessionManager.getUserEmail()
            if (userEmail != null) {
                _uiState.value = _uiState.value.copy(
                    userEmail = userEmail,
                    userName = userEmail.substringBefore('@')
                )
            }
            // After loading initial data, fetch the full profile
            loadCurrentUser()
        }
    }


    fun loadCurrentUser() {
        viewModelScope.launch {
            // Don't show loading indicator if we already have some data
            if (_uiState.value.userName.isEmpty()) {
                _uiState.value = _uiState.value.copy(isLoading = true)
            }
            _uiState.value = _uiState.value.copy(error = null)

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
                        userName = user.name ?: _uiState.value.userName,
                        userEmail = user.email ?: _uiState.value.userEmail
                    )
                } else {
                    // Don't show an error if we at least have session data
                    if (_uiState.value.userName.isEmpty()) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Error: ${response.code()} - ${response.message()}"
                        )
                    }
                }

            } catch (e: HttpException) {
                if (_uiState.value.userName.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error HTTP: ${e.code()} - ${e.message()}"
                    )
                }
            } catch (e: IOException) {
                if (_uiState.value.userName.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Sin conexión a Internet."
                    )
                }
            } catch (e: Exception) {
                if (_uiState.value.userName.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: "Error desconocido."
                    )
                }
            } finally {
                // Always ensure loading is turned off
                if (_uiState.value.isLoading) {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }


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
                        userName = user.name ?: "",
                        userEmail = user.email ?: "",
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
