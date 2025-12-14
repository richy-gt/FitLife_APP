package com.example.fitlifeapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.fitlifeapp.viewmodel.PlanEntrenamientoViewModel

// --- Colores Sunset Dark ---
private val DarkBg = Color(0xFF121212)
private val DarkSurf = Color(0xFF252525)
private val Orange = Color(0xFFFFAB91)
private val TextW = Color(0xFFEEEEEE)
private val TextG = Color(0xFFAAAAAA)

@Composable
fun DetallePlanScreen(
    navController: NavHostController,
    viewModel: PlanEntrenamientoViewModel = viewModel()
) {
    val plan = viewModel.planSeleccionado.value
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(plan) { visible = plan != null }

    Column(
        modifier = Modifier.fillMaxSize().background(DarkBg).padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (plan == null) {
            Text("No se seleccionó ningún plan.", color = TextG)
            return
        }

        AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurf),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = plan.name,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Orange
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = plan.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextW
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Duración: ${plan.duration}",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextG,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(Modifier.height(30.dp))

        Button(
            onClick = { navController.navigate("home") },
            modifier = Modifier.fillMaxWidth(0.9f).height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Volver al Menú Principal", fontWeight = FontWeight.Bold)
        }
    }
}