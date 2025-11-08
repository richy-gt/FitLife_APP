package com.example.fitlifeapp

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import java.io.File

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

    // === Lanzadores ===
    val tomarFotoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { ok ->
        if (ok) {
            imageUri?.let {

                val persistente = AvatarStorage.persistFromUri(context, it)
                AvatarPreferences.guardarAvatarUri(context, persistente)

                scope.launch {
                    snackbarHostState.showSnackbar("📸 Foto actualizada correctamente")
                }

                navController.popBackStack()
            }
        }
    }

    val elegirImagenLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            imageUri = uri
            // Guarda también de forma persistente
            val persistente = AvatarStorage.persistFromUri(context, uri)
            AvatarPreferences.guardarAvatarUri(context, persistente)

            scope.launch {
                snackbarHostState.showSnackbar("🖼️ Imagen seleccionada correctamente")
            }

            navController.popBackStack()
        }
    }

    val solicitarPermisos = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val todosOk = results.all { it.value }
        if (todosOk) accionPendiente?.invoke()
    }

    // === Funciones ===
    fun abrirCamara() {
        val imagesDir = File(context.externalCacheDir, "images").apply { mkdirs() }
        val foto = File.createTempFile("avatar_", ".jpg", imagesDir)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            foto
        )
        imageUri = uri
        tomarFotoLauncher.launch(uri)
    }

    fun abrirGaleria() {
        val request = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        elegirImagenLauncher.launch(request)
    }

    fun pedirPermisoCamaraYTomar() {
        val faltan = permisosCamara.any {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        if (faltan) {
            accionPendiente = { abrirCamara() }
            solicitarPermisos.launch(permisosCamara)
        } else abrirCamara()
    }

    fun pedirPermisoGaleriaYElegir() {
        val permisos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            permisosGaleria13 else permisosGaleria12
        val faltan = permisos.any {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        if (faltan) {
            accionPendiente = { abrirGaleria() }
            solicitarPermisos.launch(permisos)
        } else abrirGaleria()
    }

    // === UI ===
    Scaffold(
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
            Image(
                painter = rememberAsyncImagePainter(imageUri ?: R.mipmap.ic_launcher_round),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = { pedirPermisoCamaraYTomar() }) {
                Text("📷 Tomar foto")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(onClick = { pedirPermisoGaleriaYElegir() }) {
                Text("🖼️ Elegir de galería")
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(onClick = { navController.popBackStack() }) {
                Text("⬅️ Volver al perfil")
            }
        }
    }
}
