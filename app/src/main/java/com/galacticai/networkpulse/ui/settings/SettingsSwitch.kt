package com.galacticai.networkpulse.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.await
import com.galacticai.networkpulse.models.settings.Setting
import com.galacticai.networkpulse.ui.util.Consistent


class SettingsSwitch(
    val switchTitle: String,
    val switchSubtitle: String,
    val setting: Setting<Boolean>,
    val onSwitched: ((Boolean) -> Unit)? = null,
) : SettingsItem(content = @Composable {
    val context = LocalContext.current
    var value by rememberSaveable { mutableStateOf(setting.defaultValue) }
    LaunchedEffect(Unit) { value = setting.get(context) }

    fun setValue(newValue: Boolean) {
        await { Setting.Summarize.set(context, newValue) }
        value = newValue
        onSwitched?.invoke(value)
    }

    Surface(
        onClick = { setValue(!value) },
        color = colorResource(R.color.background),
        shape = Consistent.shape
    ) {
        Column(modifier = Modifier.padding(it)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = switchTitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = value,
                    colors = SwitchDefaults.colors(
                        uncheckedTrackColor = colorResource(R.color.surface).copy(.5f),
                        uncheckedThumbColor = colorResource(R.color.onBackground).copy(.5f),
                        uncheckedBorderColor = colorResource(R.color.onBackground).copy(.5f),
                    ),
                    onCheckedChange = { setValue(it) },
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = switchSubtitle,
                fontSize = 14.sp
            )
        }
    }
})