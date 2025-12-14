package com.example.fitlifeapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fitlifeapp.R

// --- Datos y Colores ---
data class EntrenadorPro(val name: String, val spec: String, val img: Int, val price: String, val rate: Double, val bio: String)

private val DarkBg = Color(0xFF121212)
private val DarkSurf = Color(0xFF252525)
private val Orange = Color(0xFFFFAB91)
private val TextW = Color(0xFFEEEEEE)
private val TextG = Color(0xFFAAAAAA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntrenadorScreen(navController: NavHostController) {
    val context = LocalContext.current
    val entrenadores = remember { listOf(
        EntrenadorPro("Ana Torres", "Cardio", R.drawable.entrenadora, "$20.000/h", 4.8, "Experta en HIIT."),
        EntrenadorPro("Carlos Ruiz", "Fuerza", R.drawable.entrenador, "$25.000/h", 4.9, "Powerlifting pro."),
        EntrenadorPro("Sofía Vargas", "Yoga", R.drawable.entrenadora2, "$18.000/h", 5.0, "Vinyasa y Hatha."),
        EntrenadorPro("P. Puilodran", "Funcional", R.drawable.entrenador2, "$22.000/h", 4.7, "Movilidad total."),
        EntrenadorPro("Ricardo Saez", "Musculación", R.drawable.entrenador3, "$30.000/h", 4.9, "Hipertrofia.")
    )}

    var search by remember { mutableStateOf("") }
    var cat by remember { mutableStateOf("Todos") }
    var selected by remember { mutableStateOf<EntrenadorPro?>(null) }
    val sheetState = rememberModalBottomSheetState()

    val filtered = entrenadores.filter {
        (it.name.contains(search, true) || it.spec.contains(search, true)) &&
                (cat == "Todos" || it.spec.contains(cat, true))
    }

    if (selected != null) {
        ModalBottomSheet(onDismissRequest = { selected = null }, sheetState = sheetState, containerColor = DarkSurf) {
            DetailSheet(selected!!) {
                Toast.makeText(context, "Solicitud enviada a ${selected!!.name}", Toast.LENGTH_SHORT).show()
                selected = null
            }
        }
    }

    Scaffold(containerColor = DarkBg, topBar = {
        CenterAlignedTopAppBar(
            title = { Text("Entrenadores", fontWeight = FontWeight.Bold, color = TextW) },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(DarkBg)
        )
    }) { p ->
        Column(Modifier.padding(p).padding(16.dp)) {
            // Buscador
            OutlinedTextField(
                value = search, onValueChange = { search = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar...", color = TextG) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Orange) },
                singleLine = true, shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Orange, unfocusedBorderColor = TextG, focusedContainerColor = DarkSurf, unfocusedContainerColor = DarkSurf, focusedTextColor = TextW, unfocusedTextColor = TextW)
            )

            // Chips Filtro
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 12.dp)) {
                items(listOf("Todos", "Fuerza", "Cardio", "Yoga", "Funcional", "Musculación")) { c ->
                    FilterChip(
                        selected = cat == c, onClick = { cat = c },
                        label = { Text(c) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Orange, containerColor = DarkSurf, labelColor = TextG, selectedLabelColor = Color.Black),
                        border = FilterChipDefaults.filterChipBorder(borderColor = TextG, selectedBorderColor = Orange, enabled = true, selected = cat == c)
                    )
                }
            }

            // Lista
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                items(filtered) { coach -> CoachItem(coach) { selected = coach } }
            }

            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = { navController.navigate("home") }, Modifier.fillMaxWidth().height(50.dp), border = BorderStroke(1.dp, TextG)) {
                Icon(Icons.Default.ArrowBack, null, tint = TextW); Spacer(Modifier.width(8.dp)); Text("Volver", color = TextW)
            }
        }
    }
}

@Composable
fun CoachItem(c: EntrenadorPro, onClick: () -> Unit) {
    Card(onClick = onClick, colors = CardDefaults.cardColors(DarkSurf), elevation = CardDefaults.cardElevation(4.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(c.img), null, Modifier.size(60.dp).clip(CircleShape).border(2.dp, Orange, CircleShape), contentScale = ContentScale.Crop)
            Column(Modifier.padding(start = 16.dp).weight(1f)) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text(c.name, fontWeight = FontWeight.Bold, color = TextW)
                    Row { Icon(Icons.Default.Star, null, tint = Color(0xFFFFE082), modifier = Modifier.size(16.dp)); Text("${c.rate}", color = TextW, style = MaterialTheme.typography.bodySmall) }
                }
                Text(c.spec, color = TextG, style = MaterialTheme.typography.bodySmall)
                Text(c.price, color = Orange, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.Default.ArrowForward, null, tint = TextG)
        }
    }
}

@Composable
fun DetailSheet(c: EntrenadorPro, onContact: () -> Unit) {
    Column(Modifier.padding(24.dp).padding(bottom = 30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painterResource(c.img), null, Modifier.size(120.dp).clip(CircleShape).border(4.dp, Orange, CircleShape), contentScale = ContentScale.Crop)
        Text(c.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TextW, modifier = Modifier.padding(top = 16.dp))
        Text(c.spec, color = Orange, style = MaterialTheme.typography.titleMedium)

        Row(Modifier.fillMaxWidth().padding(vertical = 24.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            Stat("Rating", "${c.rate}", Icons.Default.Star)
            Stat("Valor", c.price, Icons.Default.ShoppingCart)
            Stat("Exp", "5+ Años", Icons.Default.DateRange)
        }

        HorizontalDivider(color = TextG.copy(0.3f))
        Text("Sobre mí", color = TextW, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start).padding(top = 16.dp))
        Text(c.bio, color = TextG, modifier = Modifier.align(Alignment.Start).padding(top = 4.dp, bottom = 32.dp))

        Button(onClick = onContact, Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(Orange, contentColor = Color.Black), shape = RoundedCornerShape(12.dp)) {
            Icon(Icons.Default.Email, null); Spacer(Modifier.width(8.dp)); Text("Solicitar Asesoría")
        }
    }
}

@Composable
fun Stat(lbl: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = TextG, modifier = Modifier.size(24.dp))
        Text(value, color = TextW, fontWeight = FontWeight.Bold); Text(lbl, color = TextG, style = MaterialTheme.typography.labelSmall)
    }
}