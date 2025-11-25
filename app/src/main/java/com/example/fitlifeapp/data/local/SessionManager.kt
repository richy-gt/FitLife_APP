package com.example.fitlifeapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("session_prefs")

class SessionManager(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val NAME_KEY = stringPreferencesKey("name") // Nueva clave para el nombre
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
    }


    suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }


    suspend fun getAuthToken(): String? {
        return context.dataStore.data.map { it[TOKEN_KEY] }.first()
    }


    suspend fun saveUserEmail(email: String) {
        context.dataStore.edit { prefs ->
            prefs[EMAIL_KEY] = email
        }
    }


    suspend fun getUserEmail(): String? {
        return context.dataStore.data.map { it[EMAIL_KEY] }.first()
    }

    // --- Nuevas funciones para el nombre del usuario ---
    suspend fun saveUserName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[NAME_KEY] = name
        }
    }

    suspend fun getUserName(): String? {
        return context.dataStore.data.map { it[NAME_KEY] }.first()
    }
    // -----------------------------------------------


    suspend fun saveLoginState(isLoggedIn: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN_KEY] = isLoggedIn
        }
    }

    fun isLoggedIn(): Flow<Boolean> = context.dataStore.data.map { it[IS_LOGGED_IN_KEY] ?: false }

    suspend fun logout() {
        context.dataStore.edit { it.clear() }
    }
}
