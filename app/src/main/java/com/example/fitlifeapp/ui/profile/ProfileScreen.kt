package com.example.fitlifeapp.ui.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.fitlifeapp.AvatarStorage
import com.example.fitlifeapp.R
import com.example.fitlifeapp.data.local.SessionManager
import kotlinx.coroutines.launch

// colores del tema "sunset dark"
private val DarkBackground = Color(0xFF121212)
private val DarkSurface = Color(0xFF252525)
private val AccentOrange = Color(0xFFFFAB91)
private val AccentAmber = Color(0xFFFFE082)
private val TextWhite = Color(0xFFEEEEEE)
private val TextGray = Color(0xFFAAAAAA)
private val ErrorRed = Color(0xFFEF9A9A)
private val DarkText = Color(0xFF3E2723)

@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var localAvatarUri by remember { mutableStateOf(AvatarStorage.getPersistent(context)) }

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            if (backStackEntry.destination.route == "personalizacion") {
                localAvatarUri = AvatarStorage.getPersistent(context)
            }
        }
    }

    Scaffold(
        containerColor = DarkBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = AccentOrange)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Cargando perfil...", color = TextGray)
                    }
                }

                state.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Icon(Icons.Default.Warning, null, tint = ErrorRed, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Error al cargar", style = MaterialTheme.typography.titleLarge, color = ErrorRed)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(state.error ?: "", textAlign = TextAlign.Center, color = TextGray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadCurrentUser() },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentOrange, contentColor = DarkText)
                        ) {
                            Text("Reintentar")
                        }
                        TextButton(onClick = { navController.navigate("home") }) {
                            Text("Volver al inicio", color = TextGray)
                        }
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        val scale by animateFloatAsState(
                            targetValue = if (localAvatarUri != null) 1.05f else 1f,
                            animationSpec = tween(durationMillis = 600),
                            label = "avatarScale"
                        )

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(150.dp)
                                .scale(scale)
                                .border(
                                    width = 4.dp,
                                    brush = Brush.linearGradient(
                                        colors = listOf(AccentOrange, AccentAmber)
                                    ),
                                    shape = CircleShape
                                )
                                .clip(CircleShape)
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(localAvatarUri ?: R.mipmap.ic_launcher_round),
                                contentDescription = "Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // botón editar foto
                        Button(
                            onClick = { navController.navigate("camera_avatar") },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurface, contentColor = AccentOrange),
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AccentOrange)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cambiar foto")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Mi Perfil",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextWhite
                        )

                        // tarjetas de datos
                        ProfileDataCard("Nombre completo", state.userName.ifEmpty { "No disponible" }, Icons.Default.Person)
                        ProfileDataCard("Correo electrónico", state.userEmail.ifEmpty { "No disponible" }, Icons.Default.Email)

                        Spacer(modifier = Modifier.height(16.dp))

                        // acciones
                        // refrescar
                        OutlinedButton(
                            onClick = { viewModel.loadCurrentUser() },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TextGray)
                        ) {
                            if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = TextWhite)
                            else {
                                Icon(Icons.Default.Refresh, null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Refrescar datos")
                            }
                        }

                        // cerrar sesión
                        Button(
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
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ErrorRed.copy(alpha = 0.2f), contentColor = ErrorRed),
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ExitToApp, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cerrar sesión", fontWeight = FontWeight.Bold)
                        }

                        TextButton(onClick = { navController.navigate("home") }) {
                            Text("← Volver al inicio", color = TextGray)
                        }

                        OutlinedButton(
                            onClick = { navController.navigate("role_selector") },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AccentOrange)
                        ) {
                            Icon(Icons.Default.Person, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cambiar Rol (Demo)")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileDataCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(AccentOrange.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = AccentOrange)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelMedium, color = AccentAmber)
                Text(value, style = MaterialTheme.typography.bodyLarge, color = TextWhite)
            }
        }
    }
}
