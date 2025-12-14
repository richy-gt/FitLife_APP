package com.example.fitlifeapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.fitlifeapp.data.model.PlanEntrenamiento
import com.example.fitlifeapp.viewmodel.PlanEntrenamientoViewModel

// --- Colores del Tema ---
private val DarkBackground = Color(0xFF121212)
private val DarkSurface = Color(0xFF252525)
private val AccentOrange = Color(0xFFFFAB91)
private val TextWhite = Color(0xFFEEEEEE)
private val TextGray = Color(0xFFAAAAAA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanEntrenamientoScreen(
    navController: NavHostController,
    viewModel: PlanEntrenamientoViewModel = viewModel()
) {
    var searchText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedPlan by remember { mutableStateOf<PlanEntrenamiento?>(null) }

    val planes = remember {
        listOf(
            PlanEntrenamiento("Inicio Rápido", "Ideal para principiantes.", "4 semanas"),
            PlanEntrenamiento("Fuerza y Volumen", "Aumenta masa muscular.", "8 semanas"),
            PlanEntrenamiento("Quema Grasa HIIT", "Intensidad máxima.", "6 semanas"),
            PlanEntrenamiento("Yoga Flex", "Equilibrio y movilidad.", "10 semanas"),
            PlanEntrenamiento("Mantenimiento", "Mantén tu estado físico.", "Indefinido")
        )
    }

    val filteredPlanes = planes.filter {
        it.name.contains(searchText, true) || it.description.contains(searchText, true)
    }

    // --- DIÁLOGO DE CONFIRMACIÓN ---
    if (showDialog && selectedPlan != null) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.CheckCircle, null, tint = AccentOrange, modifier = Modifier.size(50.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("¿Iniciar Plan?", style = MaterialTheme.typography.titleLarge, color = TextWhite, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(selectedPlan!!.name, color = AccentOrange, fontWeight = FontWeight.SemiBold)
                    Text(selectedPlan!!.description, color = TextGray, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
                    Spacer(Modifier.height(24.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { showDialog = false }, modifier = Modifier.weight(1f), border = androidx.compose.foundation.BorderStroke(1.dp, TextGray)) { Text("Cancelar", color = TextGray) }
                        Button(onClick = {
                            viewModel.seleccionarPlan(selectedPlan!!)
                            navController.navigate("detallePlan")
                            showDialog = false
                        }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = AccentOrange, contentColor = Color.Black)) { Text("¡Vamos!") }
                    }
                }
            }
        }
    }

    // --- PANTALLA PRINCIPAL ---
    Column(
        modifier = Modifier.fillMaxSize().background(DarkBackground).padding(20.dp)
    ) {
        Text("Elige tu Objetivo", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = TextWhite)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Buscar plan...") },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = AccentOrange) },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentOrange, unfocusedBorderColor = TextGray,
                focusedLabelColor = AccentOrange, unfocusedLabelColor = TextGray,
                cursorColor = AccentOrange, focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface, focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredPlanes) { plan ->
                PlanCard(plan) {
                    selectedPlan = plan
                    showDialog = true
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = { navController.navigate("home") },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, TextGray),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
        ) {
            Icon(Icons.Default.ArrowBack, null)
            Spacer(Modifier.width(8.dp))
            Text("Volver al Menú")
        }
    }
}

@Composable
fun PlanCard(plan: PlanEntrenamiento, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).background(AccentOrange.copy(0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.DateRange, null, tint = AccentOrange)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(plan.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextWhite)
                Text(plan.description, style = MaterialTheme.typography.bodySmall, color = TextGray, maxLines = 2)
                Spacer(Modifier.height(6.dp))
                Text("⏱ ${plan.duration}", style = MaterialTheme.typography.labelSmall, color = AccentOrange, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Default.ArrowForward, null, tint = TextGray)
        }
    }
}