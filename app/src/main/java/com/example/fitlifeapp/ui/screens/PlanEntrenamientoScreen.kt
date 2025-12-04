package com.example.fitlifeapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitlifeapp.data.model.PlanEntrenamiento
import com.example.fitlifeapp.viewmodel.PlanEntrenamientoViewModel

@Composable
fun PlanEntrenamientoScreen(
    navController: NavHostController,
    viewModel: PlanEntrenamientoViewModel = viewModel()
) {
    var searchText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedPlan by remember { mutableStateOf<PlanEntrenamiento?>(null) }

    val planes = listOf(
        PlanEntrenamiento("Plan de Inicio Rápido", "Ideal para principiantes que buscan mejorar su condición física general.", "4 semanas"),
        PlanEntrenamiento("Plan de Fuerza y Volumen", "Diseñado para aumentar masa muscular y fuerza.", "8 semanas"),
        PlanEntrenamiento("Plan Quema Grasa - HIIT", "Entrenamientos intensos para maximizar la quema de calorías.", "6 semanas"),
        PlanEntrenamiento("Plan Yoga y Flexibilidad", "Movilidad, relajación y equilibrio.", "10 semanas"),
        PlanEntrenamiento("Plan Mantenimiento y Tono", "Mantén tu estado físico sin grandes cambios.", "Indefinido")
    )

    val filteredPlanes = planes.filter {
        it.name.contains(searchText, ignoreCase = true) ||
                it.description.contains(searchText, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Buscar plan") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(filteredPlanes) { plan ->
                PlanEntrenamientoCard(plan) {
                    selectedPlan = plan
                    showDialog = true
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Botón para volver al menú principal antes de seleccionar un plan
        Button(
            onClick = { navController.navigate("home") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al Menú Principal")
        }
    }

    // 🔹 Diálogo de confirmación al tocar un plan
    if (showDialog && selectedPlan != null) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "¿Deseas agregar este plan?",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(selectedPlan!!.name, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(selectedPlan!!.description, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = {
                            // Guardar plan en el ViewModel
                            viewModel.seleccionarPlan(selectedPlan!!)
                            // Navegar a detalle del plan
                            navController.navigate("detallePlan")
                            showDialog = false
                        }) {
                            Text("Sí")
                        }

                        OutlinedButton(onClick = { showDialog = false }) {
                            Text("No")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlanEntrenamientoCard(
    plan: PlanEntrenamiento,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = plan.name, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = plan.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Duración: ${plan.duration}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
