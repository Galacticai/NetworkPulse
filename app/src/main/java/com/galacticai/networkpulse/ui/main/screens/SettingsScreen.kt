package com.galacticai.networkpulse.ui.main.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.restartApp
import com.galacticai.networkpulse.models.settings.Setting
import com.galacticai.networkpulse.ui.MainActivity
import com.galacticai.networkpulse.ui.PrepareActivity
import com.galacticai.networkpulse.ui.common.TopBar
import com.galacticai.networkpulse.ui.dialogs.reloadAppDialog
import com.galacticai.networkpulse.ui.dialogs.resetDialog
import com.galacticai.networkpulse.ui.settings.SettingsGroup
import com.galacticai.networkpulse.ui.settings.SettingsSlider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


const val SETTINGS = "settings"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS)

@Composable
fun SettingsScreen(mainActivity: MainActivity?) {
    val ctx = LocalContext.current

    val title = stringResource(R.string.settings)
    Column {
        TopBar(title) {
            IconButton(onClick = {
                resetDialog(ctx, title) { _, _ ->
                    val job = Job()
                    CoroutineScope(Dispatchers.IO + job).launch {
                        Setting.restoreAll(ctx)
                        restartApp(ctx as MainActivity, PrepareActivity::class.java)
                        job.complete()
                    }
                }
            }) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = stringResource(R.string.reset)
                )
            }
        }
        LazyColumn(modifier = Modifier.padding(horizontal = 10.dp)) {
            item {
                SettingsGroup(
                    "General",
                    SettingsSlider(
                        title = stringResource(R.string.pulse_interval_setting_title),
                        subtitle = stringResource(R.string.pulse_interval_setting_description),
                        range = 2_000f..600_000f,
                        steps = (600_000 / 2_000) - 2,
                        setting = Setting.RequestInterval,
                        valueTextStartWidth = 55.dp,
                        valueTextStart = { v, _ ->
                            val seconds = v.toLong() / 1000
                            val m = seconds / 60
                            val s = seconds % 60
                            "${m}m${s}s"
                        },
                        valueTextEnd = { _, r ->
                            "${r.endInclusive.toInt() / 1000 / 60}m"
                        },
                        onFinishedValue = { reloadAppDialog(ctx) }
                    ),
                    SettingsSlider(
                        title = stringResource(R.string.graph_width_setting_title),
                        subtitle = stringResource(R.string.graph_width_setting_description),
                        range = 15f..60f,
                        steps = 60 - 15 - 2,
                        setting = Setting.GraphWidth,
                        valueTextStartWidth = 55.dp,
                        valueTextStart = { v, _ -> "${v.toInt()}dp" },
                        valueTextEnd = { _, r -> "${r.endInclusive.toInt()}dp" },
                        onFinishedValue = {}
                    )
                ).Content()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(null)
}
