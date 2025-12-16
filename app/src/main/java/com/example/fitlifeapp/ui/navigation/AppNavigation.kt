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
import com.example.fitlifeapp.ui.screens.entrenador.*
// import com.example.fitlifeapp.ui.screens.nutricionista.* // Desactivado
import com.example.fitlifeapp.ui.screens.admin.*
import com.example.fitlifeapp.data.local.UserPreferences
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.example.fitlifeapp.viewmodel.PlanEntrenamientoViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    val planViewModel: PlanEntrenamientoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // AUTH
        composable("splash") { SplashDecider(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("recover_password") { RecoverPasswordScreen(navController) }

        //  HOME Y PERFIL
        composable("home") { HomeScreen(navController) }
        composable("personalizacion") { ProfileScreen(navController) }
        composable("camera_avatar") { CameraAvatarScreen(navController) }
        composable("role_selector") { RoleSelectorScreen(navController) } // Agregada ruta faltante

        //  MIEMBRO
        composable("plan_entrenamiento") {
            PlanEntrenamientoScreen(
                navController = navController,
                viewModel = planViewModel
            )
        }
        composable("plan_nutricional") { PlanNutricionalScreen(navController) }
        composable("detallePlan") {
            DetallePlanScreen(
                navController = navController,
                viewModel = planViewModel
            )
        }
        composable("progreso") { ProgresoScreen(navController) }
        composable("productos") { ProductosScreen(navController) }
        composable("entrenador") { EntrenadorScreen(navController) }

        //  ENTRENADOR
        composable("entrenador_clientes") { EntrenadorClientesScreen(navController) }
        composable("entrenador_rutinas") { EntrenadorRutinasScreen(navController) }
        composable("entrenador_historial") { EntrenadorHistorialScreen(navController) }

        //  NUTRICIONISTA (DESACTIVADO)
        // composable("nutricionista_clientes") { NutricionistaClientesScreen(navController) }


        //  ADMIN
        composable("admin_usuarios") { AdminUsuariosScreen(navController) }
        composable("admin_reportes") { AdminReportesScreen(navController) }
        composable("admin_config") { AdminConfigScreen(navController) }
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

//no cambien los colores en las otras paginas, nose porque crashea