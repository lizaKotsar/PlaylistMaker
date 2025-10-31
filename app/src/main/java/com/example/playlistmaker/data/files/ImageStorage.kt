package com.example.playlistmaker.data.files

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream


class ImageStorage(private val appContext: Context) {

    fun copyToAppStorage(srcUri: Uri): String? {
        return try {
            val dir = File(appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlists")
            if (!dir.exists()) dir.mkdirs()

            val dst = File(dir, "pl_${System.currentTimeMillis()}.jpg")

            appContext.contentResolver.openInputStream(srcUri).use { input ->
                FileOutputStream(dst).use { out ->
                    val bmp: Bitmap = BitmapFactory.decodeStream(input) ?: return null
                    bmp.compress(Bitmap.CompressFormat.JPEG, 30, out)
                }
            }
            dst.absolutePath
        } catch (_: Exception) {
            null
        }
    }
}