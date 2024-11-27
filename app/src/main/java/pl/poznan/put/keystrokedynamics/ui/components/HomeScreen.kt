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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
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
            Text("Logout")
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
            text = "Choose a mode",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("testing") }) {
            Text(text = "Go to Testing Screen")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("training") }) {
            Text(text = "Go to Training Screen")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "If you have not trained the model yet, go to the training screen and follow the instructions. After " +
                    "completing training phases, if you are not connected to the server, please send five " +
                    "files located in Downloads to us.\n" +
                    "If you are connected to the server and want to test whether the model recognizes you, navigate to " +
                    "the testing screen.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}
