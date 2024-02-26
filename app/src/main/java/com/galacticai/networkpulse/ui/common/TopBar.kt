package com.galacticai.networkpulse.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.galacticai.networkpulse.R

@Composable
fun TopBar(title: String, buttons: List<@Composable RowScope.() -> Unit>? = null) {
    Row(
        modifier = Modifier.padding(10.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ScreenTitle(title)

        if (buttons == null) return
        Spacer(Modifier.weight(1f))
        buttons.forEach { it() }
    }
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    TopBar(title = stringResource(R.string.overview), listOf {
        IconButton(onClick = {}) { Icon(Icons.Rounded.Refresh, null) }
    })
}