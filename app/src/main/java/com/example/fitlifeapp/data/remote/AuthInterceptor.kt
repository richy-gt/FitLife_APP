package com.example.fitlifeapp.data.remote



import com.example.fitlifeapp.data.local.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response


class AuthInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Recuperar el token (usando runBlocking porque intercept no es suspend)
        val token = runBlocking {
            sessionManager.getAuthToken()
        }

        // Si no hay token, continuar con la petición original
        if (token.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }

        // Crear nueva petición CON el token
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        // Continuar con la petición autenticada
        return chain.proceed(authenticatedRequest)
    }
}