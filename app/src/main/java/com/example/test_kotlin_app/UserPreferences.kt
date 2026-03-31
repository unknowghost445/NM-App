package com.example.test_kotlin_app

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name="user_prefs")

class UserPreferences(private val context: Context) {
    companion object {
        val is_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    suspend fun saveLoginStatus(isLoggedIn: Boolean) {
        context.dataStore.edit {
            preferences -> preferences[is_LOGGED_IN] = isLoggedIn
        }
    }


    suspend fun clearLoginStatus() {
        context.dataStore.edit {
            preferences -> preferences.clear()
        }
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map {
        preferences -> preferences[is_LOGGED_IN] ?: false
    }
}