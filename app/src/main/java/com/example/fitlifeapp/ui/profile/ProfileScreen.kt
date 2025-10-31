package com.example.fitlifeapp.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.fitlifeapp.AvatarPreferences
import com.example.fitlifeapp.R
import com.example.fitlifeapp.AvatarStorage // ✅ para persistencia
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import com.example.fitlifeapp.data.local.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // ✅ carga la imagen persistente o la última guardada
    var avatarUri by remember {
        mutableStateOf(
            AvatarStorage.getPersistent(context)
                ?: AvatarPreferences.obtenerAvatarUri(context)
        )
    }

    // 🔁 refresca automáticamente al volver desde la cámara
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            if (backStackEntry.destination.route == "profile" ||
                backStackEntry.destination.route == "personalizacion"
            ) {
                avatarUri = AvatarStorage.getPersistent(context)
                    ?: AvatarPreferences.obtenerAvatarUri(context)
            }
        }
    }

    // 🔄 carga los datos del usuario al iniciar (para el ViewModel)
    LaunchedEffect(Unit) {
        viewModel.loadUser(1)
    }

    // 📨 Leer correo real desde DataStore
    var userEmail by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        scope.launch {
            val prefs = UserPreferences(context)
            val user = prefs.getUser().first()
            userEmail = user.first // el primer valor del Pair es el correo
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            state.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

            state.error != null -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "❌ Error",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.error ?: "",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadUser(1) }) { Text("Reintentar") }
                }
            }

            else -> {
                Column(
                    modifier = Modifier.align(Alignment.TopCenter),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 🧍‍♂️ Avatar animado con borde brillante
                    val scale by animateFloatAsState(
                        targetValue = if (avatarUri != null) 1.05f else 1f,
                        animationSpec = tween(durationMillis = 600),
                        label = "avatarScale"
                    )

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(130.dp)
                            .scale(scale)
                            .border(
                                width = 4.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(Color.Cyan, Color.Magenta, Color.Blue)
                                ),
                                shape = CircleShape
                            )
                            .clip(CircleShape)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(avatarUri ?: R.mipmap.ic_launcher_round),
                            contentDescription = "Avatar del usuario",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                        )
                    }

                    Button(onClick = { navController.navigate("camera_avatar") }) {
                        Text("Cambiar avatar 📸")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Perfil de Usuario",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 🧩 Mostrar correo real desde DataStore
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Correo registrado",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (userEmail != null) {
                                Text(userEmail!!, style = MaterialTheme.typography.bodyLarge)
                            } else {
                                Text("Cargando...", color = Color.Gray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 🔄 Botón para refrescar datos
                    Button(onClick = { viewModel.loadUser(1) }) {
                        Text("Refrescar datos")
                    }

                    // 🚪 Botón para cerrar sesión
                    Button(
                        onClick = {
                            scope.launch {
                                val prefs = UserPreferences(context)
                                prefs.saveLoginState(false) // marcar como deslogueado
                                navController.navigate("login") {
                                    popUpTo("personalizacion") { inclusive = true }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Cerrar sesión", color = Color.White)
                    }
                }
            }
        }
    }
}
