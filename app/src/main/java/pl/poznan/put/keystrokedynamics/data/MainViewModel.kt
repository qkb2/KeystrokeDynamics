package pl.poznan.put.keystrokedynamics.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take
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

    var phasesCompleted = mutableIntStateOf(0)

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
        Log.i("TAG", "Logging out")
        viewModelScope.launch {
            userPreferences.setLoggedIn(false, "")
            keyPressDao.clearDatabase()
            phasesCompleted.intValue = 0
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
            var duration = newPressTimestamp - pressTimestamp.longValue
            val keyToInsert = when(key) {
                "\n" -> "NL"      // New line
                " " -> "SP"       // Space
                "\"" -> "QM"      // Double quotation mark
                "\'" -> "AP"      // Single quotation mark
                "\\" -> "BS"      // Backslash
                "\t" -> "TB"      // Tab
                "\b" -> "BS"      // Backspace
                "\r" -> "CR"      // Carriage return
                "$" -> "DS"       // Dollar sign
                else -> key
            }

            /*
                Additional two characters can be passed as key to this method:
                DEL - when user uses backspace, and
                EPH - when user clicks "end phase" successfully
             */

            /* SUGGESTION:
                we could also stop treating those as chars and make them
                full on char tokens, e.g. encode them as Uxxxx no matter the char
            */

            if (pressTimestamp.longValue == 0L) {
                duration = 0L
            }

            val keyPressEntity = KeyPressEntity(
                key = keyToInsert,
                pressTime = newPressTimestamp,
                duration = duration,
                accelX = accelX,
                accelY = accelY,
                accelZ = accelZ,
            )

            if (key.isNotEmpty()) {
                keyPressDao.insert(keyPressEntity)
            }

            pressTimestamp.longValue = newPressTimestamp
        }
    }


    fun exportDataToTsv(context: Context, minPhases: Int, symWritten: Int) {
        viewModelScope.launch {
            val keyPresses = keyPressDao.getNLatestKeyPresses(symWritten)
            val tsvData = keyPressesToTsv(keyPresses)
            var apiString = "upload_tsv"
            var phases = phasesCompleted.intValue
            // WARNING! Monkey patch w/ sentinel value
            if (minPhases == -1) {
                apiString = "inference"
                phases = -1
            }
            else if (phasesCompleted.intValue == minPhases) {
                apiString = "train"
            }
            else if (phasesCompleted.intValue > minPhases) return@launch

            username.take(1).collect { user ->
                Log.i("TAG", "In export to tsv function")
                saveTsvToDownloads(context, user, phases, tsvData)
                sendTsvToFastApi(tsvData, user, apiString, context)
            }
        }
    }

    fun incrementPhase() {
        var ph = phasesCompleted.intValue
        ph++
        phasesCompleted.intValue = ph
        pressTimestamp.longValue = 0L // reset timestamp tmp value
    }
}
