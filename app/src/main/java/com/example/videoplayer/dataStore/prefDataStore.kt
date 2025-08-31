package com.example.videoplayer.dataStore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("app_prefs")

class prefDataStore(private val context: Context) {
    companion object{
        private val PERMISSION_GRANTED = booleanPreferencesKey("permission_granted")
    }


    fun isPermissionGranted(): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[PERMISSION_GRANTED] ?: false
        }
    }

    suspend fun setPermissionGranted(granted: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PERMISSION_GRANTED] = granted
        }
    }
}