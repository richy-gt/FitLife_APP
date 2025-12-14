package com.example.fitlifeapp.ui.screens

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.fitlifeapp.viewmodel.RegisterViewModel

// --- Colores del Tema "Sunset Dark" ---
private val DarkBackground = Color(0xFF121212)
private val DarkSurface = Color(0xFF252525)
private val AccentOrange = Color(0xFFFFAB91)
private val TextWhite = Color(0xFFEEEEEE)
private val TextGray = Color(0xFFAAAAAA)
private val DarkText = Color(0xFF3E2723)
private val ErrorRed = Color(0xFFEF9A9A)

// --- FUNCIONES DE VALIDACIÓN ---
fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()
fun isValidName(name: String): Boolean = name.matches(Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]{3,}$"))
fun isValidPassword(password: String): Boolean = password.matches(Regex("^(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}\$"))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Crear cuenta",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextWhite
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(color = AccentOrange)
                }

                uiState.isSuccess -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(DarkSurface, RoundedCornerShape(16.dp))
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "✅ Registro exitoso",
                            color = AccentOrange,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "¡Bienvenido a la comunidad FitLife!",
                            color = TextGray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                navController.navigate("login") {
                                    popUpTo("register") { inclusive = true }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentOrange,
                                contentColor = DarkText
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ir al login", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier.verticalScroll(scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // --- CAMPO NOMBRE ---
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                nameError = if (it.isNotEmpty() && !isValidName(it))
                                    "Solo letras (min 3 caracteres)"
                                else null
                            },
                            label = { Text("Nombre completo") },
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = AccentOrange) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            isError = nameError != null,
                            shape = RoundedCornerShape(12.dp),
                            colors = getTextFieldColors()
                        )
                        if (nameError != null) ErrorText(nameError!!)

                        // --- CAMPO CORREO ---
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                emailError = if (it.isNotEmpty() && !isValidEmail(it))
                                    "Correo inválido"
                                else null
                            },
                            label = { Text("Correo electrónico") },
                            leadingIcon = { Icon(Icons.Default.Email, null, tint = AccentOrange) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            isError = emailError != null,
                            shape = RoundedCornerShape(12.dp),
                            colors = getTextFieldColors()
                        )
                        if (emailError != null) ErrorText(emailError!!)

                        // --- CAMPO CONTRASEÑA ---
                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                passwordError = if (it.isNotEmpty() && !isValidPassword(it))
                                    "Min 8 chars, 1 mayúscula, 1 número, 1 símbolo"
                                else null
                            },
                            label = { Text("Contraseña") },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = AccentOrange) },
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                val image = if (passwordVisible) {
                                    Icons.Filled.Visibility
                                } else
                                    Icons.Filled.VisibilityOff

                                val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = image, contentDescription = description, tint = AccentOrange)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            isError = passwordError != null,
                            shape = RoundedCornerShape(12.dp),
                            colors = getTextFieldColors()
                        )
                        if (passwordError != null) ErrorText(passwordError!!)

                        Spacer(modifier = Modifier.height(12.dp))

                        // --- BOTÓN REGISTRARSE ---
                        Button(
                            onClick = { viewModel.register(email, password, name) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            enabled = name.isNotBlank() && email.isNotBlank() && password.isNotBlank()
                                    && nameError == null && emailError == null && passwordError == null,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentOrange,
                                contentColor = DarkText,
                                disabledContainerColor = DarkSurface,
                                disabledContentColor = TextGray
                            )
                        ) {
                            Text("Registrarse", fontWeight = FontWeight.Bold)
                        }

                        // --- MENSAJES DE ERROR GENERAL ---
                        if (uiState.errorMessage != null) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.1f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "❌ ${uiState.errorMessage}",
                                    color = ErrorRed,
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(onClick = { navController.navigate("login") }) {
                            Text("¿Ya tienes cuenta? Inicia sesión", color = AccentOrange)
                        }

                        // Espacio extra para scroll
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
        }
    }
}

// Helper para colores de campos de texto (ahorra líneas)
@Composable
fun getTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AccentOrange,
    unfocusedBorderColor = TextGray,
    focusedLabelColor = AccentOrange,
    unfocusedLabelColor = TextGray,
    cursorColor = AccentOrange,
    focusedContainerColor = DarkSurface,
    unfocusedContainerColor = DarkSurface,
    focusedTextColor = TextWhite,
    unfocusedTextColor = TextWhite,
    errorBorderColor = ErrorRed,
    errorLabelColor = ErrorRed,
    errorCursorColor = ErrorRed
)

// Helper para texto de error
@Composable
fun ErrorText(text: String) {
    Text(
        text = text,
        color = ErrorRed,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.fillMaxWidth().padding(start = 8.dp)
    )
}