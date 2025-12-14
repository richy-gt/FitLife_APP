package com.example.fitlifeapp.ui.screens

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.fitlifeapp.data.local.UserPreferences
import com.example.fitlifeapp.viewmodel.LoginViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


private val DarkBackground = Color(0xFF121212)
private val DarkSurface = Color(0xFF252525)
private val AccentOrange = Color(0xFFFFAB91)
private val TextWhite = Color(0xFFEEEEEE)
private val TextGray = Color(0xFFAAAAAA)

fun validateEmailLogin(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController, viewModel: LoginViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }


    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            scope.launch {
                userPrefs.saveUserEmail(username)
                userPrefs.saveUserPassword(password)
                userPrefs.saveLoginState(true)
            }
            navController.navigate("home") { popUpTo("login") { inclusive = true } }
        }
    }

    Scaffold(containerColor = DarkBackground) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "🏋️ FitLife",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = AccentOrange,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Inicio de Sesión",
                style = MaterialTheme.typography.headlineMedium,
                color = TextWhite,
                modifier = Modifier.padding(bottom = 32.dp)
            )


            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    emailError = if (it.isNotEmpty() && !validateEmailLogin(it)) "Correo inválido" else null
                },
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Default.Email, null, tint = AccentOrange) },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = emailError != null,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentOrange,
                    unfocusedBorderColor = TextGray,
                    focusedLabelColor = AccentOrange,
                    unfocusedLabelColor = TextGray,
                    cursorColor = AccentOrange,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    errorLabelColor = Color.Red,
                    errorBorderColor = Color.Red
                )
            )
            if (emailError != null) {
                Text(emailError!!, color = Color.Red, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(16.dp))


            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = AccentOrange) },
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
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentOrange,
                    unfocusedBorderColor = TextGray,
                    focusedLabelColor = AccentOrange,
                    unfocusedLabelColor = TextGray,
                    cursorColor = AccentOrange,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                )
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { navController.navigate("recover_password") }) {
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = TextGray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


            Button(
                onClick = { if (emailError == null && username.isNotBlank() && password.isNotBlank()) viewModel.login(username, password) },
                enabled = !uiState.isLoading && username.isNotBlank() && password.isNotBlank() && emailError == null,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentOrange,
                    contentColor = Color.Black,
                    disabledContainerColor = DarkSurface,
                    disabledContentColor = TextGray
                )
            ) {
                if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                else Text("Ingresar", fontWeight = FontWeight.Bold)
            }


            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFEF9A9A))) {
                    Text("❌ ${uiState.errorMessage}", color = Color.Red, modifier = Modifier.padding(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            TextButton(
                onClick = { navController.navigate("register") },
                enabled = !uiState.isLoading
            ) {
                Text("¿No tienes cuenta? Regístrate", color = AccentOrange)
            }
        }
    }
}
