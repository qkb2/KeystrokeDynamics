package pl.poznan.put.keystrokedynamics.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import pl.poznan.put.keystrokedynamics.data.MainViewModel

@Composable
fun KeyPressReader(viewModel: MainViewModel) {
    var text by remember { mutableStateOf("") }

    Column {
        BasicTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.onKeyEvent { keyEvent ->
                when (keyEvent.type) {
                    KeyEventType.KeyDown -> {
                        viewModel.onKeyPress(keyEvent.key.toString())
                    }
                    KeyEventType.KeyUp -> {
                        viewModel.onKeyRelease(keyEvent.key.toString())
                    }
                    else -> Unit
                }
                false
            }
        )
        Text("Type something...")
        Spacer(modifier = Modifier.height(16.dp))
    }
}