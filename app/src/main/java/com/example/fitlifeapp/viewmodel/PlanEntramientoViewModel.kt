package com.example.fitlifeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.example.fitlifeapp.data.model.PlanEntrenamiento

class PlanEntrenamientoViewModel : ViewModel() {

    val planSeleccionado = MutableLiveData<PlanEntrenamiento?>()

    fun seleccionarPlan(plan: PlanEntrenamiento) {
        planSeleccionado.value = plan
    }
}
