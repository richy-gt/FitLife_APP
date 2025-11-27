package com.example.fitlifeapp.viewmodel

import android.app.Application
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

    // IMPORTANTE: Obtener tus propias credenciales en:
    // https://developer.edamam.com/edamam-nutrition-api
    private val APP_ID = "TU_APP_ID"  // Reemplazar
    private val APP_KEY = "TU_APP_KEY"  // Reemplazar

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

            try {
                val response = nutritionApiService.getNutritionData(
                    appId = APP_ID,
                    appKey = APP_KEY,
                    ingredient = foodQuery
                )

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        nutritionData = NutritionData(
                            foodName = foodQuery,
                            calories = data.calories ?: 0,
                            protein = data.totalNutrients?.protein?.quantity ?: 0.0,
                            carbs = data.totalNutrients?.carbs?.quantity ?: 0.0,
                            fat = data.totalNutrients?.fat?.quantity ?: 0.0,
                            fiber = data.totalNutrients?.fiber?.quantity ?: 0.0
                        )
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No se encontró información para: $foodQuery"
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.localizedMessage}"
                )
            }
        }
    }
}