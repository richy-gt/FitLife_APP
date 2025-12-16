package com.example.fitlifeapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fitlifeapp.AvatarStorage
import com.example.fitlifeapp.data.local.SessionManager
import com.example.fitlifeapp.data.model.UserRole
import kotlinx.coroutines.launch

// colores
private val DarkBackground = Color(0xFF121212)
private val DarkSurface = Color(0xFF252525)
private val AccentOrangeSoft = Color(0xFFFFAB91)
private val AccentAmber = Color(0xFFFFE082)
private val TextWhite = Color(0xFFEEEEEE)
private val TextGray = Color(0xFFAAAAAA)
private val DarkText = Color(0xFF3E2723)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var userEmail by remember { mutableStateOf<String?>(null) }
    var userName by remember { mutableStateOf<String?>(null) }
    var userRole by remember { mutableStateOf(UserRole.MIEMBRO) }
    val scrollState = rememberScrollState()

    fun loadUserData() {
        scope.launch {
            val sessionManager = SessionManager(context)
            userEmail = sessionManager.getUserEmail()
            userName = sessionManager.getUserName()


            val roleString = sessionManager.getUserStat(userEmail ?: "", "role", "MIEMBRO")
            userRole = UserRole.fromString(roleString)
        }
    }

    LaunchedEffect(Unit) {
        loadUserData()
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "FitLife",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextWhite
                        )
                        // badge de rol
                        Surface(
                            color = getRoleColor(userRole),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = userRole.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextWhite
                ),
                actions = {
                    IconButton(onClick = { navController.navigate("personalizacion") }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Perfil",
                            tint = TextWhite,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // saludo
            Text(
                text = getGreetingByRole(userRole),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = TextWhite
            )
            if (userName != null) {
                Text(text = userName!!, style = MaterialTheme.typography.bodyMedium, color = TextGray)
            } else if (userEmail != null) {
                Text(text = userEmail!!, style = MaterialTheme.typography.bodyMedium, color = TextGray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // accesos segun rol
            when (userRole) {
                UserRole.MIEMBRO -> MiembroQuickAccess(navController)
                UserRole.ENTRENADOR -> EntrenadorQuickAccess(navController)
                UserRole.NUTRICIONISTA -> NutricionistaQuickAccess(navController)
                UserRole.ADMINISTRADOR -> AdminQuickAccess(navController)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // herramientas
            Text(
                text = "Herramientas",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                when (userRole) {
                    UserRole.MIEMBRO -> MiembroMenu(navController, scope, context)
                    UserRole.ENTRENADOR -> EntrenadorMenu(navController, scope, context)
                    UserRole.NUTRICIONISTA -> NutricionistaMenu(navController, scope, context)
                    UserRole.ADMINISTRADOR -> AdminMenu(navController, scope, context)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}



fun getRoleColor(role: UserRole): Color {
    return when (role) {
        UserRole.MIEMBRO -> Color(0xFFFFAB91) // naranja
        UserRole.ENTRENADOR -> Color(0xFF81C784) // verde
        UserRole.NUTRICIONISTA -> Color(0xFFFFE082) // amarillo
        UserRole.ADMINISTRADOR -> Color(0xFF90CAF9) // azul
    }
}

fun getGreetingByRole(role: UserRole): String {
    return when (role) {
        UserRole.MIEMBRO -> "¡Vamos a entrenar!"
        UserRole.ENTRENADOR -> "Panel de Entrenador"
        UserRole.NUTRICIONISTA -> "Panel de Nutrición"
        UserRole.ADMINISTRADOR -> "Panel de Administración"
    }
}



@Composable
fun MiembroQuickAccess(navController: NavHostController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HighlightCard(
            title = "Rutina",
            subtitle = "Ir al gym",
            backgroundColor = Color(0xFFFFAB91),
            icon = Icons.Default.PlayArrow,
            modifier = Modifier.weight(1f)
        ) { navController.navigate("plan_entrenamiento") }

        HighlightCard(
            title = "Dieta",
            subtitle = "Plan de comidas",
            backgroundColor = Color(0xFFFFE082),
            icon = Icons.Default.List,
            modifier = Modifier.weight(1f)
        ) { navController.navigate("plan_nutricional") }
    }
}

@Composable
fun EntrenadorQuickAccess(navController: NavHostController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HighlightCard(
            title = "Clientes",
            subtitle = "Gestionar clientes",
            backgroundColor = Color(0xFF81C784),
            icon = Icons.Default.Person,
            modifier = Modifier.weight(1f)
        ) { navController.navigate("entrenador_clientes") }

        HighlightCard(
            title = "Rutinas",
            subtitle = "Crear planes",
            backgroundColor = Color(0xFFFFAB91),
            icon = Icons.Default.Add,
            modifier = Modifier.weight(1f)
        ) { navController.navigate("entrenador_rutinas") }
    }
}

@Composable
fun NutricionistaQuickAccess(navController: NavHostController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HighlightCard(
            title = "Consultas",
            subtitle = "Ver solicitudes",
            backgroundColor = Color(0xFFFFE082),
            icon = Icons.Default.Email,
            modifier = Modifier.weight(1f)
        ) { navController.navigate("nutricionista_consultas") }

        HighlightCard(
            title = "Planes",
            subtitle = "Crear dietas",
            backgroundColor = Color(0xFFFFAB91),
            icon = Icons.Default.Add,
            modifier = Modifier.weight(1f)
        ) { navController.navigate("nutricionista_planes") }
    }
}

@Composable
fun AdminQuickAccess(navController: NavHostController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HighlightCard(
            title = "Usuarios",
            subtitle = "Gestión de usuarios",
            backgroundColor = Color(0xFF90CAF9),
            icon = Icons.Default.Person,
            modifier = Modifier.weight(1f)
        ) { navController.navigate("admin_usuarios") }

        HighlightCard(
            title = "Reportes",
            subtitle = "Estadísticas",
            backgroundColor = Color(0xFFCE93D8),
            icon = Icons.Default.Info,
            modifier = Modifier.weight(1f)
        ) { navController.navigate("admin_reportes") }
    }
}



@Composable
fun MiembroMenu(navController: NavHostController, scope: kotlinx.coroutines.CoroutineScope, context: android.content.Context) {
    MenuListItem(text = "Consultar Entrenador", icon = Icons.Default.Face) { navController.navigate("entrenador") }
    MenuListItem(text = "Ver mi Progreso", icon = Icons.Default.ThumbUp) { navController.navigate("progreso") }
    MenuListItem(text = "Tienda de Productos", icon = Icons.Default.ShoppingCart) { navController.navigate("productos") }
    MenuListItem(text = "Cerrar Sesión", icon = Icons.Default.ExitToApp) {
        scope.launch {
            val sessionManager = SessionManager(context)
            sessionManager.logout()
            AvatarStorage.clear(context)
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
}

@Composable
fun EntrenadorMenu(navController: NavHostController, scope: kotlinx.coroutines.CoroutineScope, context: android.content.Context) {
    MenuListItem(text = "Mis Clientes", icon = Icons.Default.Person) { navController.navigate("entrenador_clientes") }
    MenuListItem(text = "Crear Rutina", icon = Icons.Default.Add) { navController.navigate("entrenador_rutinas") }
    MenuListItem(text = "Historial", icon = Icons.Default.DateRange) { navController.navigate("entrenador_historial") }
    MenuListItem(text = "Tienda", icon = Icons.Default.ShoppingCart) { navController.navigate("productos") }
    MenuListItem(text = "Cerrar Sesión", icon = Icons.Default.ExitToApp) {
        scope.launch {
            SessionManager(context).logout()
            AvatarStorage.clear(context)
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }
}

@Composable
fun NutricionistaMenu(navController: NavHostController, scope: kotlinx.coroutines.CoroutineScope, context: android.content.Context) {
    MenuListItem(text = "Consultas Activas", icon = Icons.Default.Email) { navController.navigate("nutricionista_consultas") }
    MenuListItem(text = "Crear Plan", icon = Icons.Default.Add) { navController.navigate("nutricionista_planes") }
    MenuListItem(text = "Base de Alimentos", icon = Icons.Default.List) { navController.navigate("nutricionista_alimentos") }
    MenuListItem(text = "Tienda", icon = Icons.Default.ShoppingCart) { navController.navigate("productos") }
    MenuListItem(text = "Cerrar Sesión", icon = Icons.Default.ExitToApp) {
        scope.launch {
            SessionManager(context).logout()
            AvatarStorage.clear(context)
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }
}

@Composable
fun AdminMenu(navController: NavHostController, scope: kotlinx.coroutines.CoroutineScope, context: android.content.Context) {
    MenuListItem(text = "Gestión de Usuarios", icon = Icons.Default.Person) { navController.navigate("admin_usuarios") }
    MenuListItem(text = "Reportes Globales", icon = Icons.Default.Info) { navController.navigate("admin_reportes") }
    MenuListItem(text = "Configuración", icon = Icons.Default.Settings) { navController.navigate("admin_config") }
    MenuListItem(text = "Tienda", icon = Icons.Default.ShoppingCart) { navController.navigate("productos") }
    MenuListItem(text = "Cerrar Sesión", icon = Icons.Default.ExitToApp) {
        scope.launch {
            SessionManager(context).logout()
            AvatarStorage.clear(context)
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }
}



@Composable
fun HighlightCard(
    title: String,
    subtitle: String,
    backgroundColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Column(modifier = Modifier.align(Alignment.TopStart)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = DarkText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkText.copy(alpha = 0.7f)
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DarkText.copy(alpha = 0.15f),
                modifier = Modifier.align(Alignment.BottomEnd).size(56.dp).offset(x = 8.dp, y = 8.dp)
            )
        }
    }
}

@Composable
fun MenuListItem(
    text: String,
    icon: ImageVector,
    textColor: Color = TextWhite,
    iconColor: Color = AccentOrangeSoft,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(65.dp).clickable(onClick = onClick),
        color = DarkSurface,
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(38.dp).background(iconColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = text, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium), color = textColor)
            }
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = TextGray, modifier = Modifier.size(18.dp))
        }
    }
}