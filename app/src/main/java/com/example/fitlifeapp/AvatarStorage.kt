package com.example.fitlifeapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object AvatarStorage {
    private const val AVATAR_DIR = "avatar"
    private const val AVATAR_FILE = "avatar.jpg"

    /** Archivo destino persistente dentro del almacenamiento interno de la app */
    private fun avatarFile(context: Context): File =
        File(File(context.filesDir, AVATAR_DIR).apply { mkdirs() }, AVATAR_FILE)

    /** Copia la imagen (uri de cámara/galería) a almacenamiento interno y devuelve su Uri (file://) */
    fun persistFromUri(context: Context, source: Uri): Uri {
        val dest = avatarFile(context)

        context.contentResolver.openInputStream(source).use { input ->
            requireNotNull(input) { "No se pudo abrir la imagen de origen" }

            // Elimina si ya existía una imagen anterior
            if (dest.exists()) dest.delete()

            // Copia los bytes de forma segura
            FileOutputStream(dest).use { output ->
                input.copyTo(output)
            }
        }

        // Intenta mantener permiso persistente (solo aplica si el URI viene de galería)
        try {
            context.contentResolver.takePersistableUriPermission(
                source,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (_: Exception) {
            // Ignoramos si no aplica (por ejemplo, si proviene de la cámara)
        }

        return Uri.fromFile(dest)
    }

    /** Devuelve el Uri persistente si existe */
    fun getPersistent(context: Context): Uri? {
        val f = avatarFile(context)
        return if (f.exists()) Uri.fromFile(f) else null
    }

    /** Borra el avatar persistente */
    fun clear(context: Context) {
        avatarFile(context).delete()
    }
}
