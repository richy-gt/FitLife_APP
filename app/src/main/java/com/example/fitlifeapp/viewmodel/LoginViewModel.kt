package com.example.fitlifeapp.viewmodel



import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlifeapp.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = AuthRepository(app)

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status

    fun login(email: String, password: String) {
        viewModelScope.launch {
            if (email.isBlank() || password.isBlank()) {
                _status.value = "Completa los campos"
                return@launch
            }
            val success = repo.login(email, password)
            _status.value = if (success) "ok" else "Credenciales incorrectas"
        }
    }

    fun isLoggedIn() = repo.isLoggedIn()
}
