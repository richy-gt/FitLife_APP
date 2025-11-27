package com.example.fitlifeapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NutritionResponse(
    val uri: String? = null,
    val calories: Int? = null,
    val totalWeight: Double? = null,
    val totalNutrients: TotalNutrients? = null,
    val healthLabels: List<String>? = null,
    val ingredients: List<Ingredient>? = null
)

data class Ingredient(
    val text: String? = null,
    val parsed: List<ParsedFood>? = null
)

data class ParsedFood(
    val quantity: Double? = null,
    val measure: String? = null,
    val food: String? = null,
    val foodId: String? = null,
    val weight: Double? = null,
    val nutrients: Nutrients? = null
)

data class Nutrients(
    @SerializedName("ENERC_KCAL")
    val energyKcal: Nutrient? = null,

    @SerializedName("PROCNT")
    val protein: Nutrient? = null,

    @SerializedName("FAT")
    val fat: Nutrient? = null,

    @SerializedName("CHOCDF")
    val carbs: Nutrient? = null,

    @SerializedName("FIBTG")
    val fiber: Nutrient? = null
)

data class TotalNutrients(
    @SerializedName("ENERC_KCAL")
    val energyKcal: Nutrient? = null,

    @SerializedName("PROCNT")
    val protein: Nutrient? = null,

    @SerializedName("FAT")
    val fat: Nutrient? = null,

    @SerializedName("CHOCDF")
    val carbs: Nutrient? = null,

    @SerializedName("FIBTG")
    val fiber: Nutrient? = null
)

data class Nutrient(
    val label: String? = null,
    val quantity: Double? = null,
    val unit: String? = null
)