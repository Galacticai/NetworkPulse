package com.galacticai.networkpulse.models

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import com.galacticai.networkpulse.ui.main.screens.dataStore

object Settings {
    object Defaults {
        suspend fun restore(context: Context) {
            context.dataStore.edit {
                it[requestIntervalKey] = requestInterval
                it[graphWidthKey] = graphWidth.value
            }
        }

        const val requestInterval = 20_000L
        val graphWidth = 15.dp
    }

    val requestIntervalKey get() = longPreferencesKey("requestInterval")
    val graphWidthKey get() = floatPreferencesKey("graphWidth")
}