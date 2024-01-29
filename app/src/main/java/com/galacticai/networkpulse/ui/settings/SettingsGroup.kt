package com.galacticai.networkpulse.ui.settings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.galacticai.networkpulse.common.ui.ExpandableGroup

class SettingsGroup(
    val title: String,
    private vararg val items: SettingsItem
) {
    @Composable
    fun Content() {
        val contents =
            items.map<SettingsItem, @Composable () -> Unit> {
                @Composable {
                    it.grouped = true
                    it.Content()
                } //! function ref is not available for @Composable
            }.toTypedArray()
        ExpandableGroup(title, items = contents)
    }
}

@Preview
@Composable
fun SettingsGroupPreview() {
    SettingsGroup(
        title = "Settings",
        SettingsItem("Title", "Subtitle") { },
        SettingsItem("Title", "Subtitle") { },
        SettingsItem("Title", "Subtitle") {
            Row(modifier = Modifier.fillMaxWidth()) { }
        },
    ).Content()
}