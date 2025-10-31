package com.example.fitlifeapp.repository



import android.content.Context
import com.example.fitlifeapp.data.local.UserPreferences
import kotlinx.coroutines.flow.first

class AuthRepository(context: Context) {
    private val prefs = UserPreferences(context)

    suspend fun register(email: String, password: String): Boolean {
        prefs.saveUser(email, password)
        prefs.saveLoginState(false)
        return true
    }


    suspend fun login(email: String, password: String): Boolean {
        val (savedEmail, savedPassword) = prefs.getUser().first()
        return if (email == savedEmail && password == savedPassword) {
            prefs.saveLoginState(true)
            true
        } else false
    }

    suspend fun logout() = prefs.logout()
    fun isLoggedIn() = prefs.isLoggedIn()
}
