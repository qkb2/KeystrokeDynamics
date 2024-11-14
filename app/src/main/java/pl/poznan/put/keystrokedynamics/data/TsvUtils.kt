package pl.poznan.put.keystrokedynamics.data

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import pl.poznan.put.keystrokedynamics.R

import java.security.cert.CertificateFactory
import java.security.KeyStore
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

fun keyPressesToTsv(keyPresses: List<KeyPressEntity>): String {
    val tsvBuilder = StringBuilder()
    tsvBuilder.append("Key\tPressTime\tDuration\tAccelX\tAccelY\tAccelZ\n")  // TSV header

    for (keyPress in keyPresses) {
        tsvBuilder.append(
            "${keyPress.key}\t${keyPress.pressTime}\t${keyPress.duration}\t${keyPress.accelX}\t${keyPress.accelY}\t${keyPress.accelZ}\n")
    }

    return tsvBuilder.toString()
}

fun sendTsvToFastApi(tsvData: String, username: String, context: Context) {
//    Log.i("api", "username: $username")
    val url = "https://192.168.1.100:8000/upload-tsv?username=$username"  // Pass username as query parameter
    val client = getSslConfiguredClient(context)

    // Create a request body with the TSV data and the correct media type
    val requestBody = tsvData.toRequestBody("text/tab-separated-values".toMediaTypeOrNull())
//    Log.i("api", tsvData)

    // Build the request
    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

    // Make the network call
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("api", "Request failed: ${e.message}", e)
        }

        override fun onResponse(call: Call, response: Response) {
            val responseBody = response.body?.string() ?: "No response body"
            if (response.isSuccessful) {
                Log.i("api", "TSV data sent successfully! Response: $responseBody")
            } else {
                Log.i("api", "Failed to send TSV data. Response: $responseBody")
            }
        }
    })
}

private fun getSslConfiguredClient(context: Context): OkHttpClient {
    val certificateFactory = CertificateFactory.getInstance("X.509")
    val certificate = context.resources.openRawResource(R.raw.cert).use {
        certificateFactory.generateCertificate(it)
    }

    // Create a KeyStore containing our trusted CA
    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
        load(null, null)
        setCertificateEntry("ca", certificate)
    }

    // Create a TrustManager that trusts the CAs in our KeyStore
    val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
        init(keyStore)
    }
    val trustManagers = trustManagerFactory.trustManagers
    val sslContext = SSLContext.getInstance("TLS").apply {
        init(null, trustManagers, null)
    }

    // Build OkHttpClient with the configured SSL context
    return OkHttpClient.Builder()
        .sslSocketFactory(sslContext.socketFactory, trustManagers[0] as X509TrustManager)
        .build()
}

fun saveTsvToDownloads(context: Context, tsvData: String): Uri? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Android 10 and higher: Use MediaStore API
        saveTsvToDownloadsForQAndAbove(context, tsvData)
    } else {
        // Android 9 and lower: Directly write to external storage
        saveTsvToDownloadsForPreQ(context, tsvData)
    }
}

// For Android 10+ (Q and above)
@RequiresApi(Build.VERSION_CODES.Q)
private fun saveTsvToDownloadsForQAndAbove(context: Context, tsvData: String): Uri? {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "key_presses.tsv")
        put(MediaStore.MediaColumns.MIME_TYPE, "text/tab-separated-values")
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
                stream.write(tsvData.toByteArray())
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
private fun saveTsvToDownloadsForPreQ(context: Context, tsvData: String): Uri? {
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File(downloadsDir, "key_presses.tsv")

    return try {
        val fileOutputStream = FileOutputStream(file)
        fileOutputStream.use { stream ->
            stream.write(tsvData.toByteArray())
            Toast.makeText(context, "File saved to: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        }
        Uri.fromFile(file)  // Return Uri of the saved file
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to save file", Toast.LENGTH_SHORT).show()
        null
    }
}
