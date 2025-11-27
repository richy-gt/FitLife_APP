package com.example.fitlifeapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fitlifeapp.data.remote.NutritionApiService


class NutritionViewModelFactory(
    private val application: Application,
    private val apiService: NutritionApiService? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NutritionViewModel::class.java)) {
            return NutritionViewModel(application, apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}