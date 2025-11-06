package com.example.fitlifeapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.fitlifeapp.data.remote.RetrofitClient
import com.example.fitlifeapp.ui.navigation.AppNavigation
import com.example.fitlifeapp.ui.theme.FitLifeAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Request

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔍 Verificamos que la URL base esté correcta
        val retrofit = RetrofitClient.createPublic()
        Log.d("FitLifeAPI", "🌐 Base URL actual: ${retrofit.baseUrl()}")

        // 🔄 Probamos conexión rápida al backend (GET /)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = retrofit.callFactory() as okhttp3.Call.Factory
                val request = Request.Builder()
                    .url("http://10.0.2.2:8080/") // ruta raíz del backend
                    .build()

                val response = client.newCall(request).execute()
                Log.d("FitLifeAPI", "✅ Respuesta backend: ${response.code} ${response.message}")
            } catch (e: Exception) {
                Log.e("FitLifeAPI", "❌ Error al conectar con backend: ${e.message}")
            }
        }

        // 🎨 Interfaz principal
        setContent {
            FitLifeAppTheme {
                val navController = rememberNavController()
                AppNavigation(navController)
            }
        }
    }
}
