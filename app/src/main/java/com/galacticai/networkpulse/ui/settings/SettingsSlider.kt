package com.galacticai.networkpulse.ui.settings

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.galacticai.networkpulse.models.settings.Setting
import com.galacticai.networkpulse.ui.dialogs.resetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class SettingsSlider<T : Number>(
    title: String,
    subtitle: String,
    grouped: Boolean = false,
    val range: ClosedFloatingPointRange<Float>,
    val steps: Int = 0,
    val setting: Setting<T>,
    val valueTextStart: (Float, range: ClosedFloatingPointRange<Float>) -> String,
    val valueTextEnd: (Float, range: ClosedFloatingPointRange<Float>) -> String,
    val valueTextStartWidth: Dp? = null,
    val valueTextEndWidth: Dp? = null,
    val onFinishedValue: (Float) -> Unit,
) : SettingsItem(title, subtitle, grouped, {
    resetDialog(it, title) { _, _ ->
        val job = Job()
        CoroutineScope(Dispatchers.IO + job).launch {
            setting.restoreDefault(it)
            onFinishedValue(setting.defaultValue.toFloat())
            job.complete()
        }
    }
}, @Composable {

    val ctx = LocalContext.current
    var value by rememberSaveable { mutableStateOf(setting.defaultValue as Number) }
    Log.d("SettingsSlider init", "$value")
    LaunchedEffect(Unit) {
        value = setting.get(ctx)
        Log.d("SettingsSlider get", "$value")
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        val textStart = valueTextStart(value.toFloat(), range)
        Text(
            textStart,
            fontSize = 14.sp,
            modifier = if (valueTextStartWidth == null) Modifier
            else Modifier.width(valueTextStartWidth)
        )
        Slider(modifier = Modifier.weight(1f),
            value = value.toFloat(),
            valueRange = range,
            steps = steps,
            onValueChange = { value = it },
            onValueChangeFinished =
            @Suppress("UNCHECKED_CAST") { //? T is a Number
                val job = Job()
                CoroutineScope(Dispatchers.IO + job).launch {
                    setting.set(ctx, value as T)
                    Log.d("SettingsSlider set", "$value")
                    job.complete()
                }
                onFinishedValue(value.toFloat())
            })
        val textEnd = valueTextEnd(value.toFloat(), range)
        Text(
            textEnd,
            fontSize = 14.sp,
            textAlign = TextAlign.End,
            modifier = if (valueTextEndWidth != null) Modifier.width(
                valueTextEndWidth
            ) else Modifier
        )
    }
})