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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.fitlifeapp.data.model.PlanEntrenamiento
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment

@Preview(showBackground = true)
@Composable
fun PreviewPlanEntrenamientoScreen() {
    val navController = rememberNavController()
    PlanEntrenamientoScreen(navController)
}
@Composable
fun PlanEntrenamientoScreen(navController: NavHostController) {
    val planes = listOf(
        PlanEntrenamiento("Plan de Inicio Rápido", "Ideal para principiantes que buscan mejorar su condición física general.", "4 semanas"),
        PlanEntrenamiento("Plan de Fuerza y Volumen", "Diseñado para aquellos que quieren aumentar su masa muscular y fuerza.", "8 semanas"),
        PlanEntrenamiento("Plan Quema Grasa - Cardio Intenso", "Entrenamientos de alta intensidad (HIIT) y cardio para maximizar la quema de calorías y la definición muscular.", "6 semanas"),
        PlanEntrenamiento("Plan Yoga y Flexibilidad", "Rutinas diarias centradas en la movilidad, el equilibrio y la reducción del estrés. Ideal para mejorar la postura y prevenir lesiones.", "10 semanas"),
        PlanEntrenamiento("Plan Mantenimiento y Tono", "Diseñado para personas que ya tienen una base de entrenamiento y desean mantener su nivel de fitness y tonificar el cuerpo sin buscar cambios drásticos de volumen.", "Indefinido")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(35.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(planes) { plan ->
                PlanEntrenamientoCard(plan)
            }
        }

        Button(onClick = { navController.navigate("home") }) {
            Text("Volver al Menú Principal")
        }
    }
}

@Composable
fun PlanEntrenamientoCard(plan: PlanEntrenamiento) {
    Card(
        modifier = Modifier.fillMaxWidth(0.90f),
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
