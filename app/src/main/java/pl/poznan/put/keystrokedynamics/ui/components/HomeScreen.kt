package pl.poznan.put.keystrokedynamics.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.poznan.put.keystrokedynamics.data.MainViewModel


@Composable
fun HomeScreen(viewModel: MainViewModel){
    var text by remember { mutableStateOf("") }
    // Min Chars
    val minChars = 2000

    // Logout Button
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Button(
            onClick = { viewModel.logout() },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Logout")
        }
    }

    // Home Screen
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        KeyPressReader(viewModel = viewModel, minChars) { newText ->
            text = newText
        }
        DownloadButton(viewModel = viewModel, text, minChars)
    }
}
