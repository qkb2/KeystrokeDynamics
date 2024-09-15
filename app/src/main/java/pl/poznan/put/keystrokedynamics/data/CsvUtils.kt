package pl.poznan.put.keystrokedynamics.data

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

fun keyPressesToCsv(keyPresses: List<KeyPressEntity>): String {
    val csvBuilder = StringBuilder()
    csvBuilder.append("Key,PressTime,Duration\n")  // CSV header

    for (keyPress in keyPresses) {
        csvBuilder.append("${keyPress.key},${keyPress.pressTime},${keyPress.duration}\n")
    }

    return csvBuilder.toString()
}

fun saveCsvToDownloads(context: Context, csvData: String): Uri? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Android 10 and higher: Use MediaStore API
        saveCsvToDownloadsForQAndAbove(context, csvData)
    } else {
        // Android 9 and lower: Directly write to external storage
        saveCsvToDownloadsForPreQ(context, csvData)
    }
}

// For Android 10+ (Q and above)
@RequiresApi(Build.VERSION_CODES.Q)
private fun saveCsvToDownloadsForQAndAbove(context: Context, csvData: String): Uri? {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "key_presses.csv")
        put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
    }

    val resolver = context.contentResolver
    var uri: Uri? = null
    var outputStream: OutputStream? = null

    try {
        uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            outputStream = resolver.openOutputStream(uri)
            outputStream?.use { stream ->
                stream.write(csvData.toByteArray())
                Toast.makeText(context, "File saved to Downloads", Toast.LENGTH_LONG).show()
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to save file", Toast.LENGTH_SHORT).show()
        uri?.let { resolver.delete(it, null, null) }  // Clean up if failed
    } finally {
        outputStream?.close()
    }

    return uri
}

// For Android 9 and below (Pre-Q)
private fun saveCsvToDownloadsForPreQ(context: Context, csvData: String): Uri? {
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File(downloadsDir, "key_presses.csv")

    return try {
        val fileOutputStream = FileOutputStream(file)
        fileOutputStream.use { stream ->
            stream.write(csvData.toByteArray())
            Toast.makeText(context, "File saved to: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        }
        Uri.fromFile(file)  // Return Uri of the saved file
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to save file", Toast.LENGTH_SHORT).show()
        null
    }
}