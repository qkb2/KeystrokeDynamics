package pl.poznan.put.keystrokedynamics.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import pl.poznan.put.keystrokedynamics.data.MainViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import pl.poznan.put.keystrokedynamics.R
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(viewModel: MainViewModel) {
    var username by remember { mutableStateOf("") }
    val regex = Regex("^\\d*$")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.enter_id),
            modifier = Modifier.padding(bottom = 16.dp),
            textAlign = TextAlign.Center,
        )

        TextField(
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            value = username,
            onValueChange = {
                if (it.matches(regex)) username = it;
                            },
            label = { Text(stringResource(R.string.id)) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.login(username) },
            enabled = username.isNotBlank()
        )
        {
            Text(stringResource(R.string.log_in))
        }
    }
}