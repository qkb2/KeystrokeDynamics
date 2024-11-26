package pl.poznan.put.keystrokedynamics.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
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
fun TrainingScreen(viewModel: MainViewModel){
    // min chars
    val minChars = 300
    val minPhases = 5
    // text states
    var textState by remember { mutableStateOf(TextFieldValue("")) }
    var symWritten by remember { mutableIntStateOf(0) }

    // Testing Screen
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "After writing 300 words, change your position, e.g. sit down, stand up, lay down etc.")

        Spacer(modifier = Modifier.size(30.dp))

        KeyPressReader(viewModel = viewModel, minChars, minPhases, textState) { newText ->
            textState = newText
            symWritten++
        }

        DownloadButton(
            viewModel = viewModel,
            "Next Phase",
            textState,
            minChars,
            minPhases,
            symWritten,
            onResponse = { }
        ) {
            textState = TextFieldValue("")
            symWritten = 0
        }
    }
}