
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.fitlifeapp.data.model.PlanNutricional

@Preview(showBackground = true)
@Composable
fun PreviewPlanNutricionalScreen() {
    val navController = rememberNavController()
    PlanNutricionalScreen(navController)
}
@Composable
fun PlanNutricionalScreen(navController: NavHostController) {
    val planes = listOf(
        PlanNutricional("Dieta Balanceada", "Una dieta equilibrada para mantener un estilo de vida saludable.", 2000),
        PlanNutricional("Dieta de Definición", "Alta en proteínas y baja en carbohidratos para maximizar la pérdida de grasa.", 1800),
        PlanNutricional("Dieta de Volumen Limpio", "Plan diseñado para el crecimiento muscular con un ligero excedente calórico, priorizando carbohidratos complejos y grasas saludables.", 3000),
        PlanNutricional("Dieta Keto Simplificada", "Un enfoque muy bajo en carbohidratos y alto en grasas para inducir la cetosis y usar la grasa corporal como fuente principal de energía.", 1900),
        PlanNutricional("Dieta Vegetariana Completa", "Plan basado en plantas que asegura el aporte adecuado de proteínas y micronutrientes, ideal para atletas con restricciones dietéticas.", 2200)
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
                PlanNutricionalCard(plan)
            }
        }

        Button(onClick = { navController.navigate("home") }) {
            Text("Volver al Menú Principal")
        }
    }
}

@Composable
fun PlanNutricionalCard(plan: PlanNutricional) {
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
            Text(text = "Calorías por día: ${plan.caloriesPerDay}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
