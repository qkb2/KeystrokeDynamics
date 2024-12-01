package pl.poznan.put.keystrokedynamics.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import pl.poznan.put.keystrokedynamics.R
import pl.poznan.put.keystrokedynamics.data.MainViewModel

@Composable
fun TestingScreen(viewModel: MainViewModel) {
    // min chars
    val minChars = 100
    val minPhases = -1 // WARNING! Monkey patch sentinel value! Must NECESSARILY be set to -1
    // text states
    var textState by remember { mutableStateOf(TextFieldValue("")) }
    var symWritten by remember { mutableIntStateOf(0) }
    // API responses states
    var responseString by remember { mutableStateOf("") }
    // recognition percentage
    var percentage by remember { mutableStateOf(0f) }


    // Training Screen
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.send_for_inference))

        Spacer(modifier = Modifier.size(30.dp))

        KeyPressReader(viewModel = viewModel, minChars, minPhases, textState) { newText ->
            textState = newText
            symWritten++
        }

        DownloadButton(
            viewModel = viewModel,
            stringResource(R.string.send_to_infer),
            textState,
            minChars,
            minPhases,
            symWritten,
            onResponse = { str -> responseString = str }
        ) {
            textState = TextFieldValue("")
        }

        Spacer(modifier = Modifier.size(30.dp))

        // buttons for testing
//        Row(
//            horizontalArrangement = Arrangement.spacedBy(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Button(onClick = { percentage = 0f }) {
//                Text("0%")
//            }
//            Button(onClick = { percentage = 1f }) {
//                Text("100%")
//            }
//            Spacer(modifier = Modifier.size(30.dp))
//        }


        // TODO: pass the percentage value from the response to percentage var
        // percentage 0.75 == 75%
        RecognitionBar(percentage, size = 200)

        Spacer(modifier = Modifier.size(24.dp))

        // TODO: prettify the response body
        Text(responseString)

    }


}
