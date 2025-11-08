package com.example.fitlifeapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object AvatarStorage {
    private const val AVATAR_DIR = "avatar"
    private const val AVATAR_FILE = "avatar.jpg"


    private fun avatarFile(context: Context): File =
        File(File(context.filesDir, AVATAR_DIR).apply { mkdirs() }, AVATAR_FILE)


    fun persistFromUri(context: Context, source: Uri): Uri {
        val dest = avatarFile(context)

        context.contentResolver.openInputStream(source).use { input ->
            requireNotNull(input) { "No se pudo abrir la imagen de origen" }


            if (dest.exists()) dest.delete()


            FileOutputStream(dest).use { output ->
                input.copyTo(output)
            }
        }


        try {
            context.contentResolver.takePersistableUriPermission(
                source,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (_: Exception) {

        }

        return Uri.fromFile(dest)
    }


    fun getPersistent(context: Context): Uri? {
        val f = avatarFile(context)
        return if (f.exists()) Uri.fromFile(f) else null
    }


    fun clear(context: Context) {
        avatarFile(context).delete()
    }
}
