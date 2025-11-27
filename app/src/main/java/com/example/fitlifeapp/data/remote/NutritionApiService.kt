package com.example.fitlifeapp.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.fitlifeapp.data.remote.dto.NutritionResponse

interface NutritionApiService {
    @GET("api/nutrition-data")
    suspend fun getNutritionData(
        @Query("app_id") appId: String,
        @Query("app_key") appKey: String,
        @Query("nutrition-type") nutritionType: String = "logging",
        @Query("ingr") ingredient: String
    ): Response<NutritionResponse>
}