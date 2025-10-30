package com.example.fitlifeapp.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlifeapp.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Estado de la UI
 */
data class ProfileUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val userEmail: String = "",
    val error: String? = null
)

/**
 * ViewModel: Maneja la lógica de UI y el estado
 * Usa AndroidViewModel para tener acceso al Application Context
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserRepository(application)

    // Estado PRIVADO (solo el ViewModel lo modifica)
    private val _uiState = MutableStateFlow(ProfileUiState())

    // Estado PÚBLICO (la UI lo observa)
    val uiState: StateFlow<ProfileUiState> = _uiState

    /**
     * Carga los datos del usuario desde la API
     */
    fun loadUser(id: Int = 1) {
        // Indicar que está cargando
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null
        )

        // Ejecutar en coroutine (no bloquea la UI)
        viewModelScope.launch {
            val result = repository.fetchUser(id)

            // Actualizar el estado según el resultado
            _uiState.value = result.fold(
                onSuccess = { user ->
                    // ✅ Éxito: mostrar datos
                    _uiState.value.copy(
                        isLoading = false,
                        userName = user.username,
                        userEmail = user.email ?: "Sin email",
                        error = null
                    )
                },
                onFailure = { exception ->
                    // ❌ Error: mostrar mensaje
                    _uiState.value.copy(
                        isLoading = false,
                        error = exception.localizedMessage ?: "Error desconocido"
                    )
                }
            )
        }
    }
}