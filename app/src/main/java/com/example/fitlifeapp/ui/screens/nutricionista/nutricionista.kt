package com.example.fitlifeapp.ui.screens.nutricionista

import androidx.compose.animation.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController

// === COLORES ===
private val DarkBg = Color(0xFF121212)
private val DarkSurf = Color(0xFF252525)
private val Yellow = Color(0xFFFFE082)
private val Orange = Color(0xFFFFAB91)
private val Green = Color(0xFF81C784)
private val Red = Color(0xFFEF9A9A)
private val TextW = Color(0xFFEEEEEE)
private val TextG = Color(0xFFAAAAAA)

// === MODELOS DE DATOS ===
data class ClienteNutricional(
    val id: String,
    val nombre: String,
    val objetivo: String,
    val peso: Double,
    val pesoObjetivo: Double,
    val altura: Int, // en cm
    val edad: Int,
    val sexo: String,
    val planActual: String?,
    val caloriasObjetivo: Int,
    val ultimaConsulta: String,
    val adherencia: Int, // 0-100
    val status: String // "Activo", "Pausado", "Completado"
)

data class Medicion(
    val fecha: String,
    val peso: Double,
    val grasaCorporal: Double?,
    val circunferenciaCintura: Int?, // en cm
    val notas: String
)

// === PANTALLA PRINCIPAL ===
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutricionistaClientesScreen(navController: NavHostController) {
    // Datos de ejemplo (en producción vendrían de la API)
    val clientes = remember {
        mutableStateListOf(
            ClienteNutricional(
                id = "1",
                nombre = "Ana López",
                objetivo = "Pérdida de grasa",
                peso = 72.5,
                pesoObjetivo = 65.0,
                altura = 165,
                edad = 28,
                sexo = "F",
                planActual = "Plan Déficit 1800 kcal",
                caloriasObjetivo = 1800,
                ultimaConsulta = "2025-01-10",
                adherencia = 85,
                status = "Activo"
            ),
            ClienteNutricional(
                id = "2",
                nombre = "Carlos Ruiz",
                objetivo = "Ganancia muscular",
                peso = 68.0,
                pesoObjetivo = 75.0,
                altura = 178,
                edad = 32,
                sexo = "M",
                planActual = "Plan Superávit 2800 kcal",
                caloriasObjetivo = 2800,
                ultimaConsulta = "2025-01-08",
                adherencia = 92,
                status = "Activo"
            ),
            ClienteNutricional(
                id = "3",
                nombre = "María Torres",
                objetivo = "Mantenimiento",
                peso = 58.0,
                pesoObjetivo = 58.0,
                altura = 160,
                edad = 25,
                sexo = "F",
                planActual = "Plan Balance 2000 kcal",
                caloriasObjetivo = 2000,
                ultimaConsulta = "2024-12-15",
                adherencia = 65,
                status = "Pausado"
            ),
            ClienteNutricional(
                id = "4",
                nombre = "Pedro Silva",
                objetivo = "Definición",
                peso = 82.0,
                pesoObjetivo = 78.0,
                altura = 175,
                edad = 30,
                sexo = "M",
                planActual = "Plan Alto Proteína 2200 kcal",
                caloriasObjetivo = 2200,
                ultimaConsulta = "2025-01-12",
                adherencia = 78,
                status = "Activo"
            ),
            ClienteNutricional(
                id = "5",
                nombre = "Laura Gómez",
                objetivo = "Pérdida de peso",
                peso = 85.0,
                pesoObjetivo = 70.0,
                altura = 168,
                edad = 35,
                sexo = "F",
                planActual = null,
                caloriasObjetivo = 1600,
                ultimaConsulta = "2025-01-05",
                adherencia = 45,
                status = "Activo"
            )
        )
    }

    var busqueda by remember { mutableStateOf("") }
    var filtroStatus by remember { mutableStateOf("Todos") }
    var selectedCliente by remember { mutableStateOf<ClienteNutricional?>(null) }
    var showDetalles by remember { mutableStateOf(false) }

    val clientesFiltrados = clientes
        .filter { it.nombre.contains(busqueda, ignoreCase = true) }
        .filter { filtroStatus == "Todos" || it.status == filtroStatus }

    // === DIÁLOGO DE DETALLES ===
    if (showDetalles && selectedCliente != null) {
        ClienteDetallesDialog(
            cliente = selectedCliente!!,
            onDismiss = { showDetalles = false }
        )
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Mis Clientes", fontWeight = FontWeight.Bold, color = TextW)
                        Text(
                            "Seguimiento Nutricional",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextG
                        )
                    }
                },
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
        ) {
            // === MÉTRICAS RÁPIDAS ===
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricaMiniCard(
                    label = "Total",
                    value = "${clientes.size}",
                    icon = Icons.Default.Person,
                    color = Yellow,
                    modifier = Modifier.weight(1f)
                )
                MetricaMiniCard(
                    label = "Activos",
                    value = "${clientes.count { it.status == "Activo" }}",
                    icon = Icons.Default.Check,
                    color = Green,
                    modifier = Modifier.weight(1f)
                )
                MetricaMiniCard(
                    label = "Adherencia",
                    value = "${clientes.filter { it.status == "Activo" }.map { it.adherencia }.average().toInt()}%",
                    icon = Icons.Default.Star,
                    color = Orange,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            // === BÚSQUEDA ===
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                label = { Text("Buscar cliente...") },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Yellow) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Yellow,
                    unfocusedBorderColor = TextG,
                    focusedTextColor = TextW,
                    unfocusedTextColor = TextW,
                    cursorColor = Yellow
                )
            )

            Spacer(Modifier.height(12.dp))

            // === FILTROS ===
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Todos", "Activo", "Pausado", "Completado").forEach { status ->
                    FilterChip(
                        selected = filtroStatus == status,
                        onClick = { filtroStatus = status },
                        label = { Text(status) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Yellow,
                            containerColor = DarkSurf,
                            labelColor = TextG,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // === LISTA DE CLIENTES ===
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(clientesFiltrados) { cliente ->
                    ClienteNutricionalCard(
                        cliente = cliente,
                        onClick = {
                            selectedCliente = cliente
                            showDetalles = true
                        }
                    )
                }

                if (clientesFiltrados.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Search,
                                    null,
                                    tint = TextG,
                                    modifier = Modifier.size(64.dp)
                                )
                                Text(
                                    "No se encontraron clientes",
                                    color = TextG,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// === CARD DE CLIENTE ===
@Composable
fun ClienteNutricionalCard(
    cliente: ClienteNutricional,
    onClick: () -> Unit
) {
    val imc = cliente.peso / ((cliente.altura / 100.0) * (cliente.altura / 100.0))
    val progreso = if (cliente.objetivo.contains("Pérdida") || cliente.objetivo.contains("Definición")) {
        ((cliente.peso - cliente.pesoObjetivo) / (cliente.peso - cliente.pesoObjetivo)).coerceIn(0.0, 1.0)
    } else {
        ((cliente.peso - cliente.pesoObjetivo) / (cliente.pesoObjetivo - cliente.peso)).coerceIn(0.0, 1.0)
    }

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(DarkSurf),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            // === HEADER ===
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        cliente.nombre,
                        fontWeight = FontWeight.Bold,
                        color = TextW,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        cliente.objetivo,
                        color = Yellow,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                StatusBadge(cliente.status)
            }

            Spacer(Modifier.height(12.dp))

            // === DATOS CLAVE ===
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoChip(
                    label = "Peso",
                    value = "${cliente.peso} kg",
                    icon = Icons.Default.Person
                )
                InfoChip(
                    label = "Objetivo",
                    value = "${cliente.pesoObjetivo} kg",
                    icon = Icons.Default.Star
                )
                InfoChip(
                    label = "IMC",
                    value = String.format("%.1f", imc),
                    icon = Icons.Default.Info
                )
            }

            Spacer(Modifier.height(12.dp))

            // === PLAN ACTUAL ===
            if (cliente.planActual != null) {
                Surface(
                    color = Yellow.copy(0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.List,
                            null,
                            tint = Yellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            cliente.planActual,
                            color = TextW,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            } else {
                Surface(
                    color = Red.copy(0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            null,
                            tint = Red,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Sin plan asignado",
                            color = Red,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // === ADHERENCIA ===
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Adherencia: ${cliente.adherencia}%",
                    color = TextG,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    "Última consulta: ${cliente.ultimaConsulta}",
                    color = TextG,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = cliente.adherencia / 100f,
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = when {
                    cliente.adherencia >= 80 -> Green
                    cliente.adherencia >= 60 -> Yellow
                    else -> Red
                },
                trackColor = Color.Black.copy(0.3f)
            )
        }
    }
}

// === BADGE DE STATUS ===
@Composable
fun StatusBadge(status: String) {
    val (color, bgColor) = when (status) {
        "Activo" -> Green to Green.copy(0.2f)
        "Pausado" -> Orange to Orange.copy(0.2f)
        "Completado" -> Color(0xFF90CAF9) to Color(0xFF90CAF9).copy(0.2f)
        else -> TextG to TextG.copy(0.2f)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            status,
            Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

// === INFO CHIP ===
@Composable
fun InfoChip(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = Yellow, modifier = Modifier.size(16.dp))
        Text(
            value,
            fontWeight = FontWeight.Bold,
            color = TextW,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            label,
            color = TextG,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

// === MÉTRICA MINI ===
@Composable
fun MetricaMiniCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(DarkSurf),
        modifier = modifier
    ) {
        Column(
            Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(4.dp))
            Text(
                value,
                fontWeight = FontWeight.Bold,
                color = TextW,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                label,
                color = TextG,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

// === DIÁLOGO DE DETALLES ===
@Composable
fun ClienteDetallesDialog(
    cliente: ClienteNutricional,
    onDismiss: () -> Unit
) {
    val mediciones = remember {
        listOf(
            Medicion("2025-01-10", 72.5, 28.5, 78, "Se mantiene bien, cumple macros"),
            Medicion("2024-12-27", 73.8, 29.2, 80, "Festivos, ligero aumento"),
            Medicion("2024-12-13", 73.2, 28.8, 79, "Progreso constante"),
            Medicion("2024-11-29", 74.5, 30.1, 82, "Primera consulta")
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(DarkSurf),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp)
        ) {
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // === HEADER ===
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            cliente.nombre,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextW
                        )
                        Text(
                            "${cliente.sexo} • ${cliente.edad} años • ${cliente.altura} cm",
                            color = TextG,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, null, tint = TextG)
                    }
                }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = TextG.copy(0.3f))
                Spacer(Modifier.height(16.dp))

                // === OBJETIVO Y CALORÍAS ===
                Text("Plan Actual", fontWeight = FontWeight.Bold, color = Yellow)
                Spacer(Modifier.height(8.dp))

                if (cliente.planActual != null) {
                    Surface(
                        color = Yellow.copy(0.1f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(cliente.planActual, fontWeight = FontWeight.Bold, color = TextW)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "${cliente.caloriasObjetivo} kcal/día",
                                color = Orange,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "Objetivo: ${cliente.objetivo}",
                                color = TextG,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                } else {
                    Surface(
                        color = Red.copy(0.1f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, null, tint = Red)
                            Spacer(Modifier.width(8.dp))
                            Text("Asignar plan nutricional", color = Red, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // === HISTORIAL DE MEDICIONES ===
                Text("Historial de Mediciones", fontWeight = FontWeight.Bold, color = Yellow)
                Spacer(Modifier.height(8.dp))

                mediciones.forEach { medicion ->
                    MedicionItem(medicion)
                    Spacer(Modifier.height(8.dp))
                }

                Spacer(Modifier.height(16.dp))

                // === ACCIONES ===
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { /* Editar plan */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(Yellow, Color.Black)
                    ) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Editar Plan")
                    }
                    OutlinedButton(
                        onClick = { /* Agendar consulta */ },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, Yellow)
                    ) {
                        Icon(Icons.Default.DateRange, null, tint = Yellow, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Agendar", color = Yellow)
                    }
                }
            }
        }
    }
}

// === ITEM DE MEDICIÓN ===
@Composable
fun MedicionItem(medicion: Medicion) {
    Card(
        colors = CardDefaults.cardColors(DarkBg),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    medicion.fecha,
                    fontWeight = FontWeight.Bold,
                    color = TextW,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "${medicion.peso} kg",
                    color = Orange,
                    fontWeight = FontWeight.Bold
                )
            }

            if (medicion.grasaCorporal != null || medicion.circunferenciaCintura != null) {
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    medicion.grasaCorporal?.let {
                        Text("Grasa: ${it}%", color = TextG, style = MaterialTheme.typography.labelSmall)
                    }
                    medicion.circunferenciaCintura?.let {
                        Text("Cintura: ${it} cm", color = TextG, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            if (medicion.notas.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    medicion.notas,
                    color = TextG,
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}