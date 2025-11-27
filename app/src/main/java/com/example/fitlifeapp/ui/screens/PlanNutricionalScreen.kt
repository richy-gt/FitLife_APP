package com.example.fitlifeapp.ui.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.fitlifeapp.data.model.FoodSuggestion
import com.example.fitlifeapp.data.model.PlanNutricional
import com.example.fitlifeapp.viewmodel.NutritionViewModel
import com.example.fitlifeapp.viewmodel.NutritionViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanNutricionalScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val nutritionViewModel: NutritionViewModel = viewModel(
        factory = NutritionViewModelFactory(
            application = context.applicationContext as Application
        )
    )
    val planes = listOf(
        PlanNutricional("Dieta Balanceada", "Una dieta equilibrada para mantener un estilo de vida saludable.", 2000),
        PlanNutricional("Dieta de Definición", "Alta en proteínas y baja en carbohidratos para maximizar la pérdida de grasa.", 1800),
        PlanNutricional("Dieta de Volumen Limpio", "Plan diseñado para el crecimiento muscular con un ligero excedente calórico.", 3000),
        PlanNutricional("Dieta Keto Simplificada", "Un enfoque muy bajo en carbohidratos y alto en grasas.", 1900),
        PlanNutricional("Dieta Vegetariana Completa", "Plan basado en plantas que asegura el aporte adecuado de proteínas.", 2200)
    )

    val foodSuggestions = listOf(
        FoodSuggestion("Pollo a la plancha 100g", "100g chicken breast", "🍗", "Alto en proteínas, bajo en grasa"),
        FoodSuggestion("Arroz integral 1 taza", "1 cup brown rice", "🍚", "Carbohidratos complejos, fibra"),
        FoodSuggestion("Brócoli 100g", "100g broccoli", "🥦", "Rico en vitaminas, bajo en calorías"),
        FoodSuggestion("Salmón 100g", "100g salmon", "🐟", "Omega-3, proteínas de calidad"),
        FoodSuggestion("Aguacate medio", "1/2 avocado", "🥑", "Grasas saludables, saciedad"),
        FoodSuggestion("Huevos 2 unidades", "2 large eggs", "🥚", "Proteína completa, económico")
    )

    var selectedFood by remember { mutableStateOf<FoodSuggestion?>(null) }
    val nutritionState by nutritionViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Plan Nutricional") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "🥗 Analiza tu Comida",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Selecciona un alimento para ver su información nutricional",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        foodSuggestions.chunked(2).forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowItems.forEach { food ->
                                    FoodChip(
                                        food = food,
                                        onClick = {
                                            selectedFood = food
                                            nutritionViewModel.searchFood(food.quantity)
                                        },
                                        isSelected = selectedFood == food,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (rowItems.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    if (nutritionState.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    nutritionState.nutritionData?.let { data ->
                        Spacer(modifier = Modifier.height(16.dp))
                        NutritionInfoCard(data, selectedFood?.benefits ?: "")
                    }

                    nutritionState.error?.let { error ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Text(
                "📋 Planes Disponibles",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(planes) { plan ->
                    PlanNutricionalCard(plan)
                }
            }

            Button(
                onClick = { navController.navigate("home") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al Menú Principal")
            }
        }
    }
}

@Composable
fun FoodChip(
    food: FoodSuggestion,
    onClick: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(if (isSelected) 8.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                food.emoji,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                food.name.split(" ").take(2).joinToString(" "),
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )
        }
    }
}

@Composable
fun NutritionInfoCard(data: com.example.fitlifeapp.viewmodel.NutritionData, benefits: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "📊 Información Nutricional",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            NutrientRow("🔥 Calorías", "${data.calories} kcal")
            NutrientRow("🥩 Proteínas", "${String.format("%.1f", data.protein)}g")
            NutrientRow("🍞 Carbohidratos", "${String.format("%.1f", data.carbs)}g")
            NutrientRow("🧈 Grasas", "${String.format("%.1f", data.fat)}g")
            NutrientRow("🌾 Fibra", "${String.format("%.1f", data.fiber)}g")

            if (benefits.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "💡 $benefits",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun NutrientRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PlanNutricionalCard(plan: PlanNutricional) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = plan.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = plan.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Calorías por día: ${plan.caloriesPerDay}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}