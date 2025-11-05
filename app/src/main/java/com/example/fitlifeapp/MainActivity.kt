package com.example.fitlifeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.fitlifeapp.ui.navigation.AppNavigation
import com.example.fitlifeapp.ui.theme.FitLifeAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent {
            FitLifeAppTheme {
                val navController = rememberNavController()
                AppNavigation(navController)
            }
        }
    }
}