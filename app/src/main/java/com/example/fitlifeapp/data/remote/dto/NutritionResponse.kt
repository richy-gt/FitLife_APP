package com.example.fitlifeapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NutritionResponse(
    val calories: Int? = null,
    val totalWeight: Double? = null,
    val totalNutrients: TotalNutrients? = null,
    val healthLabels: List<String>? = null
)

data class TotalNutrients(
    @SerializedName("ENERC_KCAL")
    val energyKcal: Nutrient? = null,  // Calorías

    @SerializedName("PROCNT")
    val protein: Nutrient? = null,      // Proteínas

    @SerializedName("FAT")
    val fat: Nutrient? = null,         // Grasas

    @SerializedName("CHOCDF")
    val carbs: Nutrient? = null,      // Carbohidratos

    @SerializedName("FIBTG")
    val fiber: Nutrient? = null        // Fibra
)

data class Nutrient(
    val label: String? = null,
    val quantity: Double? = null,
    val unit: String? = null
)