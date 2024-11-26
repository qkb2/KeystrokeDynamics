package pl.poznan.put.keystrokedynamics.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import pl.poznan.put.keystrokedynamics.data.MainViewModel

@Composable
fun TestingScreen(viewModel: MainViewModel) {
    // Min Chars
    val minChars = 100
    val minPhases = -1 // WARNING! Monkey patch sentinel value! Must NECESSARILY be set to -1
    var textState by remember { mutableStateOf(TextFieldValue("")) }
    var symWritten by remember { mutableIntStateOf(0) }
    // Training screen
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "After writing 100 words, send for inference and wait for the reply from the server.")

        Spacer(modifier = Modifier.size(30.dp))

        KeyPressReader(viewModel = viewModel, minChars, minPhases, textState) { newText ->
            textState = newText
            symWritten++
        }

        DownloadButton(
            viewModel = viewModel,
            "Send to infer",
            textState,
            minChars,
            minPhases,
            symWritten
        ) {
            textState = TextFieldValue("")
        }

        // TODO: mutable states for API call responses (accuracy etc.)
    }
}
