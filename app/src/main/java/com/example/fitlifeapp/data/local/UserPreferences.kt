package com.example.fitlifeapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// DataStore de sesión
private val Context.dataStore by preferencesDataStore("user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        private val EMAIL_KEY = stringPreferencesKey("user_email")
        private val PASSWORD_KEY = stringPreferencesKey("user_password")
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
    }

    // ✅ Guardar email
    suspend fun saveUserEmail(email: String) {
        context.dataStore.edit { prefs ->
            prefs[EMAIL_KEY] = email
        }
    }

    // ✅ Guardar contraseña (opcional)
    suspend fun saveUserPassword(password: String) {
        context.dataStore.edit { prefs ->
            prefs[PASSWORD_KEY] = password
        }
    }

    // ✅ Guardar estado de login
    suspend fun saveLoginState(isLoggedIn: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN_KEY] = isLoggedIn
        }
    }

    // ✅ Obtener usuario (correo y contraseña)
    fun getUser(): Flow<Pair<String?, String?>> {
        return context.dataStore.data.map { prefs ->
            Pair(prefs[EMAIL_KEY], prefs[PASSWORD_KEY])
        }
    }

    // ✅ Saber si está logueado
    fun isLoggedIn(): Flow<Boolean> = context.dataStore.data.map {
        it[IS_LOGGED_IN_KEY] ?: false
    }

    // ✅ Cerrar sesión
    suspend fun logout() {
        context.dataStore.edit { it.clear() }
    }

    // ✅ (opcional) Obtener el correo directamente
    suspend fun getUserEmail(): String? {
        return context.dataStore.data.map { it[EMAIL_KEY] }.first()
    }
}
