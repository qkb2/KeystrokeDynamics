package pl.poznan.put.keystrokedynamics.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension to create a DataStore instance
private val Context.dataStore by preferencesDataStore(name = "settings")

class UserPreferences(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val LOGGED_IN_KEY = booleanPreferencesKey("logged_in")
        val USERNAME_KEY = stringPreferencesKey("username")
    }

    // Get the login state
    val isLoggedIn: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[LOGGED_IN_KEY] ?: false
        }

    // Save the login state
    suspend fun setLoggedIn(loggedIn: Boolean, username: String) {
        dataStore.edit { preferences ->
            preferences[LOGGED_IN_KEY] = loggedIn
            preferences[USERNAME_KEY] = username
        }
    }
}
