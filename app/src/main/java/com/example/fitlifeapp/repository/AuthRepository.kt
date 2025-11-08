package com.example.fitlifeapp.repository

import android.content.Context
import com.example.fitlifeapp.data.local.UserPreferences
import kotlinx.coroutines.flow.first

class AuthRepository(context: Context) {
    private val prefs = UserPreferences(context)


    suspend fun register(email: String, password: String): Boolean {
        prefs.saveUserEmail(email)
        prefs.saveUserPassword(password)
        prefs.saveLoginState(true)
        return true
    }


    suspend fun login(email: String, password: String): Boolean {
        val (savedEmail, savedPassword) = prefs.getUser().first()
        return if (email == savedEmail && password == savedPassword) {
            prefs.saveLoginState(true)
            true
        } else {
            prefs.saveLoginState(false)
            false
        }
    }


    suspend fun logout() = prefs.logout()


    fun isLoggedIn() = prefs.isLoggedIn()
}
