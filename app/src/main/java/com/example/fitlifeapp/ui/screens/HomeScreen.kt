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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.fitlifeapp.AvatarStorage
import com.example.fitlifeapp.data.local.SessionManager
import kotlinx.coroutines.launch

// --- Colores del Tema "Sunset Dark" ---
private val DarkBackground = Color(0xFF121212)
private val DarkSurface = Color(0xFF252525)
private val AccentOrangeSoft = Color(0xFFFFAB91)    // Naranja Coral
private val AccentAmber = Color(0xFFFFE082)         // Ámbar/Crema
private val TextWhite = Color(0xFFEEEEEE)
private val TextGray = Color(0xFFAAAAAA)
private val DarkText = Color(0xFF3E2723)            // Color texto oscuro para contraste en botones claros

@Preview
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var userEmail by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    fun loadEmail() {
        scope.launch {
            val sessionManager = SessionManager(context)
            userEmail = sessionManager.getUserEmail()
        }
    }

    LaunchedEffect(Unit) {
        loadEmail()
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "FitLife",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite,
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextWhite
                ),
                actions = {
                    // ÚNICO ACCESO AL PERFIL (Icono superior derecha)
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
            horizontalAlignment = Alignment.Start,
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // --- Saludo ---
            Text(
                text = "¡Vamos a entrenar!",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = TextWhite
            )
            if (userEmail != null) {
                Text(
                    text = userEmail!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )
            } else {
                Text("Cargando...", color = TextGray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- ACCESOS DIRECTOS PRINCIPALES (Highlights) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tarjeta 1: ENTRENAMIENTO (Naranja)
                HighlightCard(
                    title = "Rutina",
                    subtitle = "Ir al gym",
                    backgroundColor = AccentOrangeSoft,
                    icon = Icons.Default.PlayArrow,
                    modifier = Modifier.weight(1f)
                ) {
                    navController.navigate("plan_entrenamiento")
                }

                // Tarjeta 2: NUTRICIÓN (Ámbar)
                HighlightCard(
                    title = "Dieta",
                    subtitle = "Plan de comidas",
                    backgroundColor = AccentAmber,
                    icon = Icons.Default.List,
                    modifier = Modifier.weight(1f)
                ) {
                    navController.navigate("plan_nutricional")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- HERRAMIENTAS (Lista inferior) ---
            Text(
                text = "Herramientas",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // Entrenador
                MenuListItem(
                    text = "Consultar Entrenador",
                    icon = Icons.Default.Face,
                    onClick = { navController.navigate("entrenador") }
                )

                // Progreso
                MenuListItem(
                    text = "Ver mi Progreso",
                    icon = Icons.Default.ThumbUp,
                    onClick = { navController.navigate("progreso") }
                )

                // Tienda
                MenuListItem(
                    text = "Tienda de Productos",
                    icon = Icons.Default.ShoppingCart,
                    onClick = { navController.navigate("productos") }
                )

                MenuListItem(
                    text = "Cerrar Sesion",
                    icon = Icons.Default.ExitToApp,

                    onClick = {
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
                )

                // Aquí eliminé el botón de "Cerrar sesión" y el divider que lo separaba.
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// ==========================================
// Componentes Reutilizables
// ==========================================

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
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(56.dp)
                    .offset(x = 8.dp, y = 8.dp)
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
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        color = DarkSurface,
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(iconColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = textColor
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = TextGray,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}