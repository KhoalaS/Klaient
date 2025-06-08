package com.khoalas.klaient.services

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import io.ktor.client.request.prepareGet
import io.ktor.http.contentLength
import io.ktor.utils.io.exhausted


class DownloadService(private val context: Context, private val client: HttpClient) {
    suspend fun downloadToDCIM(url: String, fileName: String) {
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)

        withContext(Dispatchers.IO) {
            val resolver = context.contentResolver

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }

            val collection = when {
                mimeType?.startsWith("image/") == true -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                mimeType?.startsWith("video/") == true -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                else -> throw IllegalArgumentException("Unsupported MIME type for DCIM: $mimeType")
            }

            val uri: Uri = resolver.insert(collection, contentValues)
                ?: throw IOException("Failed to create new MediaStore record.")
            try {
                client.prepareGet(url).execute { httpResponse ->
                    val channel: ByteReadChannel = httpResponse.body()
                    var count = 0L
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        val buffer = ByteArray(1024 * 32)

                        outputStream.use {
                            while (!channel.exhausted()) {
                                val bytesRead = channel.readAvailable(buffer)
                                if (bytesRead == -1) {
                                    break
                                }
                                count += bytesRead
                                outputStream.write(buffer, 0, bytesRead)
                                Log.d(
                                    "DOWLOAD_DCIM",
                                    "Received $count bytes from ${httpResponse.contentLength()}"
                                )
                            }
                        }
                    }
                }
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            } catch (e: Exception) {
                resolver.delete(uri, null, null) // Cleanup on failure
                throw e
            }
        }
    }
}
