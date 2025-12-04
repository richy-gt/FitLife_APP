package com.example.fitlifeapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fitlifeapp.R
import com.example.fitlifeapp.data.model.Entrenador
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

@Preview(showBackground = true)
@Composable
fun PreviewEntrenadorScreen() {
    val navController = rememberNavController()
    EntrenadorScreen(navController)
}

@Composable
fun EntrenadorScreen(navController: NavHostController) {

    val entrenadores = listOf(
        Entrenador("Ana Torres", "Especialista en cardio y pérdida de peso", R.drawable.entrenadora),
        Entrenador("Carlos Ruiz", "Experto en musculación y fuerza", R.drawable.entrenador),
        Entrenador("Sofía Vargas", "Instructora de yoga y flexibilidad", R.drawable.entrenadora2),
        Entrenador("Qatricio Puilodran", "Especialista en entrenamiento funcional", R.drawable.entrenador2),
        Entrenador("Sicardo Raez", "Especialista en musculación", R.drawable.entrenador3),
    )

    var searchText by remember { mutableStateOf("") }

    val filteredEntrenadores = if (searchText.isEmpty()) {
        entrenadores
    } else {
        entrenadores.filter { entrenador ->
            entrenador.name.contains(searchText, ignoreCase = true) ||
                    entrenador.specialization.contains(searchText, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SearchFilter(
            searchText = searchText,
            onSearchTextChanged = { searchText = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredEntrenadores) { entrenador ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    EntrenadorCard(entrenador)
                }
            }
        }

        Button(
            onClick = { navController.navigate("home") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Volver al Menú Principal")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFilter(
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchTextChanged,
        modifier = modifier,
        placeholder = { Text("Buscar entrenador...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Buscar")
        },
        singleLine = true
    )
}

@Composable
fun EntrenadorCard(entrenador: Entrenador) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = entrenador.photoResId),
                contentDescription = "Foto de ${entrenador.name}",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(text = entrenador.name, style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = entrenador.specialization, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
