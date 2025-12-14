package com.example.fitlifeapp.ui.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.fitlifeapp.data.model.FoodSuggestion
import com.example.fitlifeapp.data.model.PlanNutricional
import com.example.fitlifeapp.viewmodel.NutritionViewModel
import com.example.fitlifeapp.viewmodel.NutritionViewModelFactory

// --- Colores del Tema "Sunset Dark" ---
private val DarkBackground = Color(0xFF121212)
private val DarkSurface = Color(0xFF252525)
private val AccentOrangeSoft = Color(0xFFFFAB91) // Naranja Coral
private val AccentAmber = Color(0xFFFFE082)      // Ámbar
private val TextWhite = Color(0xFFEEEEEE)
private val TextGray = Color(0xFFAAAAAA)
private val DarkText = Color(0xFF3E2723)         // Texto oscuro para contraste

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
        containerColor = DarkBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Nutrición Inteligente",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextWhite
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

            // --- Tarjeta de Análisis (Dashboard Style) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = DarkSurface
                ),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(0.dp) // Flat style moderno
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = AccentOrangeSoft)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Analiza tu Comida",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Toca un alimento para ver sus macros:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Grid de Alimentos
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        foodSuggestions.chunked(2).forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
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

                    // Loading
                    if (nutritionState.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = AccentOrangeSoft)
                        }
                    }

                    // Resultado Nutricional
                    nutritionState.nutritionData?.let { data ->
                        Spacer(modifier = Modifier.height(20.dp))
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "🥗 Planes Disponibles",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )

            // Lista de Planes
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(planes) { plan ->
                    PlanNutricionalCard(plan)
                }
            }

            // Botón Volver
            OutlinedButton(
                onClick = { navController.navigate("home") },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TextGray),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AccentOrangeSoft else DarkBackground
        ),
        // Borde sutil si no está seleccionado
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color.DarkGray) else null
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                food.emoji,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                food.name.split(" ").take(2).joinToString(" "),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = if (isSelected) DarkText else TextWhite,
                maxLines = 1
            )
        }
    }
}

@Composable
fun NutritionInfoCard(data: com.example.fitlifeapp.viewmodel.NutritionData, benefits: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkBackground // Fondo más oscuro dentro de la tarjeta gris
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, AccentAmber.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Información Nutricional",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AccentAmber
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            NutrientRow("🔥 Calorías", "${data.calories} kcal")
            NutrientRow("🥩 Proteínas", "${String.format("%.1f", data.protein)}g")
            NutrientRow("🍞 Carbohidratos", "${String.format("%.1f", data.carbs)}g")
            NutrientRow("🧈 Grasas", "${String.format("%.1f", data.fat)}g")
            NutrientRow("🌾 Fibra", "${String.format("%.1f", data.fiber)}g")

            if (benefits.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color.DarkGray)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "💡 $benefits",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
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
            style = MaterialTheme.typography.bodyMedium,
            color = TextWhite
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = AccentOrangeSoft // Destacar números en naranja
        )
    }
}

@Composable
fun PlanNutricionalCard(plan: PlanNutricional) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono Circular (Estrella o Corazón para salud)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(AccentAmber.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Icono Seguro: Favorite (Corazón) representa salud
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = AccentAmber
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plan.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = plan.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Chip de calorías
                Surface(
                    color = Color.Black.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "⚡ ${plan.caloriesPerDay} kcal/día",
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentAmber,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}