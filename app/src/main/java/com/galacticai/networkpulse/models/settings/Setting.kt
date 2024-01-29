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
import com.galacticai.networkpulse.common.number_with_unit.NumberUnit
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
            GraphWidth.restoreDefault(context)
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
        defaultValue: Jsonable
    ) : Setting<String>(
        keyName,
        defaultValue.toJson().toString()
    ) {
        open suspend fun setObject(context: Context, value: T) =
            super.set(context, value.toJson().toString())

        abstract suspend fun getObject(context: Context): T
    }

    data object RequestInterval : Setting<Long>(
        keyName = "RequestInterval",
        defaultValue = 20_000L
    )

    data object GraphWidth : Setting<Int>(
        keyName = "GraphWidth",
        defaultValue = 32
    )

    data object DownloadSize : SettingsObject<com.galacticai.networkpulse.models.DownloadSize>(
        keyName = "DownloadSize",
        defaultValue = com.galacticai.networkpulse.models.DownloadSize.Size10K
    ) {
        override suspend fun getObject(context: Context): com.galacticai.networkpulse.models.DownloadSize {
            val json = JSONObject(super.get(context))
            val size = json.getInt("value")
            val unit = NumberUnit.fromJson(json.getJSONObject("unit"))
            return com.galacticai.networkpulse.models.DownloadSize.fromString("$size$unit")
        }
    }
}
