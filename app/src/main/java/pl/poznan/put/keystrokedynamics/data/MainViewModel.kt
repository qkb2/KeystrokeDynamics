package pl.poznan.put.keystrokedynamics.data

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : ViewModel() {
    private val keyPressDatabase = KeyPressDatabase.getDatabase(application)
    private val keyPressDao = keyPressDatabase.keyPressDao()
    private var pressTimestamp: Long? = null

    private val userPreferences = UserPreferences(application)
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
        pressTimestamp = System.currentTimeMillis()
    }

    fun onKeyRelease(key: String) {
        val releaseTimestamp = System.currentTimeMillis()

        pressTimestamp?.let { pressTime ->
            val duration = releaseTimestamp - pressTime
            val keyPress = KeyPressEntity(
                key = key,
                pressTime = pressTime,
                duration = duration
            )
            viewModelScope.launch {
                keyPressDao.insert(keyPress)
            }
        }

        // Reset pressTimestamp after storing
        pressTimestamp = null
    }
}