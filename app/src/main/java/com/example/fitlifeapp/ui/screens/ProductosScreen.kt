package com.example.fitlifeapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController


data class Producto(
    val nombre: String,
    val precio: Int,
    val categoria: String
)


@Preview(showBackground = true)
@Composable
fun PreviewProductosScreen() {
    ProductosScreen(navController = rememberNavController())
}

@Composable
fun ProductosScreen(navController: NavHostController) {

    val productos = listOf(
        Producto("Creatina Monohidratada", 10000, "Suplementos"),
        Producto("Proteína Whey", 12000, "Suplementos"),
        Producto("Pre-entreno", 15000, "Suplementos"),
        Producto("BCAA", 9000, "Suplementos"),
        Producto("Multivitamínico", 7000, "Suplementos"),
        Producto("Polera Deportiva", 15000, "Ropa"),
        Producto("Pantalón Deportivo", 20000, "Ropa"),
        Producto("Calzas Mujer", 18000, "Ropa"),
        Producto("Polerón Gym", 25000, "Ropa"),
        Producto("Guantes", 5000, "Accesorios"),
        Producto("Straps", 7000, "Accesorios"),
        Producto("Cinturón de Levantamiento", 18000, "Accesorios"),
        Producto("Rodilleras", 12000, "Accesorios"),
        Producto("Cuerda para Saltar", 6000, "Accesorios")
    )

    var buscar by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("---") }
    var orden by remember { mutableStateOf("---") }

    val listaFinal = productos
        .filter { it.nombre.contains(buscar, ignoreCase = true) }
        .filter { categoria == "---" || it.categoria == categoria }
        .let {
            when (orden) {
                "Precio ↑" -> it.sortedBy { p -> p.precio }
                "Precio ↓" -> it.sortedByDescending { p -> p.precio }
                else -> it
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Productos",
                style = MaterialTheme.typography.titleLarge
            )
            TextButton(
                onClick = { navController.navigate("home") }
            ) {
                Text("Volver")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = buscar,
            onValueChange = { buscar = it },
            label = { Text("Buscar producto") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CompactDropdown(
                label = "Categoría",
                opciones = listOf("---", "Suplementos", "Ropa", "Accesorios"),
                valor = categoria,
                onChange = { categoria = it },
                modifier = Modifier.weight(1f)
            )

            CompactDropdown(
                label = "Ordenar",
                opciones = listOf("---", "Precio ↑", "Precio ↓"),
                valor = orden,
                onChange = { orden = it },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))


        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listaFinal) { producto ->
                ProductoItem(producto)
            }
        }


        Button(
            onClick = { navController.navigate("home") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al menú principal")
        }
    }
}


@Composable
fun CompactDropdown(
    label: String,
    opciones: List<String>,
    valor: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expand by remember { mutableStateOf(false) }

    Column(modifier) {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.height(4.dp))
        Box {
            OutlinedButton(
                onClick = { expand = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(valor)
            }
            DropdownMenu(
                expanded = expand,
                onDismissRequest = { expand = false }
            ) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            onChange(opcion)
                            expand = false
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun ProductoItem(producto: Producto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${producto.precio} CLP",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = producto.categoria,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
