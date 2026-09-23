package com.example.data.storage

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import com.example.data.model.AudioClip
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AudioExportHelper {

    fun getAudioDuration(file: File): Long {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(file.absolutePath)
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            retriever.release()
            durationStr?.toLongOrNull() ?: estimateWavDuration(file)
        } catch (e: Exception) {
            estimateWavDuration(file)
        }
    }

    private fun estimateWavDuration(file: File): Long {
        // Standard Android TTS WAV is typically 22050Hz 16-bit mono (~44100 bytes/sec)
        val bytes = file.length()
        if (bytes <= 44) return 0L
        val dataBytes = bytes - 44
        return (dataBytes * 1000L / 44100L).coerceAtLeast(500L)
    }

    fun exportToPublicDirectory(context: Context, clip: AudioClip): Result<String> {
        return try {
            val sourceFile = File(clip.filePath)
            if (!sourceFile.exists()) {
                return Result.failure(IllegalStateException("Audio file not found"))
            }

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val sanitizedTitle = clip.title.take(30).replace(Regex("[^a-zA-Z0-9_\\-\\s]"), "").trim()
            val exportFileName = "Supertonic_${sanitizedTitle}_$timestamp.wav"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.Audio.Media.DISPLAY_NAME, exportFileName)
                    put(MediaStore.Audio.Media.MIME_TYPE, "audio/wav")
                    put(MediaStore.Audio.Media.RELATIVE_PATH, "${Environment.DIRECTORY_MUSIC}/SupertonicTTS")
                    put(MediaStore.Audio.Media.IS_PENDING, 1)
                }

                val uri = resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)
                    ?: return Result.failure(IllegalStateException("Failed to create MediaStore entry"))

                resolver.openOutputStream(uri)?.use { out ->
                    FileInputStream(sourceFile).use { input ->
                        input.copyTo(out)
                    }
                }

                contentValues.clear()
                contentValues.put(MediaStore.Audio.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)

                Result.success("Saved to Music/SupertonicTTS/$exportFileName")
            } else {
                @Suppress("DEPRECATION")
                val exportDir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
                    "SupertonicTTS"
                )
                if (!exportDir.exists()) exportDir.mkdirs()
                val targetFile = File(exportDir, exportFileName)
                sourceFile.copyTo(targetFile, overwrite = true)
                Result.success("Saved to ${targetFile.absolutePath}")
            }
        } catch (e: Exception) {
            Log.e("AudioExportHelper", "Failed to export audio: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun createShareIntent(context: Context, clip: AudioClip): Intent? {
        val file = File(clip.filePath)
        if (!file.exists()) return null

        val authority = "${context.packageName}.fileprovider"
        val uri: Uri = FileProvider.getUriForFile(context, authority, file)

        return Intent(Intent.ACTION_SEND).apply {
            type = "audio/wav"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, clip.title)
            putExtra(Intent.EXTRA_TEXT, "Generated with Supertonic 3 On-Device TTS:\n\"${clip.text.take(120)}...\"")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    fun formatDuration(durationMs: Int): String {
        val totalSecs = (durationMs / 1000).coerceAtLeast(0)
        val minutes = totalSecs / 60
        val seconds = totalSecs % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> String.format(Locale.getDefault(), "%.1f KB", bytes / 1024.0)
            else -> String.format(Locale.getDefault(), "%.1f MB", bytes / (1024.0 * 1024.0))
        }
    }
}
