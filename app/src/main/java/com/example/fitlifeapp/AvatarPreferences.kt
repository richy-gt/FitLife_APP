package com.example.fitlifeapp

import android.content.Context
import android.net.Uri

object AvatarPreferences {
    private const val PREFS_NAME = "avatar_prefs"
    private const val KEY_AVATAR_URI = "avatar_uri"

    fun guardarAvatarUri(context: Context, uri: Uri?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_AVATAR_URI, uri?.toString()).apply()
    }

    fun obtenerAvatarUri(context: Context): Uri? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val uriString = prefs.getString(KEY_AVATAR_URI, null)
        return uriString?.let { Uri.parse(it) }
    }
}
