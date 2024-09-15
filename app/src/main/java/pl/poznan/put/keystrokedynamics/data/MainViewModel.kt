package pl.poznan.put.keystrokedynamics.data

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(
    private val keyPressDatabase: KeyPressDatabase,
    private val userPreferences: UserPreferences) : ViewModel() {
    private val keyPressDao = keyPressDatabase.keyPressDao()
    private var pressTimestamp: Long = 0

    val isLoggedIn: Flow<Boolean> = userPreferences.isLoggedIn

    fun login(username: String) {
        viewModelScope.launch {
            userPreferences.setLoggedIn(true, username)
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.setLoggedIn(false, "")
        }
    }

    fun onKeyPress(key: String) {
        val newPressTimestamp = System.currentTimeMillis()
        val duration = newPressTimestamp - pressTimestamp
        val keyPressEntity = KeyPressEntity(
            key = key,
            pressTime = newPressTimestamp,
            duration = duration
        )
        viewModelScope.launch {
            keyPressDao.insert(keyPressEntity)
        }
        // replace the old press timestamp
        pressTimestamp = newPressTimestamp
    }

    fun exportDataToCsv(context: Context) {
        viewModelScope.launch {
            val keyPresses = keyPressDao.getAllKeyPresses()
            val csvData = keyPressesToCsv(keyPresses)
            saveCsvToDownloads(context, csvData)
        }
    }
}