package pl.poznan.put.keystrokedynamics.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.poznan.put.keystrokedynamics.data.MainViewModel
import kotlin.math.max

@Composable
fun KeyPressReader(viewModel: MainViewModel, minCount: Int, onTextChanged: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    val inputCount = text.length

    Column {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = "Chars left to type " + max(0, minCount - inputCount),
                color = Color.Gray,
                textAlign = TextAlign.End,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        TextField(
            value = text,
            placeholder = {
                Text("Type something really cool 😎", color = Color.Gray)
            },
            onValueChange = { newText ->
                if (newText.length > text.length) {
                    // Key pressed (a new character added)
                    viewModel.onKeyPress(newText.last().toString())
                }
                else if (newText.length < text.length) {
                    viewModel.onKeyPress("DEL")
                }
                text = newText
                onTextChanged(newText) // inform about text change
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                autoCorrectEnabled = false
                ), // Use text keyboard
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .heightIn(max = 200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}