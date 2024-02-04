package com.galacticai.networkpulse.models.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.galacticai.networkpulse.common.models.Jsonable
import com.galacticai.networkpulse.ui.main.screens.dataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.json.JSONObject

sealed class Setting<T>(
    val keyName: String,
    val defaultValue: T,
) {
    companion object {
        suspend fun restoreAll(context: Context) {
            RequestInterval.restoreDefault(context)
            GraphSize.restoreDefault(context)
            DownloadSize.restoreDefault(context)
        }
    }

    val key = when (defaultValue) {
        is Boolean -> booleanPreferencesKey(keyName)
        is Int -> intPreferencesKey(keyName)
        is Long -> longPreferencesKey(keyName)
        is Float -> floatPreferencesKey(keyName)
        is Double -> doublePreferencesKey(keyName)
        is String -> stringPreferencesKey(keyName)
        else -> throw IllegalArgumentException("Unsupported type")
    }

    @Suppress("UNCHECKED_CAST") //? set(..) will always save the value as T
    suspend fun get(context: Context): T = context
        .dataStore.data
        .map { it[key] }.firstOrNull() as T
        ?: defaultValue

    @Suppress("UNCHECKED_CAST") //? get(..) will always save the key as Preferences.Key<T>
    suspend fun set(context: Context, value: T) =
        context.dataStore.edit { it[key as Preferences.Key<T>] = value }

    suspend fun restoreDefault(context: Context) =
        set(context, defaultValue)

    sealed class SettingsObject<T : Jsonable>(
        keyName: String,
        val defaultObject: T
    ) : Setting<String>(
        keyName,
        defaultObject.toJson().toString()
    ) {
        open suspend fun setObject(context: Context, value: T) =
            super.set(context, value.toJson().toString())

        abstract suspend fun getObject(context: Context): T
    }

    data object RequestInterval : Setting<Long>(
        keyName = "RequestInterval",
        defaultValue = 20_000L
    )

    data object GraphSize : Setting<Int>(
        keyName = "GraphSize",
        defaultValue = 35
    )

    data object DownloadSize : SettingsObject<com.galacticai.networkpulse.models.DownloadSize>(
        keyName = "DownloadSize",
        defaultObject = com.galacticai.networkpulse.models.DownloadSize.Size10K
    ) {
        override suspend fun getObject(context: Context): com.galacticai.networkpulse.models.DownloadSize =
            com.galacticai.networkpulse.models.DownloadSize.fromJson(
                JSONObject(super.get(context))
            )
    }
}
