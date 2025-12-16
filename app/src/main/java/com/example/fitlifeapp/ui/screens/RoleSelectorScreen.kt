package com.example.fitlifeapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fitlifeapp.data.local.SessionManager
import com.example.fitlifeapp.data.model.UserRole
import kotlinx.coroutines.launch

// colores
private val DarkBg = Color(0xFF121212)
private val DarkSurf = Color(0xFF252525)
private val TextW = Color(0xFFEEEEEE)
private val TextG = Color(0xFFAAAAAA)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectorScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }

    var currentRole by remember { mutableStateOf(UserRole.MIEMBRO) }
    var showSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val email = sessionManager.getUserEmail() ?: "user@example.com"
        val roleString = sessionManager.getUserStat(email, "role", "MIEMBRO")
        currentRole = UserRole.fromString(roleString)
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cambiar Rol", fontWeight = FontWeight.Bold, color = TextW) },
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
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (showSuccess) {
                Card(colors = CardDefaults.cardColors(Color(0xFF81C784).copy(0.2f))) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Check, null, tint = Color(0xFF81C784))
                        Spacer(Modifier.width(8.dp))
                        Text("¡Rol actualizado! Vuelve al inicio", color = Color(0xFF81C784))
                    }
                }
            }

            Text(
                "Rol Actual: ${currentRole.displayName}",
                style = MaterialTheme.typography.titleLarge,
                color = TextW,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Selecciona un rol para probar las diferentes funcionalidades:",
                color = TextG
            )

            Spacer(Modifier.height(8.dp))


            RoleCard(
                role = UserRole.MIEMBRO,
                title = "Miembro",
                description = "Usuario básico con acceso a planes y progreso",
                icon = Icons.Default.Person,
                color = Color(0xFFFFAB91),
                isSelected = currentRole == UserRole.MIEMBRO
            ) {
                scope.launch {
                    val email = sessionManager.getUserEmail() ?: "user@example.com"
                    sessionManager.saveUserStat(email, "role", "MIEMBRO")
                    currentRole = UserRole.MIEMBRO
                    showSuccess = true
                }
            }

            RoleCard(
                role = UserRole.ENTRENADOR,
                title = "Entrenador",
                description = "Gestiona clientes, crea rutinas y ve estadísticas",
                icon = Icons.Default.Star,
                color = Color(0xFF81C784),
                isSelected = currentRole == UserRole.ENTRENADOR
            ) {
                scope.launch {
                    val email = sessionManager.getUserEmail() ?: "user@example.com"
                    sessionManager.saveUserStat(email, "role", "ENTRENADOR")
                    currentRole = UserRole.ENTRENADOR
                    showSuccess = true
                }
            }

            // nutricionista desactivado
            RoleCard(
                role = UserRole.NUTRICIONISTA,
                title = "Nutricionista (Próximamente)",
                description = "Esta función no está disponible por el momento.",
                icon = Icons.Default.Lock,
                color = Color.Gray, // usamos color.gray explicitamente
                isSelected = false,
                isEnabled = false
            ) {
               // accion vacia
            }

            RoleCard(
                role = UserRole.ADMINISTRADOR,
                title = "Administrador",
                description = "Control total: usuarios, reportes y configuración",
                icon = Icons.Default.Settings,
                color = Color(0xFF90CAF9),
                isSelected = currentRole == UserRole.ADMINISTRADOR
            ) {
                scope.launch {
                    val email = sessionManager.getUserEmail() ?: "user@example.com"
                    sessionManager.saveUserStat(email, "role", "ADMINISTRADOR")
                    currentRole = UserRole.ADMINISTRADOR
                    showSuccess = true
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF81C784),
                    contentColor = Color.Black
                )
            ) {
                Text("Volver al Inicio")
            }
        }
    }
}

@Composable
fun RoleCard(
    role: UserRole,
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isSelected: Boolean,
    isEnabled: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        enabled = isEnabled,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(0.3f) else DarkSurf,
            disabledContainerColor = DarkSurf.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = if (isSelected)
            androidx.compose.foundation.BorderStroke(2.dp, color)
        else null
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(50.dp)
                    .background(color.copy(0.2f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
            }

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    color = if (isEnabled) TextW else Color.Gray,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    description,
                    color = TextG,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

// nutricion no funciono nose porque y me quiero matar chao
