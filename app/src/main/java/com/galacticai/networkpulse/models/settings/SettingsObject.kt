package com.galacticai.networkpulse.models.settings

import android.content.Context
import com.galacticai.networkpulse.common.models.Jsonable
import org.json.JSONObject

class SettingsObject<T : Jsonable>(
    keyName: String,
    defaultValue: Jsonable
) : Setting<String>(
    keyName,
    defaultValue.toString()
) {
    suspend fun setObject(context: Context, value: T) =
        super.set(context, value.toString())

    suspend fun getObject(context: Context): JSONObject =
        JSONObject(super.get(context))
}