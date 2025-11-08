package com.example.fitlifeapp.data.remote

import com.example.fitlifeapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {


    @POST("users/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>


    @POST("users/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>


    @GET("users/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<UserDto>


    @GET("users/{id}")
    suspend fun getUserById(
        @Path("id") id: String,
        @Header("Authorization") token: String
    ): Response<UserDto>


    @GET("users")
    suspend fun getAllUsers(
        @Header("Authorization") token: String
    ): Response<UsersResponse>
}
