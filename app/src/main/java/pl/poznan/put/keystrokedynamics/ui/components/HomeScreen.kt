package pl.poznan.put.keystrokedynamics.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import pl.poznan.put.keystrokedynamics.R
import pl.poznan.put.keystrokedynamics.data.MainViewModel


@Composable
fun HomeScreen(viewModel: MainViewModel, navController: NavHostController){
    // Logout Button
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Button(
            onClick = { viewModel.logout() },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(stringResource(R.string.logout))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.choose_mode),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("testing") }) {
            Text(text = stringResource(R.string.go_to_testing_screen))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("training") }) {
            Text(text = stringResource(R.string.go_to_training_screen))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.training_instructions),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}
