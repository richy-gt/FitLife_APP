package com.example.fitlifeapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fitlifeapp.data.local.SessionManager
import kotlinx.coroutines.launch

// --- Colores del Tema "Sunset Dark" ---
private val DarkBackground = Color(0xFF121212)
private val DarkSurface = Color(0xFF252525)
private val AccentOrangeSoft = Color(0xFFFFAB91)
private val AccentAmber = Color(0xFFFFE082)
private val TextWhite = Color(0xFFEEEEEE)
private val TextGray = Color(0xFFAAAAAA)
private val DarkText = Color(0xFF3E2723)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgresoScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope() // Necesario para guardar en segundo plano
    val sessionManager = remember { SessionManager(context) }

    // --- ESTADOS ---
    var userEmail by remember { mutableStateOf("default") }
    var pesoActual by remember { mutableStateOf("75") } // Valor inicial mientras carga
    var grasaActual by remember { mutableStateOf("15") }
    var masaMuscular by remember { mutableStateOf("40") }

    // Lista de récords (Se cargarán desde memoria)
    val records = remember { mutableStateListOf<Pair<String, String>>() }

    // --- CARGAR DATOS GUARDADOS AL INICIAR ---
    LaunchedEffect(Unit) {
        val email = sessionManager.getUserEmail() ?: "user_generico"
        userEmail = email

        // Cargar Estadísticas (Si no hay dato guardado, usa el valor por defecto: "75", "15", etc.)
        pesoActual = sessionManager.getUserStat(email, "weight", "75")
        grasaActual = sessionManager.getUserStat(email, "fat", "15")
        masaMuscular = sessionManager.getUserStat(email, "muscle", "40")

        // Cargar Récords (o usar los por defecto si es la primera vez)
        val defaultRecords = listOf(
            "Bench Press" to "100",
            "Sentadilla" to "140",
            "Peso Muerto" to "180",
            "Press Militar" to "60"
        )

        records.clear()
        defaultRecords.forEachIndexed { index, defaultPair ->
            // Recupera nombre y peso guardados para cada ejercicio
            val savedName = sessionManager.getUserStat(email, "rec_${index}_name", defaultPair.first)
            val savedWeight = sessionManager.getUserStat(email, "rec_${index}_weight", defaultPair.second)
            records.add(savedName to savedWeight)
        }
    }

    // Estados para el Diálogo
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogValue by remember { mutableStateOf("") }
    var currentEditingIndex by remember { mutableStateOf(-1) }

    // --- FUNCIÓN PARA GUARDAR (Con Persistencia) ---
    fun saveValue(newValue: String) {
        if (newValue.isNotBlank()) {
            // 1. Actualizar la UI inmediatamente
            when (currentEditingIndex) {
                -1 -> pesoActual = newValue
                -2 -> grasaActual = newValue
                -3 -> masaMuscular = newValue
                else -> {
                    if (currentEditingIndex >= 0 && currentEditingIndex < records.size) {
                        val (name, _) = records[currentEditingIndex]
                        records[currentEditingIndex] = name to newValue
                    }
                }
            }

            // 2. Guardar permanentemente en DataStore
            scope.launch {
                when (currentEditingIndex) {
                    -1 -> sessionManager.saveUserStat(userEmail, "weight", newValue)
                    -2 -> sessionManager.saveUserStat(userEmail, "fat", newValue)
                    -3 -> sessionManager.saveUserStat(userEmail, "muscle", newValue)
                    else -> {
                        // Guardar récord específico (solo el peso en este caso)
                        if (currentEditingIndex >= 0) {
                            sessionManager.saveUserStat(userEmail, "rec_${currentEditingIndex}_weight", newValue)
                        }
                    }
                }
            }
        }
        showDialog = false
    }

    // Función auxiliar para abrir diálogo
    fun openDialog(title: String, currentValue: String, index: Int) {
        dialogTitle = title
        dialogValue = currentValue
        currentEditingIndex = index
        showDialog = true
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Mi Evolución", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = TextWhite)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = DarkBackground, titleContentColor = TextWhite)
            )
        }
    ) { padding ->
        if (showDialog) {
            EditValueDialog(title = dialogTitle, initialValue = dialogValue, onDismiss = { showDialog = false }, onConfirm = { saveValue(it) })
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Text("Constancia Semanal", style = MaterialTheme.typography.titleMedium, color = TextGray, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(12.dp))
            WeeklyStreakSection()

            Spacer(modifier = Modifier.height(24.dp))

            Text("Estadísticas Corporales (Toca para editar)", style = MaterialTheme.typography.titleMedium, color = TextGray, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // PESO
                ProgresoItem(
                    statName = "Peso Corporal",
                    statValue = "$pesoActual kg",
                    progress = 0.75f, // Esto podría ser dinámico comparado con una meta
                    icon = Icons.Default.Person,
                    color = AccentOrangeSoft,
                    onClick = { openDialog("Editar Peso", pesoActual, -1) }
                )

                // GRASA
                ProgresoItem(
                    statName = "Grasa Corporal",
                    statValue = "$grasaActual%",
                    progress = (grasaActual.toFloatOrNull() ?: 15f) / 100f,
                    icon = Icons.Default.FavoriteBorder,
                    color = AccentAmber,
                    onClick = { openDialog("Editar % Grasa", grasaActual, -2) }
                )

                // MÚSCULO
                ProgresoItem(
                    statName = "Masa Muscular",
                    statValue = "$masaMuscular%",
                    progress = (masaMuscular.toFloatOrNull() ?: 40f) / 100f,
                    icon = Icons.Default.Star,
                    color = Color(0xFF81C784),
                    onClick = { openDialog("Editar % Músculo", masaMuscular, -3) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Mejores Marcas (Toca para editar)", style = MaterialTheme.typography.titleMedium, color = TextGray, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(12.dp))

            // Grid Dinámico de Récords
            if (records.isNotEmpty()) {
                records.chunked(2).forEachIndexed { rowIndex, rowItems ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowItems.forEachIndexed { colIndex, item ->
                            val realIndex = (rowIndex * 2) + colIndex
                            PersonalRecordCard(item.first, "${item.second} kg", Modifier.weight(1f)) {
                                openDialog("Editar ${item.first}", item.second, realIndex)
                            }
                        }
                        if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedButton(
                onClick = { navController.navigate("home") },
                modifier = Modifier.fillMaxWidth().height(50.dp).padding(bottom = 20.dp),
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

// --- COMPONENTES AUXILIARES ---

@Composable
fun EditValueDialog(title: String, initialValue: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf(initialValue) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        titleContentColor = TextWhite,
        textContentColor = TextWhite,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { if (it.length <= 5) text = it },
                label = { Text("Nuevo valor") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentOrangeSoft, unfocusedBorderColor = TextGray,
                    focusedTextColor = TextWhite, unfocusedTextColor = TextWhite,
                    cursorColor = AccentOrangeSoft, focusedLabelColor = AccentOrangeSoft, unfocusedLabelColor = TextGray
                )
            )
        },
        confirmButton = { Button(onClick = { onConfirm(text) }, colors = ButtonDefaults.buttonColors(containerColor = AccentOrangeSoft, contentColor = DarkText)) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = TextGray) } }
    )
}

@Composable
fun ProgresoItem(statName: String, statValue: String, progress: Float, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = DarkSurface), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(46.dp).background(color.copy(alpha = 0.15f), CircleShape), contentAlignment = Alignment.Center) { Icon(icon, null, tint = color, modifier = Modifier.size(24.dp)) }
                Spacer(modifier = Modifier.width(16.dp))
                Column { Text(statName, style = MaterialTheme.typography.bodyMedium, color = TextGray); Text(statValue, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = TextWhite) }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(progress = progress.coerceIn(0f, 1f), modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)), color = color, trackColor = Color.Black.copy(alpha = 0.3f), strokeCap = StrokeCap.Round)
        }
    }
}

@Composable
fun PersonalRecordCard(exercise: String, weight: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = modifier, colors = CardDefaults.cardColors(containerColor = DarkSurface), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(weight, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = AccentAmber)
            Text(exercise, style = MaterialTheme.typography.labelMedium, color = TextGray, textAlign = TextAlign.Center, maxLines = 1)
        }
    }
}

@Composable
fun WeeklyStreakSection() {
    val days = listOf("L", "M", "M", "J", "V", "S", "D")
    // Nota: Para hacer persistente la racha (activeDays), necesitarías una lógica similar con saveUserStat
    // Por ahora lo dejaremos visual/local por simplicidad.
    val activeDays = remember { mutableStateListOf(true, true, false, true, true, false, false) }
    Card(colors = CardDefaults.cardColors(containerColor = DarkSurface), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            days.forEachIndexed { index, day ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { activeDays[index] = !activeDays[index] }) {
                    Text(day, color = TextGray, style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(if (activeDays[index]) AccentOrangeSoft else Color.Transparent).border(1.dp, if (activeDays[index]) Color.Transparent else TextGray.copy(0.5f), CircleShape), contentAlignment = Alignment.Center) {
                        if (activeDays[index]) Icon(Icons.Default.Check, null, tint = DarkText, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}