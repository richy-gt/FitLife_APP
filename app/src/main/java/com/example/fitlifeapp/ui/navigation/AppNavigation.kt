package com.example.fitlifeapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fitlifeapp.CameraAvatarScreen
import com.example.fitlifeapp.ui.profile.ProfileScreen  // ✅ ubicación correcta

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "profile"  // Pantalla inicial
    ) {
        // Pantalla de perfil
        composable("profile") {
            ProfileScreen(navController)
        }

        // Pantalla para cámara / selección de imagen
        composable("camera_avatar") {
            CameraAvatarScreen(navController)
        }
    }
}
