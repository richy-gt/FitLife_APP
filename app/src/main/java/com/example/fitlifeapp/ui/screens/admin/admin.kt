package com.example.fitlifeapp.ui.screens.admin

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
private val Blue = Color(0xFF90CAF9)
private val Purple = Color(0xFFCE93D8)
private val TextW = Color(0xFFEEEEEE)
private val TextG = Color(0xFFAAAAAA)

// gestion de usuarios

data class Usuario(
    val name: String,
    val email: String,
    val role: String,
    val status: String,
    val fechaRegistro: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsuariosScreen(navController: NavHostController) {
    val usuarios = remember {
        mutableStateListOf(
            Usuario("Juan Pérez", "juan@example.com", "MIEMBRO", "Activo", "2024-01-15"),
            Usuario("Ana Torres", "ana@example.com", "ENTRENADOR", "Activo", "2024-02-20"),
            Usuario("Carlos Ruiz", "carlos@example.com", "NUTRICIONISTA", "Activo", "2024-03-10"),
            Usuario("María González", "maria@example.com", "MIEMBRO", "Inactivo", "2024-04-05"),
            Usuario("Pedro Silva", "pedro@example.com", "ADMINISTRADOR", "Activo", "2023-12-01")
        )
    }

    var filtroRole by remember { mutableStateOf("Todos") }
    var buscar by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<Usuario?>(null) }

    val filtrados = usuarios
        .filter { it.name.contains(buscar, ignoreCase = true) || it.email.contains(buscar, ignoreCase = true) }
        .filter { filtroRole == "Todos" || it.role == filtroRole }

    if (showDialog && selectedUser != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = DarkSurf,
            title = { Text("Cambiar Rol", color = TextW) },
            text = {
                Column {
                    Text("Selecciona el nuevo rol para ${selectedUser!!.name}:", color = TextG)
                    Spacer(Modifier.height(12.dp))
                    listOf("MIEMBRO", "ENTRENADOR", "NUTRICIONISTA", "ADMINISTRADOR").forEach { role ->
                        Card(
                            onClick = {
                                val index = usuarios.indexOf(selectedUser)
                                if (index != -1) {
                                    usuarios[index] = selectedUser!!.copy(role = role)
                                }
                                showDialog = false
                            },
                            colors = CardDefaults.cardColors(
                                if (role == selectedUser!!.role) Blue.copy(0.3f) else DarkBg
                            ),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Text(role, Modifier.padding(12.dp), color = TextW)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar", color = TextG)
                }
            }
        )
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Gestión de Usuarios", fontWeight = FontWeight.Bold, color = TextW) },
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

            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                AdminStatCard("Total", "${usuarios.size}", Icons.Default.Person, Modifier.weight(1f))
                AdminStatCard("Miembros", "${usuarios.count { it.role == "MIEMBRO" }}", Icons.Default.Person, Modifier.weight(1f))
                AdminStatCard("Staff", "${usuarios.count { it.role != "MIEMBRO" }}", Icons.Default.Star, Modifier.weight(1f))
            }

            Spacer(Modifier.height(16.dp))


            OutlinedTextField(
                value = buscar,
                onValueChange = { buscar = it },
                label = { Text("Buscar usuario...") },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Blue) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Blue,
                    unfocusedBorderColor = TextG,
                    focusedTextColor = TextW,
                    unfocusedTextColor = TextW
                )
            )

            Spacer(Modifier.height(12.dp))


            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("Todos", "MIEMBRO", "ENTRENADOR", "NUTRICIONISTA", "ADMINISTRADOR")) { f ->
                    FilterChip(
                        selected = filtroRole == f,
                        onClick = { filtroRole = f },
                        label = { Text(f, style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Blue,
                            containerColor = DarkSurf,
                            labelColor = TextG,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))


            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filtrados) { usuario ->
                    UsuarioCard(usuario) {
                        selectedUser = usuario
                        showDialog = true
                    }
                }
            }
        }
    }
}

@Composable
fun UsuarioCard(u: Usuario, onEdit: () -> Unit) {
    Card(colors = CardDefaults.cardColors(DarkSurf), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Column {
                    Text(u.name, fontWeight = FontWeight.Bold, color = TextW)
                    Text(u.email, color = TextG, style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, null, tint = Blue)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = getRoleColorAdmin(u.role),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        u.role,
                        Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Black
                    )
                }
                Surface(
                    color = if (u.status == "Activo") Color.Green.copy(0.2f) else Color.Red.copy(0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        u.status,
                        Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (u.status == "Activo") Color.Green else Color.Red
                    )
                }
            }
            Text("Registro: ${u.fechaRegistro}", color = TextG, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

fun getRoleColorAdmin(role: String): Color {
    return when (role) {
        "MIEMBRO" -> Color(0xFFFFAB91)
        "ENTRENADOR" -> Color(0xFF81C784)
        "NUTRICIONISTA" -> Color(0xFFFFE082)
        "ADMINISTRADOR" -> Color(0xFF90CAF9)
        else -> Color.Gray
    }
}

@Composable
fun AdminStatCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(colors = CardDefaults.cardColors(DarkSurf), modifier = modifier) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = Blue, modifier = Modifier.size(24.dp))
            Text(value, fontWeight = FontWeight.Bold, color = TextW, style = MaterialTheme.typography.titleMedium)
            Text(label, color = TextG, style = MaterialTheme.typography.labelSmall)
        }
    }
}

// reportes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReportesScreen(navController: NavHostController) {
    Scaffold(
        containerColor = DarkBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Reportes Globales", fontWeight = FontWeight.Bold, color = TextW) },
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
            Text("Estadísticas del Gimnasio", fontWeight = FontWeight.Bold, color = TextW, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))


            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
                MetricCard("Usuarios Activos", "1,234", Icons.Default.Person, Modifier.weight(1f))
                MetricCard("Ingresos Mes", "$2.5M", Icons.Default.ShoppingCart, Modifier.weight(1f))
            }

            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
                MetricCard("Planes Vendidos", "456", Icons.Default.Star, Modifier.weight(1f))
                MetricCard("Satisfacción", "4.8/5", Icons.Default.ThumbUp, Modifier.weight(1f))
            }

            Spacer(Modifier.height(24.dp))

            Text("Distribución por Rol", fontWeight = FontWeight.Bold, color = TextW)
            Spacer(Modifier.height(12.dp))

            RoleDistributionCard("Miembros", 850, 0xFF90CAF9)
            Spacer(Modifier.height(8.dp))
            RoleDistributionCard("Entrenadores", 45, 0xFF81C784)
            Spacer(Modifier.height(8.dp))
            RoleDistributionCard("Nutricionistas", 25, 0xFFFFE082)
            Spacer(Modifier.height(8.dp))
            RoleDistributionCard("Admins", 8, 0xFFCE93D8)

            Spacer(Modifier.height(24.dp))

            Text("Actividad Reciente", fontWeight = FontWeight.Bold, color = TextW)
            Spacer(Modifier.height(12.dp))

            ActivityCard("156 nuevos registros esta semana", Icons.Default.Person)
            Spacer(Modifier.height(8.dp))
            ActivityCard("3,450 sesiones de entrenamiento", Icons.Default.DateRange)
            Spacer(Modifier.height(8.dp))
            ActivityCard("890 consultas nutricionales", Icons.Default.Email)
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(colors = CardDefaults.cardColors(DarkSurf), modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, null, tint = Purple, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(8.dp))
            Text(value, fontWeight = FontWeight.Bold, color = TextW, style = MaterialTheme.typography.headlineMedium)
            Text(title, color = TextG, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun RoleDistributionCard(role: String, count: Int, colorHex: Long) {
    Card(colors = CardDefaults.cardColors(DarkSurf)) {
        Row(Modifier.padding(16.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(12.dp)
                        .background(Color(colorHex), CircleShape)
                )
                Spacer(Modifier.width(12.dp))
                Text(role, color = TextW)
            }
            Text("$count usuarios", color = TextG, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ActivityCard(text: String, icon: ImageVector) {
    Card(colors = CardDefaults.cardColors(DarkSurf)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Blue, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(12.dp))
            Text(text, color = TextW)
        }
    }
}

// configuración

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminConfigScreen(navController: NavHostController) {
    var notificaciones by remember { mutableStateOf(true) }
    var mantenimiento by remember { mutableStateOf(false) }
    var backupAuto by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Configuración", fontWeight = FontWeight.Bold, color = TextW) },
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
            Text("Configuración del Sistema", fontWeight = FontWeight.Bold, color = TextW, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            ConfigItem("Notificaciones Push", notificaciones) { notificaciones = it }
            Spacer(Modifier.height(12.dp))
            ConfigItem("Modo Mantenimiento", mantenimiento) { mantenimiento = it }
            Spacer(Modifier.height(12.dp))
            ConfigItem("Backup Automático", backupAuto) { backupAuto = it }

            Spacer(Modifier.height(24.dp))

            Text("Acciones Administrativas", fontWeight = FontWeight.Bold, color = TextW)
            Spacer(Modifier.height(12.dp))

            ActionButton("Generar Backup Manual", Icons.Default.Star) { /* Acción */ }
            Spacer(Modifier.height(12.dp))
            ActionButton("Limpiar Caché", Icons.Default.Clear) { /* Acción */ }
            Spacer(Modifier.height(12.dp))
            ActionButton("Exportar Reportes", Icons.Default.Send) { /* Acción */ }
        }
    }
}

@Composable
fun ConfigItem(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(colors = CardDefaults.cardColors(DarkSurf)) {
        Row(
            Modifier.padding(16.dp).fillMaxWidth(),
            Arrangement.SpaceBetween,
            Alignment.CenterVertically
        ) {
            Text(label, color = TextW)
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(checkedThumbColor = Blue)
            )
        }
    }
}

@Composable
fun ActionButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        colors = ButtonDefaults.buttonColors(Blue, Color.Black),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(icon, null)
        Spacer(Modifier.width(8.dp))
        Text(text)
    }
}