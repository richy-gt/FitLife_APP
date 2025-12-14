package com.example.fitlifeapp

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import java.io.File

// --- Colores del Tema "Sunset Dark" ---
private val DarkBackground = Color(0xFF121212)
private val AccentOrange = Color(0xFFFFAB91)
private val TextWhite = Color(0xFFEEEEEE)
private val TextGray = Color(0xFFAAAAAA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraAvatarScreen(navController: NavController) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(AvatarPreferences.obtenerAvatarUri(context)) }
    var accionPendiente by remember { mutableStateOf<(() -> Unit)?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val permisosCamara = arrayOf(Manifest.permission.CAMERA)
    val permisosGaleria13 = arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    val permisosGaleria12 = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    // === Lanzadores (Lógica intacta) ===
    val tomarFotoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { ok ->
        if (ok) {
            imageUri?.let {
                val persistente = AvatarStorage.persistFromUri(context, it)
                AvatarPreferences.guardarAvatarUri(context, persistente)
                scope.launch { snackbarHostState.showSnackbar("📸 Foto actualizada") }
                navController.popBackStack()
            }
        }
    }

    val elegirImagenLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            imageUri = uri
            val persistente = AvatarStorage.persistFromUri(context, uri)
            AvatarPreferences.guardarAvatarUri(context, persistente)
            scope.launch { snackbarHostState.showSnackbar("🖼️ Imagen seleccionada") }
            navController.popBackStack()
        }
    }

    val solicitarPermisos = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.all { it.value }) accionPendiente?.invoke()
    }

    // === Funciones Auxiliares ===
    fun abrirCamara() {
        val imagesDir = File(context.externalCacheDir, "images").apply { mkdirs() }
        val foto = File.createTempFile("avatar_", ".jpg", imagesDir)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", foto)
        imageUri = uri
        tomarFotoLauncher.launch(uri)
    }

    fun abrirGaleria() {
        val request = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        elegirImagenLauncher.launch(request)
    }

    fun pedirPermisoCamaraYTomar() {
        if (permisosCamara.any { ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED }) {
            accionPendiente = { abrirCamara() }
            solicitarPermisos.launch(permisosCamara)
        } else abrirCamara()
    }

    fun pedirPermisoGaleriaYElegir() {
        val permisos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) permisosGaleria13 else permisosGaleria12
        if (permisos.any { ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED }) {
            accionPendiente = { abrirGaleria() }
            solicitarPermisos.launch(permisos)
        } else abrirGaleria()
    }

    // === UI ===
    Scaffold(
        containerColor = DarkBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Actualizar Foto", style = MaterialTheme.typography.headlineSmall, color = TextWhite)
            Spacer(modifier = Modifier.height(24.dp))

            // Avatar con borde naranja
            Box(
                modifier = Modifier
                    .size(170.dp)
                    .border(4.dp, AccentOrange, CircleShape)
                    .padding(5.dp) // Espacio entre borde e imagen
                    .clip(CircleShape)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri ?: R.mipmap.ic_launcher_round),
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Botón Cámara
            Button(
                onClick = { pedirPermisoCamaraYTomar() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange, contentColor = Color.Black)
            ) {
                Text("📷 Tomar foto")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Galería
            Button(
                onClick = { pedirPermisoGaleriaYElegir() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange, contentColor = Color.Black)
            ) {
                Text("🖼️ Elegir de galería")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón Volver
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, TextGray)
            ) {
                Text("⬅️ Cancelar")
            }
        }
    }
}