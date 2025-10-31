package com.example.fitlifeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.fitlifeapp.ui.navigation.AppNavigation
import kotlinx.coroutines.launch
import kotlinx.coroutines.GlobalScope
import com.example.fitlifeapp.ui.theme.FitLifeAppTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ⚠️ Esto borra todos los datos guardados (solo úsalo una vez)
        val prefs = com.example.fitlifeapp.data.local.UserPreferences(this)
        kotlinx.coroutines.GlobalScope.launch {
            prefs.clearAll()
        }

        setContent {
            FitLifeAppTheme {
                val navController = rememberNavController()
                AppNavigation(navController)
            }
        }

    }

}
