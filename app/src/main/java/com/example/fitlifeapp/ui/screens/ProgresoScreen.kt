package com.example.fitlifeapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Alignment

@Preview(showBackground = true)
@Composable
fun PreviewProgresoScreen() {
    val navController = rememberNavController()
    ProgresoScreen(navController)
}
@Composable

fun ProgresoScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        verticalArrangement = Arrangement.Center,

        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Column(
            verticalArrangement = Arrangement.spacedBy(40.dp),

        ) {
            ProgresoItem("Peso Corporal", "75 kg", 0.75f)
            ProgresoItem("Grasa Corporal", "15%", 0.15f)
            ProgresoItem("Masa Muscular", "40%", 0.40f)
        }


        Spacer(modifier = Modifier.height(200.dp))

        Button(onClick = { navController.navigate("home") }) {
            Text("Volver al Menú Principal")
        }
    }
}

@Composable
fun ProgresoItem(statName: String, statValue: String, progress: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(0.9f),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = statName, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = statValue, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
