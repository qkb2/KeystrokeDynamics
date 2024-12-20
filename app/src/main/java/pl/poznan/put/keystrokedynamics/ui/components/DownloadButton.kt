package pl.poznan.put.keystrokedynamics.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.content.ContextCompat
import pl.poznan.put.keystrokedynamics.R
import pl.poznan.put.keystrokedynamics.data.MainViewModel

@Composable
fun DownloadButton(
    viewModel: MainViewModel,
    buttonText: String,
    textState: TextFieldValue,
    minCount: Int,
    minPhases: Int,
    symWritten: Int,
    onResponse: (String) -> Unit,
    onTextReset: () -> Unit
) {
    val context = LocalContext.current

    // Create a launcher for requesting storage permission (for Android 9 and below)
    val requestStoragePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, proceed with TSV export
            viewModel.exportDataToTsv(context, minPhases, symWritten, onResponse)
        } else {
            // Permission denied
            Toast.makeText(context, context.getString(R.string.storage_permission_denied), Toast.LENGTH_SHORT).show()
        }
    }

    // Function to check and request storage permission for Android 9 and below
    fun checkAndRequestStoragePermission() {
        Log.i("TAG", "In check function")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request permission for Android 9 and below
                requestStoragePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            } else {
                // Permission already granted, proceed with TSV export
                viewModel.exportDataToTsv(context, minPhases, symWritten, onResponse)
            }
        } else {
            // For Android 10+, no permission is needed, directly export data
            viewModel.exportDataToTsv(context, minPhases, symWritten, onResponse)
        }
    }

    Button(onClick = {
        if (minPhases == -1) {
            if (textState.text.length >= minCount) {
                Log.i("TAG", "Inference button click OK.")
                checkAndRequestStoragePermission()
            }
        }
        else if (textState.text.length >= minCount) {
            viewModel.incrementPhase()
            if (viewModel.phasesCompleted.intValue >= minPhases) {
                Log.i("TAG", "Completed.")
                Toast.makeText(context, context.getString(R.string.send_files_message), Toast.LENGTH_LONG).show()
                checkAndRequestStoragePermission()
            } else {
                Log.i("TAG", "Next phase.")
                Toast.makeText(context, context.getString(R.string.change_position_message), Toast.LENGTH_SHORT).show()
                checkAndRequestStoragePermission()
                onTextReset()
                viewModel.onKeyPress("EPH") // end phase symbol
            }
        } else {
            Log.i("TAG", "Too short.")
            Toast.makeText(
                context,
                context.getString(R.string.enter_min_characters, minCount),
                Toast.LENGTH_SHORT
            ).show()
        }
    }) {
        Text(buttonText)
    }
}