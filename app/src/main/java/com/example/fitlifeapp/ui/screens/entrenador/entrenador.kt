package com.example.fitlifeapp.ui.screens.entrenador

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

// colores
private val DarkBg = Color(0xFF121212)
private val DarkSurf = Color(0xFF252525)
private val Green = Color(0xFF81C784)
private val Orange = Color(0xFFFFAB91)
private val TextW = Color(0xFFEEEEEE)
private val TextG = Color(0xFFAAAAAA)

// gestion de clientes ===

data class Cliente(val name: String, val objetivo: String, val progreso: Int, val status: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntrenadorClientesScreen(navController: NavHostController) {
    val clientes = remember {
        listOf(
            Cliente("Juan Pérez", "Pérdida de peso", 65, "Activo"),
            Cliente("María González", "Ganar masa", 80, "Activo"),
            Cliente("Carlos Ruiz", "Mantenimiento", 45, "Inactivo"),
            Cliente("Ana Torres", "Fuerza", 90, "Activo"),
            Cliente("Pedro Silva", "Resistencia", 55, "Activo")
        )
    }

    var filtro by remember { mutableStateOf("Todos") }

    val filtrados = when (filtro) {
        "Activos" -> clientes.filter { it.status == "Activo" }
        "Inactivos" -> clientes.filter { it.status == "Inactivo" }
        else -> clientes
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis Clientes", fontWeight = FontWeight.Bold, color = TextW) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = TextW)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(DarkBg)
            )
        }
    ) { p ->
        Column(Modifier.padding(p).padding(16.dp)) {

            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
                StatsCard("Total", "${clientes.size}", Icons.Default.Person, Modifier.weight(1f))
                StatsCard("Activos", "${clientes.count { it.status == "Activo" }}", Icons.Default.Check, Modifier.weight(1f))
                StatsCard("Inactivos", "${clientes.count { it.status == "Inactivo" }}", Icons.Default.Close, Modifier.weight(1f))
            }

            Spacer(Modifier.height(16.dp))


            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Todos", "Activos", "Inactivos").forEach { f ->
                    FilterChip(
                        selected = filtro == f,
                        onClick = { filtro = f },
                        label = { Text(f) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Green,
                            containerColor = DarkSurf,
                            labelColor = TextG,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))


            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filtrados) { cliente ->
                    ClienteCard(cliente)
                }
            }
        }
    }
}

@Composable
fun ClienteCard(c: Cliente) {
    Card(
        colors = CardDefaults.cardColors(DarkSurf),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Column {
                    Text(c.name, fontWeight = FontWeight.Bold, color = TextW)
                    Text(c.objetivo, color = TextG, style = MaterialTheme.typography.bodySmall)
                }
                Surface(
                    color = if (c.status == "Activo") Green.copy(0.2f) else Color.Red.copy(0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        c.status,
                        Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (c.status == "Activo") Green else Color.Red
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Progreso del plan: ${c.progreso}%", color = TextG, style = MaterialTheme.typography.labelSmall)
            LinearProgressIndicator(
                progress = c.progreso / 100f,
                Modifier.fillMaxWidth().height(6.dp),
                color = Green,
                trackColor = Color.Black.copy(0.3f)
            )
        }
    }
}

@Composable
fun StatsCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(colors = CardDefaults.cardColors(DarkSurf), modifier = modifier) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = Green, modifier = Modifier.size(24.dp))
            Text(value, fontWeight = FontWeight.Bold, color = TextW, style = MaterialTheme.typography.titleMedium)
            Text(label, color = TextG, style = MaterialTheme.typography.labelSmall)
        }
    }
}

// crear rutinas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntrenadorRutinasScreen(navController: NavHostController) {
    var nombreRutina by remember { mutableStateOf("") }
    var objetivo by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }

    val ejercicios = remember { mutableStateListOf<String>() }
    var nuevoEjercicio by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Crear Rutina", fontWeight = FontWeight.Bold, color = TextW) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = TextW)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(DarkBg)
            )
        }
    ) { p ->
        Column(
            Modifier
                .padding(p)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (showSuccess) {
                Card(colors = CardDefaults.cardColors(Green.copy(0.2f))) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Check, null, tint = Green)
                        Spacer(Modifier.width(8.dp))
                        Text("¡Rutina creada exitosamente!", color = Green)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            Text("Información de la Rutina", fontWeight = FontWeight.Bold, color = TextW)
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = nombreRutina,
                onValueChange = { nombreRutina = it },
                label = { Text("Nombre de la rutina") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Green,
                    unfocusedBorderColor = TextG,
                    focusedTextColor = TextW,
                    unfocusedTextColor = TextW
                )
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = objetivo,
                onValueChange = { objetivo = it },
                label = { Text("Objetivo (Ej: Hipertrofia)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Green,
                    unfocusedBorderColor = TextG,
                    focusedTextColor = TextW,
                    unfocusedTextColor = TextW
                )
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = duracion,
                onValueChange = { duracion = it },
                label = { Text("Duración (Ej: 8 semanas)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Green,
                    unfocusedBorderColor = TextG,
                    focusedTextColor = TextW,
                    unfocusedTextColor = TextW
                )
            )

            Spacer(Modifier.height(24.dp))

            Text("Ejercicios", fontWeight = FontWeight.Bold, color = TextW)
            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nuevoEjercicio,
                    onValueChange = { nuevoEjercicio = it },
                    label = { Text("Ejercicio") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Green,
                        unfocusedBorderColor = TextG,
                        focusedTextColor = TextW,
                        unfocusedTextColor = TextW
                    )
                )
                IconButton(
                    onClick = {
                        if (nuevoEjercicio.isNotBlank()) {
                            ejercicios.add(nuevoEjercicio)
                            nuevoEjercicio = ""
                        }
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .background(Green, RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.Black)
                }
            }

            Spacer(Modifier.height(12.dp))

            ejercicios.forEachIndexed { index, ejercicio ->
                Card(
                    colors = CardDefaults.cardColors(DarkSurf),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier.padding(12.dp),
                        Arrangement.SpaceBetween,
                        Alignment.CenterVertically
                    ) {
                        Text("${index + 1}. $ejercicio", color = TextW)
                        IconButton(onClick = { ejercicios.removeAt(index) }) {
                            Icon(Icons.Default.Delete, null, tint = Color.Red)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (nombreRutina.isNotBlank() && objetivo.isNotBlank() && ejercicios.isNotEmpty()) {
                        showSuccess = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(Green, Color.Black),
                enabled = nombreRutina.isNotBlank() && objetivo.isNotBlank() && ejercicios.isNotEmpty()
            ) {
                Icon(Icons.Default.Check, null)
                Spacer(Modifier.width(8.dp))
                Text("Guardar Rutina")
            }
        }
    }
}

// historial

data class Sesion(val cliente: String, val fecha: String, val tipo: String, val duracion: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntrenadorHistorialScreen(navController: NavHostController) {
    val sesiones = remember {
        listOf(
            Sesion("Juan Pérez", "2025-01-10", "Fuerza", "60 min"),
            Sesion("María González", "2025-01-09", "Cardio", "45 min"),
            Sesion("Ana Torres", "2025-01-08", "Híbrido", "90 min"),
            Sesion("Pedro Silva", "2025-01-07", "Resistencia", "50 min"),
            Sesion("Juan Pérez", "2025-01-05", "Fuerza", "60 min")
        )
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Historial de Sesiones", fontWeight = FontWeight.Bold, color = TextW) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = TextW)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(DarkBg)
            )
        }
    ) { p ->
        LazyColumn(
            Modifier.padding(p).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sesiones) { sesion ->
                Card(colors = CardDefaults.cardColors(DarkSurf)) {
                    Row(
                        Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(sesion.cliente, fontWeight = FontWeight.Bold, color = TextW)
                            Text(sesion.tipo, color = Orange, style = MaterialTheme.typography.bodySmall)
                            Text(sesion.fecha, color = TextG, style = MaterialTheme.typography.labelSmall)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Icon(Icons.Default.DateRange, null, tint = Green, modifier = Modifier.size(20.dp))
                            Text(sesion.duracion, color = Green, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}