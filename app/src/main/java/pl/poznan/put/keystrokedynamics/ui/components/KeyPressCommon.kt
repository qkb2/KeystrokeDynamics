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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.poznan.put.keystrokedynamics.R
import pl.poznan.put.keystrokedynamics.data.MainViewModel
import kotlin.math.max

@Composable
fun KeyPressReader(
    viewModel: MainViewModel,
    minCount: Int,
    minStates: Int,
    textState: TextFieldValue,
    onTextChanged: (TextFieldValue) -> Unit) {
    val inputCount = textState.text.length

    Column {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = stringResource(
                    R.string.chars_and_phases,
                    max(0, minCount - inputCount),
                    max(0, minStates - viewModel.phasesCompleted.intValue)
                ),
                color = Color.Gray,
                textAlign = TextAlign.End,
                modifier = Modifier.padding(end = 8.dp)
            )
        }

        TextField(
            value = textState,
            placeholder = {
                Text(stringResource(R.string.type_here), color = Color.Gray)
            },
            onValueChange = { newTextState ->
                val newText = newTextState.text
                val oldText = textState.text
                val cursorPosition = newTextState.selection.start

                if (newText.length > oldText.length && cursorPosition > 0) {
                    // Key pressed (a new character added at cursor position)
                    viewModel.onKeyPress(newText[cursorPosition - 1].toString())
                } else if (newText.length < oldText.length) {
                    viewModel.onKeyPress("DEL")
                }

//                textState = newTextState
                onTextChanged(newTextState) // inform about text change
            },
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Password
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .heightIn(max = 200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}