package com.example.data.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DocumentStorageManager(private val context: Context) {

    private val baseDir: File = File(context.filesDir, "shajeen_documents").apply {
        if (!exists()) mkdirs()
    }

    /**
     * Copies a selected Uri (image or PDF) to private internal app storage.
     * Returns a Triple of (Saved Absolute Path, Display File Name, File Size in bytes).
     */
    fun saveDocumentFromUri(uri: Uri, subfolder: String = "general"): SavedFileInfo {
        val contentResolver = context.contentResolver
        var fileName = "doc_${System.currentTimeMillis()}"
        var fileSize = 0L

        // Query file display name & size
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (nameIndex >= 0) {
                    val name = cursor.getString(nameIndex)
                    if (!name.isNullOrBlank()) fileName = name
                }
                if (sizeIndex >= 0) {
                    fileSize = cursor.getLong(sizeIndex)
                }
            }
        }

        // Determine mime type
        val mimeType = contentResolver.getType(uri) ?: getMimeTypeFromExtension(fileName)

        val targetDir = File(baseDir, subfolder).apply { if (!exists()) mkdirs() }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val sanitizedName = fileName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        val destFile = File(targetDir, "${timeStamp}_$sanitizedName")

        contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        }

        val finalSize = if (destFile.exists()) destFile.length() else fileSize

        return SavedFileInfo(
            filePath = destFile.absolutePath,
            fileName = fileName,
            fileSize = finalSize,
            mimeType = mimeType
        )
    }

    /**
     * Saves a captured Bitmap directly from camera to internal storage.
     */
    fun saveBitmap(bitmap: Bitmap, prefix: String = "photo"): SavedFileInfo {
        val targetDir = File(baseDir, "camera").apply { if (!exists()) mkdirs() }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val fileName = "${prefix}_$timeStamp.jpg"
        val destFile = File(targetDir, fileName)

        FileOutputStream(destFile).use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
        }

        return SavedFileInfo(
            filePath = destFile.absolutePath,
            fileName = fileName,
            fileSize = destFile.length(),
            mimeType = "image/jpeg"
        )
    }

    /**
     * Creates a dummy receipt or document placeholder if needed for demonstration.
     */
    fun createSampleReceipt(bookingNum: String, amount: String = "500$"): SavedFileInfo {
        val targetDir = File(baseDir, "receipts").apply { if (!exists()) mkdirs() }
        val fileName = "receipt_$bookingNum.png"
        val destFile = File(targetDir, fileName)

        if (!destFile.exists()) {
            val bitmap = Bitmap.createBitmap(400, 600, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)
            canvas.drawColor(android.graphics.Color.WHITE)
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.DKGRAY
                textSize = 24f
                isAntiAlias = true
            }
            canvas.drawText("سند تحويل - وكالة شجين", 40f, 60f, paint)
            canvas.drawText("الرقم: $bookingNum", 40f, 120f, paint)
            canvas.drawText("المبلغ: $amount", 40f, 180f, paint)
            FileOutputStream(destFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        }

        return SavedFileInfo(
            filePath = destFile.absolutePath,
            fileName = fileName,
            fileSize = destFile.length(),
            mimeType = "image/png"
        )
    }

    private fun getMimeTypeFromExtension(fileName: String): String {
        val extension = MimeTypeMap.getFileExtensionFromUrl(fileName).lowercase(Locale.US)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "application/octet-stream"
    }

    data class SavedFileInfo(
        val filePath: String,
        val fileName: String,
        val fileSize: Long,
        val mimeType: String
    )
}
