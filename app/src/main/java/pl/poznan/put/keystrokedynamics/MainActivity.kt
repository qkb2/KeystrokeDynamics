package pl.poznan.put.keystrokedynamics

import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.poznan.put.keystrokedynamics.data.MainViewModel
import pl.poznan.put.keystrokedynamics.data.KeyPressDatabase
import pl.poznan.put.keystrokedynamics.data.UserPreferences
import pl.poznan.put.keystrokedynamics.ui.components.LoginScreen
import pl.poznan.put.keystrokedynamics.ui.components.HomeScreen
import pl.poznan.put.keystrokedynamics.ui.components.TestingScreen
import pl.poznan.put.keystrokedynamics.ui.components.TrainingScreen
import pl.poznan.put.keystrokedynamics.ui.theme.KeystrokeDynamicsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KeystrokeDynamicsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val database = KeyPressDatabase.getDatabase(this)
                    val userPreferences = UserPreferences(this)
                    val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
                    val viewModel = MainViewModel(database, userPreferences, sensorManager)
                    viewModel.clearDatabase()
                    KeystrokeDynamicsApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun KeystrokeDynamicsApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState(initial = false)

    // Automatically navigate based on login state
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate("main") {
                popUpTo("login") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo("main") { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = if (isLoggedIn) "main" else "login") {
        composable("login") { LoginScreen(viewModel) }
        composable("main") { HomeScreen(viewModel, navController) }
        composable("testing") { TestingScreen(viewModel) }
        composable("training") { TrainingScreen(viewModel) }
    }
}