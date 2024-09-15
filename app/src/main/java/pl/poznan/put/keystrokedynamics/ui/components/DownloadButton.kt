package pl.poznan.put.keystrokedynamics.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import pl.poznan.put.keystrokedynamics.data.MainViewModel

@Composable
fun DownloadButton(viewModel: MainViewModel) {
    val context = LocalContext.current

    // Create a launcher for requesting storage permission (for Android 9 and below)
    val requestStoragePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, proceed with CSV export
            viewModel.exportDataToCsv(context)
        } else {
            // Permission denied
            Toast.makeText(context, "Storage permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to check and request storage permission for Android 9 and below
    fun checkAndRequestStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request permission for Android 9 and below
                requestStoragePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            } else {
                // Permission already granted, proceed with CSV export
                viewModel.exportDataToCsv(context)
            }
        } else {
            // For Android 10+, no permission is needed, directly export data
            viewModel.exportDataToCsv(context)
        }
    }

    Button(onClick = { checkAndRequestStoragePermission() }) {
        Text("Export Data to CSV")
    }
}