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
    val userImage: String? = null,
    val error: String? = null
)


class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService: ApiService = RetrofitClient
        .create(application)
        .create(ApiService::class.java)

    private val sessionManager = SessionManager(application)


    private val _uiState = MutableStateFlow(ProfileUiState())


    val uiState: StateFlow<ProfileUiState> = _uiState


    fun loadCurrentUser() {

        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            try {

                val token = sessionManager.getAuthToken()
                if (token.isNullOrEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No hay sesión activa. Por favor inicia sesión"
                    )
                    return@launch
                }


                val user = apiService.getCurrentUser()


                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    userName = "${user.firstName} ${user.lastName}",
                    userEmail = user.email,
                    userImage = user.image,
                    error = null
                )

            } catch (e: HttpException) {

                val errorMsg = when (e.code()) {
                    401 -> "Sesión expirada. Inicia sesión nuevamente"
                    403 -> "No tienes permisos para ver este perfil"
                    404 -> "Usuario no encontrado"
                    500 -> "Error en el servidor"
                    else -> "Error HTTP: ${e.code()}"
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = errorMsg
                )

            } catch (e: IOException) {

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Sin conexión a Internet. Verifica tu red"
                )

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.localizedMessage ?: "Desconocido"}"
                )
            }
        }
    }


    fun loadUser(id: Int) {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            try {
                val user = apiService.getUserById(id)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    userName = "${user.firstName} ${user.lastName}",
                    userEmail = user.email,
                    userImage = user.image,
                    error = null
                )

            } catch (e: HttpException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error HTTP: ${e.code()}"
                )

            } catch (e: IOException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Sin conexión a Internet"
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Error desconocido"
                )
            }
        }
    }
}