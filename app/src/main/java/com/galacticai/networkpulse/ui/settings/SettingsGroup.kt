package com.galacticai.networkpulse.ui.settings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.ui.ExpandableGroup

class SettingsGroup(
    val title: String,
    private val items: List<SettingsItem>
) {
    @Composable
    fun Content() {
        ExpandableGroup(
            title,
            bgColor = colorResource(R.color.background),
            surfaceColor = colorResource(R.color.surface),
            items = items.map {
                @Composable { p ->
                    it.grouped = true
                    it.Content(p)
                }
            }
        )
    }
}

@Preview
@Composable
fun SettingsGroupPreview() {
    SettingsGroup(
        title = "Settings",
        items = listOf(
            SettingsItem("Title", "Subtitle") { },
            SettingsItem("Title", "Subtitle") { },
            SettingsItem("Title", "Subtitle") {
                Row(modifier = Modifier.fillMaxWidth()) { }
            },
        )
    ).Content()
}