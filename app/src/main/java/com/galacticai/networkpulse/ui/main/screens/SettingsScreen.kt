package com.galacticai.networkpulse.ui.main.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.number_with_unit.NumberUnit
import com.galacticai.networkpulse.common.number_with_unit.NumberUnitBase
import com.galacticai.networkpulse.common.number_with_unit.NumberUnitPower
import com.galacticai.networkpulse.common.number_with_unit.NumberWithUnit
import com.galacticai.networkpulse.models.Settings
import com.galacticai.networkpulse.ui.MainActivity
import com.galacticai.networkpulse.ui.common.ScreenTitle
import com.galacticai.networkpulse.ui.theme.GalacticTheme
import java.text.DecimalFormat

const val SETTINGS = "settings"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS)

@Composable
fun SettingsScreen(mainActivity: MainActivity?) {
    val ctx = LocalContext.current

    var requestInterval by remember { mutableLongStateOf(Settings.Defaults.requestInterval) }
    var graphWidth by remember { mutableFloatStateOf(Settings.Defaults.graphWidth.value) }

    suspend {
        ctx.dataStore.data.collect {
            val requestIntervalKey = Settings.requestIntervalKey
            if (it[requestIntervalKey] != null) requestInterval = it[requestIntervalKey]!!

            val graphWidthKey = Settings.graphWidthKey
            if (it[graphWidthKey] != null) graphWidth = it[graphWidthKey]!!
        }
    }
    GalacticTheme {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ScreenTitle(text = stringResource(R.string.settings))
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        suspend { Settings.Defaults.restore(ctx) }
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = stringResource(R.string.reset)
                        )
                    }
                }
            }
        ) { scaffoldPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(scaffoldPadding)
                    .padding(10.dp)
            ) {
                item {
                    SettingsItem(
                        title = stringResource(R.string.pulse_interval_setting_title),
                        subtitle = stringResource(R.string.pulse_interval_setting_description)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val range = 2f..600f
                            val seconds = requestInterval / 1000
                            val minutes = seconds / 60
                            Text(
                                "${minutes}m${seconds%60}s",
                                fontSize = 14.sp,
                                modifier = Modifier.width(50.dp)
                            )
                            Slider(
                                modifier = Modifier.weight(.9f),
                                value = (requestInterval / 1000).toFloat(),
                                valueRange = range,
                                onValueChange = {
                                    requestInterval = (it * 1000).toLong()
                                },
                                onValueChangeFinished = {
                                    suspend {
                                        ctx.dataStore.edit {
                                            it[Settings.requestIntervalKey] = requestInterval
                                        }
                                    }
                                }
                            )
                            Text("${range.endInclusive.toInt() / 60}m", fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit,
) {
    Surface(
        color = colorResource(R.color.primaryContainer),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.padding(1.dp))
            Text(
                subtitle,
                fontSize = 14.sp,
                color = colorResource(R.color.onBackground).copy(alpha = .8f)
            )
            content()
        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(null)
}
