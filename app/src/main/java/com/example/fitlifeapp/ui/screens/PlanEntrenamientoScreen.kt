
package com.example.fitlifeapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fitlifeapp.data.model.PlanEntrenamiento

@Composable
fun PlanEntrenamientoScreen(navController: NavHostController) {
    val planes = listOf(
        PlanEntrenamiento("Plan de Inicio Rápido", "Ideal para principiantes que buscan mejorar su condición física general.", "4 semanas"),
        PlanEntrenamiento("Plan de Fuerza y Volumen", "Diseñado para aquellos que quieren aumentar su masa muscular y fuerza.", "8 semanas")
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = { navController.navigate("home") }) {
            Text("Volver al Menú Principal")
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(planes) { plan ->
                PlanEntrenamientoCard(plan)
            }
        }
    }
}

@Composable
fun PlanEntrenamientoCard(plan: PlanEntrenamiento) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = plan.name, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = plan.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Duración: ${plan.duration}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
