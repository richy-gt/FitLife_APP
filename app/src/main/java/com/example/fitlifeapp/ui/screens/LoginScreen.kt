package com.example.fitlifeapp.ui.screens

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.ui.platform.LocalContext
import com.example.fitlifeapp.data.local.UserPreferences
import com.example.fitlifeapp.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

// ✅ Validar correo (solo para login)
fun validateEmailLogin(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    // 🔥 Cuando el login sea exitoso
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            scope.launch {
                // Guardamos usuario y estado de sesión persistente
                userPrefs.saveUserEmail(username)
                userPrefs.saveUserPassword(password)
                userPrefs.saveLoginState(true)
            }

            // Navegamos al home y limpiamos el backstack
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ----------------------------
        // TÍTULOS
        // ----------------------------
        Text(
            text = "🏋️ FitLife",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Inicio de Sesión",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // ----------------------------
        // CAMPO CORREO
        // ----------------------------
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                emailError = if (username.isNotEmpty() && !validateEmailLogin(username)) {
                    "Ingrese un correo válido (ejemplo: usuario@gmail.com)"
                } else null
            },
            label = { Text("Correo electrónico") },
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = emailError != null
        )

        if (emailError != null) {
            Text(
                emailError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ----------------------------
        // CAMPO CONTRASEÑA
        // ----------------------------
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ----------------------------
        // BOTÓN INGRESAR
        // ----------------------------
        Button(
            onClick = {
                if (emailError == null && username.isNotBlank() && password.isNotBlank()) {
                    viewModel.login(username, password)
                }
            },
            enabled = !uiState.isLoading && username.isNotBlank() && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (uiState.isLoading) "Iniciando..." else "Ingresar")
        }

        // ----------------------------
        // MENSAJE DE ERROR
        // ----------------------------
        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "❌ ${uiState.errorMessage}",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ----------------------------
        // LINK A REGISTRO
        // ----------------------------
        TextButton(
            onClick = { navController.navigate("register") },
            enabled = !uiState.isLoading
        ) {
            Text("¿No tienes cuenta? Regístrate")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ----------------------------
        // CREDENCIALES DE PRUEBA
        // ----------------------------
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "💡 Credenciales de prueba:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "Usuario: emilys\nContraseña: emilyspass",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}
