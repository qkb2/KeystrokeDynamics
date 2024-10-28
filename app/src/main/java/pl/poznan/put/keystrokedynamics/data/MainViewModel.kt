package pl.poznan.put.keystrokedynamics.data

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(
    private val keyPressDatabase: KeyPressDatabase,
    private val userPreferences: UserPreferences, private val sensorManager: SensorManager) : ViewModel() {
    private val keyPressDao = keyPressDatabase.keyPressDao()
    private var pressTimestamp = mutableLongStateOf(0)
    private var accelX: Float = 0.0f
    private var accelY: Float = 0.0f
    private var accelZ: Float = 0.0f

    val isLoggedIn: Flow<Boolean> = userPreferences.isLoggedIn
    val username: Flow<String> = userPreferences.username

    init {
        // Initialize SensorManager and start listening to accelerometer
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val sensorEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    accelX = it.values[0]
                    accelY = it.values[1]
                    accelZ = it.values[2]
                }
            }
        }
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun login(username: String) {
        viewModelScope.launch {
            userPreferences.setLoggedIn(true, username)
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.setLoggedIn(false, "")
            keyPressDao.clearDatabase()
        }
    }

    fun clearDatabase() {
        viewModelScope.launch {
            keyPressDao.clearDatabase()
        }
    }

    fun onKeyPress(key: String) {
        viewModelScope.launch {
            val newPressTimestamp = System.currentTimeMillis()
            val duration = newPressTimestamp - pressTimestamp.longValue
            var keyToInsert = key

            if (key == "\n") {
                keyToInsert = "NL"
            }
            else if (key == " ") {
                keyToInsert = "SP"
            }

            val keyPressEntity = KeyPressEntity(
                key = keyToInsert,
                pressTime = newPressTimestamp,
                duration = duration,
                accelX = accelX,
                accelY = accelY,
                accelZ = accelZ,
            )

            if (key.isNotEmpty() && pressTimestamp.longValue != 0L) {
                keyPressDao.insert(keyPressEntity)
            }

            pressTimestamp.longValue = newPressTimestamp
        }
    }


    fun exportDataToTsv(context: Context) {
        viewModelScope.launch {
            val keyPresses = keyPressDao.getAllKeyPresses()
            val tsvData = keyPressesToTsv(keyPresses)

            username.collect { user ->
                saveTsvToDownloads(context, tsvData)
                sendTsvToFastApi(tsvData, user, context)
            }
            // TODO: tsvData doesn't contain first letter
        }
    }
}