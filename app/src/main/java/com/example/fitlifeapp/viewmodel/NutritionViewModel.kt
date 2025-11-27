package com.example.fitlifeapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlifeapp.data.remote.NutritionApiService
import com.example.fitlifeapp.data.remote.NutritionRetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class NutritionUiState(
    val isLoading: Boolean = false,
    val nutritionData: NutritionData? = null,
    val error: String? = null
)

data class NutritionData(
    val foodName: String,
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val fiber: Double
)

class NutritionViewModel(
    app: Application,
    private val apiService: NutritionApiService? = null
) : AndroidViewModel(app) {


    private val APP_ID = "45d45589"
    private val APP_KEY = "8dc12f079f436ab38acc71074bd8b8cd"

    private val nutritionApiService: NutritionApiService by lazy {
        apiService ?: NutritionRetrofitClient
            .create()
            .create(NutritionApiService::class.java)
    }

    private val _uiState = MutableStateFlow(NutritionUiState())
    val uiState: StateFlow<NutritionUiState> = _uiState

    fun searchFood(foodQuery: String) {
        if (foodQuery.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            Log.d("NutritionVM", "🔍 Buscando: '$foodQuery'")

            try {
                val response = nutritionApiService.getNutritionData(
                    appId = APP_ID,
                    appKey = APP_KEY,
                    ingredient = foodQuery
                )

                Log.d("NutritionVM", "📡 Response Code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!

                    val nutrients = data.ingredients
                        ?.firstOrNull()
                        ?.parsed
                        ?.firstOrNull()
                        ?.nutrients

                    Log.d("NutritionVM", "🥗 Ingredients encontrados: ${data.ingredients?.size}")
                    Log.d("NutritionVM", "📊 Nutrients: $nutrients")

                    if (nutrients == null) {
                        Log.e("NutritionVM", "❌ No se encontraron nutrientes en la respuesta")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "No se encontró información nutricional para: $foodQuery"
                        )
                        return@launch
                    }

                    val calories = nutrients.energyKcal?.quantity?.toInt() ?: 0
                    val protein = nutrients.protein?.quantity ?: 0.0
                    val carbs = nutrients.carbs?.quantity ?: 0.0
                    val fat = nutrients.fat?.quantity ?: 0.0
                    val fiber = nutrients.fiber?.quantity ?: 0.0

                    Log.d("NutritionVM", "✅ Datos procesados:")
                    Log.d("NutritionVM", "  - Calorías: $calories kcal")
                    Log.d("NutritionVM", "  - Proteínas: $protein g")
                    Log.d("NutritionVM", "  - Carbohidratos: $carbs g")
                    Log.d("NutritionVM", "  - Grasas: $fat g")
                    Log.d("NutritionVM", "  - Fibra: $fiber g")

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        nutritionData = NutritionData(
                            foodName = foodQuery,
                            calories = calories,
                            protein = protein,
                            carbs = carbs,
                            fat = fat,
                            fiber = fiber
                        )
                    )
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("NutritionVM", "❌ Error: $errorBody")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error ${response.code()}: $errorBody"
                    )
                }
            } catch (e: Exception) {
                Log.e("NutritionVM", "💥 Exception: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.localizedMessage}"
                )
            }
        }
    }
}