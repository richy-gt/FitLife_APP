package com.example.fitlifeapp.data.local



import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userDataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_PASSWORD = stringPreferencesKey("password")
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    suspend fun saveUser(email: String, password: String) {
        context.userDataStore.edit { prefs ->
            prefs[KEY_EMAIL] = email
            prefs[KEY_PASSWORD] = password
        }
    }

    suspend fun saveLoginState(isLoggedIn: Boolean) {
        context.userDataStore.edit { prefs ->
            prefs[KEY_IS_LOGGED_IN] = isLoggedIn
        }
    }

    fun getUser(): Flow<Pair<String?, String?>> {
        return context.userDataStore.data.map { prefs ->
            Pair(prefs[KEY_EMAIL], prefs[KEY_PASSWORD])
        }
    }

    fun isLoggedIn(): Flow<Boolean> {
        return context.userDataStore.data.map { prefs ->
            prefs[KEY_IS_LOGGED_IN] ?: false
        }
    }

    suspend fun logout() {
        context.userDataStore.edit { prefs ->
            prefs[KEY_IS_LOGGED_IN] = false
        }
    }

    suspend fun clearAll() {
        context.userDataStore.edit { prefs ->
            prefs.clear()
        }
    }

}
