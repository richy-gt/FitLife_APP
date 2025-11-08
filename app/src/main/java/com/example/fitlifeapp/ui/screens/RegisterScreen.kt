package com.example.fitlifeapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.fitlifeapp.viewmodel.RegisterViewModel

// ----------------------------
// FUNCIONES DE VALIDACIÓN
// ----------------------------
fun isValidEmail(email: String): Boolean {
    // Verifica formato de correo (ej: algo@gmail.com)
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isValidName(name: String): Boolean {
    // Solo letras y espacios (mínimo 3)
    return name.matches(Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]{3,}$"))
}

fun isValidPassword(password: String): Boolean {
    // Al menos 8 caracteres, una mayúscula, un número y un carácter especial
    return password.matches(Regex("^(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}\$"))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val status by viewModel.status.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    // Errores visibles en tiempo real
    var emailError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Crear cuenta") },
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }

                uiState.isSuccess -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "✅ Registro exitoso",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            navController.navigate("login") {
                                popUpTo("register") { inclusive = true }
                            }
                        }) {
                            Text("Ir al login")
                        }
                    }
                }

                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // ----------------------------
                        // CAMPO NOMBRE
                        // ----------------------------
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                nameError = if (name.isNotEmpty() && !isValidName(name)) {
                                    "Ingrese un nombre válido (solo letras, sin números ni símbolos)"
                                } else null
                            },
                            label = { Text("Nombre completo") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            isError = nameError != null
                        )
                        if (nameError != null) {
                            Text(
                                nameError!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        // ----------------------------
                        // CAMPO CORREO
                        // ----------------------------
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                emailError = if (email.isNotEmpty() && !isValidEmail(email)) {
                                    "Ingrese un correo válido (ejemplo: usuario@gmail.com)"
                                } else null
                            },
                            label = { Text("Correo electrónico") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            isError = emailError != null
                        )
                        if (emailError != null) {
                            Text(
                                emailError!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        // ----------------------------
                        // CAMPO CONTRASEÑA
                        // ----------------------------
                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                passwordError = if (password.isNotEmpty() && !isValidPassword(password)) {
                                    "Debe tener 8 caracteres, una mayúscula, un número y un carácter especial"
                                } else null
                            },
                            label = { Text("Contraseña") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            isError = passwordError != null
                        )
                        if (passwordError != null) {
                            Text(
                                passwordError!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // ----------------------------
                        // BOTÓN REGISTRARSE
                        // ----------------------------
                        Button(
                            onClick = {
                                if (email.isBlank() || password.isBlank() || name.isBlank()) {
                                    Toast.makeText(
                                        navController.context,
                                        "Completa todos los campos",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else if (emailError == null && nameError == null && passwordError == null) {
                                    // Si todo es correcto
                                    viewModel.register(email, password, name)
                                } else {
                                    Toast.makeText(
                                        navController.context,
                                        "Corrige los errores antes de continuar",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = name.isNotBlank() && email.isNotBlank() && password.isNotBlank()
                        ) {
                            Text("Registrarse")
                        }

                        // ----------------------------
                        // MENSAJES DE ESTADO
                        // ----------------------------
                        if (uiState.errorMessage != null) {
                            Text(
                                text = "❌ ${uiState.errorMessage}",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { navController.navigate("login") }) {
                            Text("¿Ya tienes cuenta? Inicia sesión")
                        }
                    }
                }
            }
        }
    }
}
