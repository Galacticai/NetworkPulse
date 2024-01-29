package com.galacticai.networkpulse.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun TopBar(title: String, buttons: (@Composable () -> Unit)? = null) {
    Row(
        modifier = Modifier.padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ScreenTitle(text = title)
        Spacer(modifier = Modifier.weight(1f))
        buttons?.invoke()
    }
}