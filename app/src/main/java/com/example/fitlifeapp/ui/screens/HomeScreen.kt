
package com.example.fitlifeapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fitlifeapp.data.local.SessionManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var userEmail by remember { mutableStateOf<String?>(null) }

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
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "🏋️ FitLife",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Bienvenido a FitLife 💪",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (userEmail != null) {
                Text(
                    text = "Has iniciado sesión como: $userEmail",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            } else {
                Text(
                    text = "Cargando usuario...",
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.navigate("personalizacion") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ir a perfil 👤")
            }

            Button(
                onClick = { navController.navigate("entrenador") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Entrenador")
            }

            Button(
                onClick = { navController.navigate("plan_entrenamiento") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Plan de Entrenamiento")
            }

            Button(
                onClick = { navController.navigate("plan_nutricional") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Plan Nutricional")
            }

            Button(
                onClick = { navController.navigate("progreso") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Progreso")
            }


            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        val sessionManager = SessionManager(context)
                        sessionManager.logout()
                        navController.navigate("login") {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}
