package com.galacticai.networkpulse.ui.main.screens

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.await
import com.galacticai.networkpulse.common.models.bit_value.BitUnitBase
import com.galacticai.networkpulse.common.models.bit_value.BitValue
import com.galacticai.networkpulse.common.openURL
import com.galacticai.networkpulse.common.restartApp
import com.galacticai.networkpulse.common.ui.CustomDropdownMenu
import com.galacticai.networkpulse.models.DownloadSize
import com.galacticai.networkpulse.models.settings.Setting
import com.galacticai.networkpulse.models.settings.Setting.ValueUnitBase.JsonableBitUnitBase.Companion.jsonable
import com.galacticai.networkpulse.ui.MainActivity
import com.galacticai.networkpulse.ui.PrepareActivity
import com.galacticai.networkpulse.ui.common.TopBar
import com.galacticai.networkpulse.ui.dialogs.reloadAppDialog
import com.galacticai.networkpulse.ui.dialogs.resetDialog
import com.galacticai.networkpulse.ui.settings.SettingsGroup
import com.galacticai.networkpulse.ui.settings.SettingsItem
import com.galacticai.networkpulse.ui.settings.SettingsSlider
import com.galacticai.networkpulse.ui.settings.SettingsSwitch


const val SETTINGS = "settings"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS)

@Composable
fun SettingsScreen() {
    val ctx = LocalContext.current

    val title = stringResource(R.string.settings)
    Column {
        TopBar(title) {
            IconButton(onClick = {
                resetDialog(ctx, title) { _, _ ->
                    await { Setting.restoreAll(ctx) }
                    restartApp(ctx as MainActivity, PrepareActivity::class.java)
                }
            }) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = stringResource(R.string.reset)
                )
            }
        }
        val primaryContainer = colorResource(R.color.surface)
        Surface(
            modifier = Modifier.padding(horizontal = 10.dp),
            color = primaryContainer,
            border = BorderStroke(2.dp, primaryContainer),
            shape = RoundedCornerShape(20.dp)
        ) {
            LazyColumn {
                fun spacer() = item { Spacer(modifier = Modifier.height(10.dp)) }
                item {
                    SettingsGroup(
                        title = stringResource(R.string.title_pulse),
                        items = listOf(
                            itemEnablePulseService(ctx),
                            itemPulseInterval(ctx),
                            itemDownloadSize(ctx),
                            itemDownloadPerDay(ctx),
                        )
                    ).Content()
                }
                spacer()
                item {
                    SettingsGroup(
                        title = stringResource(R.string.display),
                        items = listOf(
                            //TODO: should I torture myself and keep this? (keep = always fetch before displaying values)
                            //itemValueUnit(ctx),
                            itemShowSummary(ctx),
                            itemGraphHeight(ctx),
                            itemGraphScaleLinesCount(ctx),
                            itemGraphCellSize(ctx),
                            itemGraphCellSpacing(ctx),
                        )
                    ).Content()
                }
                spacer()
                item {
                    SettingsGroup(
                        title = stringResource(R.string.about_app),
                        items = listOf(
                            itemVersion(ctx),
                            itemSourceCode(ctx),
                            itemCopyright(ctx),
                            itemLicense(ctx),
                        )
                    ).Content()
                }
            }
        }
    }
}


private fun itemEnablePulseService(context: Context): SettingsItem {
    return SettingsSwitch(
        switchTitle = context.getString(R.string.enable_pulse_service_setting_title),
        switchSubtitle = context.getString(R.string.enable_pulse_service_setting_description),
        setting = Setting.EnablePulseService,
    ) { reloadAppDialog(context) }
}

private fun itemPulseInterval(context: Context): SettingsItem {
    return SettingsSlider(
        title = context.getString(R.string.pulse_interval_setting_title),
        subtitle = context.getString(R.string.pulse_interval_setting_description),
        range = 2_000f..600_000f,
        steps = (600_000 / 2_000) - 2,
        setting = Setting.RequestInterval,
        valueTextStartWidth = 55.dp,
        valueTextEndWidth = 55.dp,
        valueTextStart = { v, _ ->
            val seconds = v.toLong() / 1000
            val m = seconds / 60
            val s = seconds % 60
            "${m}m${s}s"
        },
        valueTextEnd = { _, r -> "${r.endInclusive.toInt() / 1000 / 60}m" },
        onFinishedValue = { reloadAppDialog(context) }
    )
}

private fun itemDownloadSize(context: Context): SettingsItem {
    val title = context.getString(R.string.download_size_setting_title)
    return SettingsItem(
        title = title,
        subtitle = context.getString(R.string.download_size_setting_description),
        onResetClick = {
            resetDialog(context, title) { _, _ ->
                await { Setting.DownloadSize.restoreDefault(context) }
                restartApp(context as MainActivity, PrepareActivity::class.java)
            }
        },
    ) {
        val entries = DownloadSize.entries
        var selected by remember { mutableStateOf(Setting.DownloadSize.defaultObject) }
        LaunchedEffect(Unit) { selected = Setting.DownloadSize.getObject(context) }

        Spacer(modifier = Modifier.height(10.dp))

        CustomDropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            list = entries,
            defaultSelected = selected,
            color = colorResource(R.color.primary),
            colorBG = colorResource(R.color.background),
            onSelected = {
                resetDialog(context, title) { _, _ ->
                    selected = it
                    await { Setting.DownloadSize.setObject(context, it) }
                    restartApp(context as MainActivity, PrepareActivity::class.java)
                }
            }
        )
    }
}

private fun itemDownloadPerDay(context: Context): SettingsItem {
    val title = context.getString(R.string.data_usage_setting_title)
    return SettingsItem(
        title = title,
        subtitle = context.getString(R.string.data_usage_setting_description),
    ) {
        var text by remember { mutableStateOf(context.getString(R.string.calculating)) }
        LaunchedEffect(Unit) {
            val interval = Setting.RequestInterval.get(context)
            val size = Setting.DownloadSize.getObject(context)
            val day = 24 * 60 * 60 * 1000
            val timesPerDay = day / interval
            val totalSize = BitValue(size.value * timesPerDay, size.unit).toNearestUnit()
            text = "$totalSize/${context.getString(R.string.day)}"
        }
        Text(
            text = text,
            color = colorResource(R.color.primary)
        )
    }
}

private fun itemShowSummary(context: Context): SettingsItem {
    return SettingsSwitch(
        switchTitle = context.getString(R.string.summarize_setting_title),
        switchSubtitle = context.getString(R.string.summarize_setting_description),
        setting = Setting.Summarize,
    )
}


private fun itemValueUnit(context: Context): SettingsItem {
    val title = context.getString(R.string.value_unit_setting_title)
    return SettingsItem(
        title = title,
        subtitle = context.getString(R.string.value_unit_setting_description),
        onResetClick = {
            resetDialog(context, title) { _, _ ->
                await { Setting.ValueUnitBase.restoreDefault(context) }
                restartApp(context as MainActivity, PrepareActivity::class.java)
            }
        },
    ) {
        val entries = listOf(BitUnitBase.Bit.jsonable(), BitUnitBase.Byte.jsonable())
        var selected by remember { mutableStateOf(Setting.ValueUnitBase.defaultObject) }
        LaunchedEffect(Unit) { selected = Setting.ValueUnitBase.getObject(context) }

        Spacer(modifier = Modifier.height(10.dp))

        CustomDropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            list = entries,
            defaultSelected = selected,
            color = colorResource(R.color.primary),
            colorBG = colorResource(R.color.background),
            onSelected = {
                resetDialog(context, title) { _, _ ->
                    selected = it
                    await { Setting.ValueUnitBase.setObject(context, it) }
                    restartApp(context as MainActivity, PrepareActivity::class.java)
                }
            }
        )
    }
}

private fun itemGraphHeight(context: Context): SettingsSlider<Int> {
    return SettingsSlider(
        title = context.getString(R.string.graph_height_setting_title),
        subtitle = context.getString(R.string.graph_height_setting_description),
        range = 200f..450f,
        steps = 45 - 20 - 1,
        setting = Setting.GraphHeight,
        valueTextStartWidth = 40.dp,
        valueTextEndWidth = 40.dp,
        valueTextStart = { v, _ -> v.toInt().toString() },
        valueTextEnd = { _, r -> r.endInclusive.toInt().toString() },
    )
}

private fun itemGraphScaleLinesCount(context: Context): SettingsSlider<Int> {
    return SettingsSlider(
        title = context.getString(R.string.graph_scale_lines_count_setting_title),
        subtitle = context.getString(R.string.graph_scale_lines_count_setting_description),
        range = 2f..12f,
        steps = 12 - 2 - 1,
        setting = Setting.GraphScaleLinesCount,
        valueTextStartWidth = 40.dp,
        valueTextEndWidth = 40.dp,
        valueTextStart = { v, _ -> v.toInt().toString() },
        valueTextEnd = { _, r -> r.endInclusive.toInt().toString() },
    )
}

private fun itemGraphCellSize(context: Context): SettingsSlider<Int> {
    return SettingsSlider(
        title = context.getString(R.string.graph_cell_size_setting_title),
        subtitle = context.getString(R.string.graph_cell_size_setting_description),
        range = 20f..40f,
        steps = 40 - 15 - 1,
        setting = Setting.GraphCellSize,
        valueTextStartWidth = 40.dp,
        valueTextEndWidth = 40.dp,
        valueTextStart = { v, _ -> v.toInt().toString() },
        valueTextEnd = { _, r -> r.endInclusive.toInt().toString() },
    )
}

private fun itemGraphCellSpacing(context: Context): SettingsSlider<Int> {
    return SettingsSlider(
        title = context.getString(R.string.graph_cell_spacing_setting_title),
        subtitle = context.getString(R.string.graph_cell_spacing_setting_description),
        range = 0f..15f,
        steps = 15 - 2,
        setting = Setting.GraphCellSpacing,
        valueTextStartWidth = 40.dp,
        valueTextEndWidth = 40.dp,
        valueTextStart = { v, _ -> v.toInt().toString() },
        valueTextEnd = { _, r -> r.endInclusive.toInt().toString() },
    )
}

private fun itemVersion(context: Context): SettingsItem {
    //? For preview only
    if (context !is MainActivity) {
        return SettingsItem(
            title = context.getString(R.string.version),
            subtitle = "version",
        )
    }

    val packageInfo: PackageInfo = context.packageManager.getPackageInfo(
        context.packageName,
        PackageManager.GET_META_DATA
    )
    val version = packageInfo.versionName
    return SettingsItem(
        title = context.getString(R.string.version),
        subtitle = "v$version",
    ) {
        TextButton(onClick = {
            context.openURL("https://github.com/Galacticai/NetworkPulse/releases")
        }) {
            Text(context.getString(R.string.all_releases))
        }
    }
}


private fun itemSourceCode(context: Context): SettingsItem {
    val link = "github.com/Galacticai/NetworkPulse"
    return SettingsItem(
        title = context.getString(R.string.github_repository),
        subtitle = link,
    ) {
        TextButton(onClick = {
            context.openURL("https://$link")
        }) {
            Text(context.getString(R.string.open))
        }
    }
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
        Row {
            TextButton(onClick = {
                context.openURL("https://github.com/Galacticai/NetworkPulse/blob/master/LICENSE.txt")
            }) {
                Text(context.getString(R.string.more_information))
            }
            Spacer(modifier = Modifier.width(10.dp))
            TextButton(onClick = {
                context.openURL("https://www.gnu.org/licenses/gpl-3.0.en.html")
            }) {
                Text(context.getString(R.string.about_gpl_3_0))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}
