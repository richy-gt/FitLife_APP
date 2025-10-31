package com.example.fitlifeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.fitlifeapp.ui.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Controlador de navegación
            val navController = rememberNavController()

            // Carga la navegación principal de la app
            AppNavigation(navController)
        }
    }
}
