package com.example.eventconnect.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Converts a content Uri (from gallery picker) into a File
 * so it can be uploaded as MultipartBody.Part
 */
fun uriToFile(context: Context, uri: Uri): File {
    val contentResolver = context.contentResolver
    val inputStream: InputStream =
        contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Unable to open URI")

    // Create a temporary file in cache directory
    val file = File.createTempFile(
        "ngo_image_",
        ".jpg",
        context.cacheDir
    )

    FileOutputStream(file).use { outputStream ->
        inputStream.copyTo(outputStream)
    }

    inputStream.close()
    return file
}
