package pl.poznan.put.keystrokedynamics.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

        val scrollState = rememberScrollState()
        val focusRequester = remember { FocusRequester() }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(56.dp) // Fix the height of the TextField
                .focusRequester(focusRequester)
        ) {
            BasicTextField(
                value = textState,
                onValueChange = { newTextState ->
                    val newText = newTextState.text
                    val oldText = textState.text
                    val cursorPosition = newTextState.selection.start

                    if (newText.length - oldText.length == 1 && cursorPosition > 0) {
                        // Key pressed (a new character added at cursor position)
                        viewModel.onKeyPress(newText[cursorPosition - 1].toString())
                    } else if (newText.length < oldText.length) {
                        viewModel.onKeyPress("DEL")
                    }

                    if (newText.length - oldText.length > 1) {
                        onTextChanged(textState)
                    } else {
                        onTextChanged(newTextState) // inform about text change
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState) // Horizontal scrolling enabled
                    .focusRequester(focusRequester)
                    .border(
                        width = 1.dp, // Border width
                        color = Color.Gray, // Border color
                        shape = RoundedCornerShape(8.dp) // Rounded corners
                    )
                    .padding(4.dp),
                singleLine = true, // Prevents multiline expansion
                decorationBox = { innerTextField ->
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (textState.text.isEmpty()) {
                            Text("Type something really cool 😎", color = Color.Gray)
                        }
                        innerTextField() // Render the actual text field
                    }
                },
                textStyle = TextStyle(color = Color.Black, fontSize = 16.sp), // Text styling
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Password
                ),
                keyboardActions = KeyboardActions() {}
            )
            LaunchedEffect(textState) {
                // Auto-scroll to the end of the text
                scrollState.scrollTo(scrollState.maxValue)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}