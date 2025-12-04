package com.example.fitlifeapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fitlifeapp.CameraAvatarScreen
import com.example.fitlifeapp.ui.profile.ProfileScreen
import com.example.fitlifeapp.ui.screens.*
import com.example.fitlifeapp.data.local.UserPreferences
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.example.fitlifeapp.viewmodel.PlanEntrenamientoViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    // 🔹 Creamos el ViewModel de PlanEntrenamiento compartido
    val planViewModel: PlanEntrenamientoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        composable("splash") {
            SplashDecider(navController)
        }

        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }

        composable("home") { HomeScreen(navController) }
        composable("entrenador") { EntrenadorScreen(navController) }

        // 🔥 Pantalla lista de planes con ViewModel compartido
        composable("plan_entrenamiento") {
            PlanEntrenamientoScreen(
                navController = navController,
                viewModel = planViewModel
            )
        }

        composable("plan_nutricional") { PlanNutricionalScreen(navController) }

        // 🔥 Pantalla detalle del plan con ViewModel compartido y navController
        composable("detallePlan") {
            DetallePlanScreen(
                navController = navController,
                viewModel = planViewModel
            )
        }

        composable("progreso") { ProgresoScreen(navController) }
        composable("productos") { ProductosScreen(navController) }
        composable("personalizacion") { ProfileScreen(navController) }
        composable("camera_avatar") { CameraAvatarScreen(navController) }
    }
}

@Composable
private fun SplashDecider(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val prefs = UserPreferences(context)
                val loggedIn = prefs.isLoggedIn().first()
                val userEmail = prefs.getUserEmail()

                if (loggedIn && !userEmail.isNullOrEmpty()) {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                } else {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            } catch (e: Exception) {
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
    }
}
