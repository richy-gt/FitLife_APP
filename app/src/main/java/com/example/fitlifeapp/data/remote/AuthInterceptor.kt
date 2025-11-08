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


        val token = runBlocking {
            sessionManager.getAuthToken()
        }


        if (token.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }


        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()


        return chain.proceed(authenticatedRequest)
    }
}