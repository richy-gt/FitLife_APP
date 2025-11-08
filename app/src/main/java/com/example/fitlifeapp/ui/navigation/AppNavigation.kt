package com.example.fitlifeapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fitlifeapp.CameraAvatarScreen
import com.example.fitlifeapp.ui.profile.ProfileScreen
import com.example.fitlifeapp.ui.screens.LoginScreen
import com.example.fitlifeapp.ui.screens.RegisterScreen
import com.example.fitlifeapp.ui.screens.HomeScreen
import com.example.fitlifeapp.data.local.UserPreferences
import androidx.compose.ui.platform.LocalContext
import com.example.fitlifeapp.ui.screens.EntrenadorScreen
import com.example.fitlifeapp.ui.screens.PlanEntrenamientoScreen
import com.example.fitlifeapp.ui.screens.PlanNutricionalScreen
import com.example.fitlifeapp.ui.screens.ProgresoScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(navController: NavHostController) {
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
        composable("plan_entrenamiento") { PlanEntrenamientoScreen(navController) }
        composable("plan_nutricional") { PlanNutricionalScreen(navController) }
        composable("progreso") { ProgresoScreen(navController) }
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
