package com.galacticai.networkpulse.models.settings

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.galacticai.networkpulse.common.models.Jsonable
import com.galacticai.networkpulse.common.models.bit_value.BitUnit
import com.galacticai.networkpulse.common.models.bit_value.BitUnitBase
import com.galacticai.networkpulse.common.models.bit_value.BitUnitExponent
import com.galacticai.networkpulse.common.models.bit_value.ShortLongName
import com.galacticai.networkpulse.ui.main.screens.dataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.json.JSONObject

sealed class Setting<T>(
    val keyName: String,
    val defaultValue: T,
) {
    companion object {
        val entries
            get() = listOf(
                RequestInterval,
                DownloadSize,
                Summarize,
                GraphHeight,
                GraphScaleLinesCount,
                GraphCellSize,
                GraphCellSpacing,
            )

        suspend fun restoreAll(c: Context) = entries.forEach { it.restoreDefault(c) }
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
    suspend fun set(context: Context, value: T) {
        context.dataStore.edit { it[key as Preferences.Key<T>] = value }
    }

    @Composable
    fun remember(): MutableState<T> {
        val context = LocalContext.current
        val setting = rememberSaveable { mutableStateOf(defaultValue) }
        LaunchedEffect(Unit) { setting.value = get(context) }
        return setting
    }

    suspend fun restoreDefault(context: Context) =
        set(context, defaultValue)

    abstract class SettingsObject<T : Jsonable>(
        keyName: String,
        val defaultObject: T
    ) : Setting<String>(
        keyName,
        defaultObject.toJson().toString()
    ) {
        open suspend fun setObject(context: Context, value: T) {
            super.set(context, value.toJson().toString())
        }

        abstract suspend fun getObject(context: Context): T

        @Composable
        fun rememberObject(): MutableState<T> {
            val context = LocalContext.current
            val setting = rememberSaveable { mutableStateOf(defaultObject) }
            LaunchedEffect(Unit) { setting.value = getObject(context) }
            return setting
        }
    }

    data object EnablePulseService : Setting<Boolean>(
        keyName = "EnablePulseService",
        defaultValue = true
    )

    data object RequestInterval : Setting<Long>(
        keyName = "RequestInterval",
        defaultValue = 20_000L
    )

    data object DownloadSize : SettingsObject<com.galacticai.networkpulse.models.DownloadSize>(
        keyName = "DownloadSize",
        defaultObject = com.galacticai.networkpulse.models.DownloadSize(
            100,
            BitUnit(BitUnitExponent.Metric.Kilo, BitUnitBase.Byte)
        )
    ) {
        override suspend fun getObject(context: Context): com.galacticai.networkpulse.models.DownloadSize =
            com.galacticai.networkpulse.models.DownloadSize.fromJson(
                JSONObject(super.get(context))
            )
    }

    data object RecentRecordsTime : Setting<Int>(
        keyName = "RecentRecordsTime",
        defaultValue = 1000 * 60 * 60 * 2
    )

    data object Summarize : Setting<Boolean>(
        keyName = "Summarize",
        defaultValue = true
    )

    data object ValueUnitBase : SettingsObject<ValueUnitBase.JsonableBitUnitBase>(
        keyName = "ValueUnitBase",
        defaultObject = JsonableBitUnitBase.fromOriginalType(BitUnitBase.Byte)
    ) {
        class JsonableBitUnitBase(name: ShortLongName, toBitMultiplier: Int) :
            BitUnitBase(name, toBitMultiplier), Jsonable {
            fun toOriginalType(): BitUnitBase = BitUnitBase(name, toBitMultiplier)
            override fun toJson() = JSONObject().apply {
                put("toBitMultiplier", toBitMultiplier)
                put("name", JSONObject().apply {
                    put("short", name.short)
                    put("long", name.long)
                })
            }

            companion object {
                fun BitUnitBase.jsonable() = fromOriginalType(this)
                fun fromOriginalType(bitUnitBase: BitUnitBase): JsonableBitUnitBase =
                    JsonableBitUnitBase(bitUnitBase.name, bitUnitBase.toBitMultiplier)

                fun fromJson(json: JSONObject): JsonableBitUnitBase {
                    val toBitMultiplier = json.getInt("toBitMultiplier")
                    val nameJ = json.getJSONObject("name")
                    val name = ShortLongName(
                        nameJ.getString("short"),
                        nameJ.getString("long")
                    )
                    return JsonableBitUnitBase(name, toBitMultiplier)
                }
            }
        }

        override suspend fun getObject(context: Context): JsonableBitUnitBase =
            JsonableBitUnitBase.fromJson(
                JSONObject(super.get(context))
            )
    }

    data object GraphHeight : Setting<Int>(
        keyName = "GraphHeight",
        defaultValue = 200
    )

    data object GraphScaleLinesCount : Setting<Int>(
        keyName = "GraphScaleLinesCount",
        defaultValue = 10
    )

    data object GraphCellSize : Setting<Int>(
        keyName = "GraphCellSize",
        defaultValue = 35
    )

    data object GraphCellSpacing : Setting<Int>(
        keyName = "GraphCellSpacing",
        defaultValue = 2
    )

}
