package com.example.fitlifeapp.data.remote

import android.content.Context
import com.example.fitlifeapp.data.local.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {


    private const val BASE_URL = "https://dummyjson.com/"


    private const val CONNECT_TIMEOUT = 15L // segundos
    private const val READ_TIMEOUT = 20L    // segundos
    private const val WRITE_TIMEOUT = 20L   // segundos

    fun create(context: Context): Retrofit {


        val sessionManager = SessionManager(context)


        val authInterceptor = AuthInterceptor(sessionManager)


        val loggingInterceptor = HttpLoggingInterceptor().apply {

            level = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.BASIC
            }
        }


        val okHttpClient = OkHttpClient.Builder()

            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)


            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)


            .retryOnConnectionFailure(true)

            .build()


        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }


    fun createPublic(): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }
}