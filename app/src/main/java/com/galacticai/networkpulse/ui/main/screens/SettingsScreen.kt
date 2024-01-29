package com.galacticai.networkpulse.ui.main.screens

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.restartApp
import com.galacticai.networkpulse.common.ui.CustomDropdownMenu
import com.galacticai.networkpulse.models.DownloadSize
import com.galacticai.networkpulse.models.settings.Setting
import com.galacticai.networkpulse.ui.MainActivity
import com.galacticai.networkpulse.ui.PrepareActivity
import com.galacticai.networkpulse.ui.common.TopBar
import com.galacticai.networkpulse.ui.dialogs.reloadAppDialog
import com.galacticai.networkpulse.ui.dialogs.resetDialog
import com.galacticai.networkpulse.ui.settings.SettingsGroup
import com.galacticai.networkpulse.ui.settings.SettingsItem
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
                        job.complete()
                    }
                    restartApp(ctx as MainActivity, PrepareActivity::class.java)
                }
            }) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = stringResource(R.string.reset)
                )
            }
        }
        LazyColumn(
            modifier = Modifier.padding(horizontal = 10.dp),
        ) {
            fun spacer() = item { Spacer(modifier = Modifier.height(10.dp)) }
            item {
                SettingsGroup(
                    title = stringResource(R.string.title_pulse),
                    itemPulseInterval(ctx),
                    itemDownloadSize(ctx)
                ).Content()
            }
            spacer()
            item {
                SettingsGroup(
                    title = stringResource(R.string.display),
                    itemGraphWidth(ctx)
                ).Content()
            }
            spacer()
            item {
                SettingsGroup(
                    title = stringResource(R.string.about),
                    itemVersion(ctx),
                    itemCopyright(ctx),
                    itemLicense(ctx),
                ).Content()
            }
        }
    }
}


private fun itemPulseInterval(context: Context): SettingsItem {
    return SettingsSlider(
        title = context.getString(R.string.pulse_interval_setting_title),
        subtitle = context.getString(R.string.pulse_interval_setting_description),
        range = 2_000f..600_000f,
        //        steps = (600_000 / 2_000) - 2,
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
        onFinishedValue = { reloadAppDialog(context) }
    )
}

private fun itemDownloadSize(context: Context): SettingsItem {
    val title = context.getString(R.string.download_size_setting_title)
    return SettingsItem(
        title = title,
        subtitle = context.getString(R.string.download_size_setting_description),
        onResetClick = {
            val job = Job()
            CoroutineScope(Dispatchers.IO + job).launch {
                Setting.DownloadSize.restoreDefault(context)
                job.complete()
            }
            reloadAppDialog(context)
        },
    ) {
        val entries = DownloadSize.entries
        var selected by remember { mutableStateOf<DownloadSize>(DownloadSize.Size10K) }
        LaunchedEffect(Unit) { selected = Setting.DownloadSize.getObject(context) }

        CustomDropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            list = entries,
            defaultSelected = selected,
            color = colorResource(R.color.primary),
            colorBG = colorResource(R.color.background),
            itemText = { "$it" },
            onSelected = {
                resetDialog(context, title) { _, _ ->
                    selected = it
                    val job = Job()
                    CoroutineScope(Dispatchers.IO + job).launch {
                        Setting.DownloadSize.setObject(context, it)
                        job.complete()
                    }
                    reloadAppDialog(context)
                }
            }
        )
    }
}

private fun itemVersion(context: Context): SettingsItem {
    //? For preview only
    if (context !is MainActivity) return return SettingsItem(
        title = context.getString(R.string.version),
        subtitle = "version",
    )

    val packageInfo: PackageInfo = context.packageManager.getPackageInfo(
        context.packageName,
        PackageManager.GET_META_DATA
    )
    val version = packageInfo.versionName
    return SettingsItem(
        title = context.getString(R.string.version),
        subtitle = version,
    )
}

private fun itemGraphWidth(context: Context): SettingsSlider<Int> {
    return SettingsSlider(
        title = context.getString(R.string.graph_width_setting_title),
        subtitle = context.getString(R.string.graph_width_setting_description),
        range = 15f..60f,
        steps = 60 - 15 - 2,
        setting = Setting.GraphWidth,
        valueTextStartWidth = 55.dp,
        valueTextStart = { v, _ -> "${v.toInt()}dp" },
        valueTextEnd = { _, r -> "${r.endInclusive.toInt()}dp" },
        onFinishedValue = {}
    )
}


private fun itemCopyright(context: Context): SettingsItem {
    return SettingsItem(
        title = context.getString(R.string.copyright),
        subtitle = context.getString(R.string.copyright_value),
    )
}

private fun itemLicense(context: Context): SettingsItem {
    return SettingsItem(
        title = context.getString(R.string.license),
        subtitle = context.getString(R.string.gpl3_0_or_later),
    ) {
        ClickableText(
            text = AnnotatedString(context.getString(R.string.more_information)),
            style = MaterialTheme.typography.bodyMedium.copy(colorResource(R.color.primary)),
            onClick = {
                context.startActivity(Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.gnu.org/licenses/gpl-3.0.en.html")
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(null)
}
