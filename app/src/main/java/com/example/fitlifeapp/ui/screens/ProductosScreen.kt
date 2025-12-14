package com.example.fitlifeapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.fitlifeapp.R

// --- Colores del Tema "Sunset Dark" ---
private val DarkBackground = Color(0xFF121212)
private val DarkSurface = Color(0xFF252525)
private val AccentOrangeSoft = Color(0xFFFFAB91)
private val AccentAmber = Color(0xFFFFE082)
private val TextWhite = Color(0xFFEEEEEE)
private val TextGray = Color(0xFFAAAAAA)
private val DarkText = Color(0xFF3E2723)

data class Producto(
    val nombre: String,
    val precio: Int,
    val categoria: String,
    val photoResId: Int
)

@Preview(showBackground = true)
@Composable
fun PreviewProductosScreen() {
    ProductosScreen(navController = rememberNavController())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductosScreen(navController: NavHostController) {

    val productos = listOf(
        Producto("Creatina Monohidratada", 10000, "Suplementos", R.drawable.creatina),
        Producto("Proteína Whey", 12000, "Suplementos", R.drawable.whey),
        Producto("Pre-entreno", 15000, "Suplementos", R.drawable.preentreno),
        Producto("BCAA", 9000, "Suplementos", R.drawable.bcaa),
        Producto("Multivitamínico", 7000, "Suplementos", R.drawable.multivitaminico),
        Producto("Polera Deportiva", 15000, "Ropa", R.drawable.polera),
        Producto("Pantalón Deportivo", 20000, "Ropa", R.drawable.pantalon),
        Producto("Calzas Mujer", 18000, "Ropa", R.drawable.calzas),
        Producto("Polerón Gym", 25000, "Ropa", R.drawable.poleron),
        Producto("Guantes", 5000, "Accesorios", R.drawable.guantes),
        Producto("Straps", 7000, "Accesorios", R.drawable.straps),
        Producto("Cinturón de Levantamiento", 18000, "Accesorios", R.drawable.cinturon),
        Producto("Rodilleras", 12000, "Accesorios", R.drawable.rodilleras),
        Producto("Cuerda para Saltar", 6000, "Accesorios", R.drawable.cuerda)
    )

    var buscar by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Todas") }
    var orden by remember { mutableStateOf("Relevancia") }

    val categorias = listOf("Todas", "Suplementos", "Ropa", "Accesorios")
    val opcionesOrden = listOf("Relevancia", "Precio: Menor a Mayor", "Precio: Mayor a Menor")

    val listaFinal = productos
        .filter { it.nombre.contains(buscar, ignoreCase = true) }
        .filter { categoriaSeleccionada == "Todas" || it.categoria == categoriaSeleccionada }
        .let {
            when (orden) {
                "Precio: Menor a Mayor" -> it.sortedBy { p -> p.precio }
                "Precio: Mayor a Menor" -> it.sortedByDescending { p -> p.precio }
                else -> it
            }
        }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Tienda FitLife",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextWhite
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Barra de Búsqueda
            OutlinedTextField(
                value = buscar,
                onValueChange = { buscar = it },
                placeholder = { Text("Buscar...", color = TextGray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AccentOrangeSoft) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentOrangeSoft,
                    unfocusedBorderColor = TextGray,
                    cursorColor = AccentOrangeSoft,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Categorías", style = MaterialTheme.typography.labelMedium, color = TextGray)
            Spacer(modifier = Modifier.height(8.dp))

            // Filtros de Categoría (Chips Personalizados)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categorias) { cat ->
                    val isSelected = categoriaSeleccionada == cat

                    // Chip personalizado para evitar errores de librerías
                    Surface(
                        color = if (isSelected) AccentOrangeSoft else DarkSurface,
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) AccentOrangeSoft else TextGray
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { categoriaSeleccionada = cat }
                    ) {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSelected) DarkText else TextWhite,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ordenar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                // Icono Seguro: List en vez de Sort
                Icon(Icons.Default.List, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                SortDropdown(
                    opciones = opcionesOrden,
                    valor = orden,
                    onChange = { orden = it }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Lista de Productos
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp), // Más espacio entre cards
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(listaFinal) { producto ->
                    ProductoItem(producto)
                }
            }

            // Botón Volver
            OutlinedButton(
                onClick = { navController.navigate("home") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(bottom = 16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TextGray),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Volver al Menú Principal")
            }
        }
    }
}

// Componente para el menú de Ordenar
@Composable
fun SortDropdown(
    opciones: List<String>,
    valor: String,
    onChange: (String) -> Unit
) {
    var expand by remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { expand = true }) {
            Text(valor, color = AccentAmber, fontWeight = FontWeight.SemiBold)
        }
        DropdownMenu(
            expanded = expand,
            onDismissRequest = { expand = false },
            modifier = Modifier.background(DarkSurface)
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion, color = TextWhite) },
                    onClick = {
                        onChange(opcion)
                        expand = false
                    }
                )
            }
        }
    }
}

@Composable
fun ProductoItem(producto: Producto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp), // Esquinas más redondeadas en la tarjeta
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen con fondo redondeado
            Surface(
                shape = RoundedCornerShape(20.dp), // Esquinas más redondeadas en la imagen
                color = Color.White,
                modifier = Modifier.size(80.dp)
            ) {
                Image(
                    painter = painterResource(id = producto.photoResId),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite
                )
                Text(
                    text = producto.categoria,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "$ ${producto.precio}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = AccentAmber
                )
            }

            // Botón de acción (Decorativo)
            IconButton(
                onClick = { /* Acción futura: Agregar al carrito */ },
                modifier = Modifier
                    .background(AccentOrangeSoft, CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar",
                    tint = DarkBackground
                )
            }
        }
    }
}
