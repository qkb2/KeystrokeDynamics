package pl.poznan.put.keystrokedynamics.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pl.poznan.put.keystrokedynamics.data.MainViewModel

@Composable
fun KeyPressReader(viewModel: MainViewModel) {
    var text by remember { mutableStateOf("") }

    Column {
        TextField(
            value = text,
            placeholder = {
                Text("Type something really cool 😎", color = Color.Gray)
            },
            onValueChange = { newText ->
                val currentTime = System.currentTimeMillis()
                if (newText.length > text.length) {
                    // Key pressed (a new character added)
                    viewModel.onKeyPress(newText.last().toString())
                }
                text = newText
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                autoCorrectEnabled = false
                ), // Use text keyboard
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}