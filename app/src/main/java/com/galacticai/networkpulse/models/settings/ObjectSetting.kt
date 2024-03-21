package com.galacticai.networkpulse.models.settings

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import com.galacticai.networkpulse.common.models.Jsonable

abstract class ObjectSetting<T : Jsonable>(
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