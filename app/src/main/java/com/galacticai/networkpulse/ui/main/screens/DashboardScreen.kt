package com.galacticai.networkpulse.ui.main.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.ui.MainActivity
import com.galacticai.networkpulse.ui.common.TopBar
import com.galacticai.networkpulse.ui.main.RecordsList

@Composable
fun DashboardScreen(mainActivity: MainActivity) {
    val records = mainActivity.getAllValuesFromDB().reversed()
    var all by remember { mutableStateOf(records) }

    Column {
        TopBar(stringResource(R.string.dashboard)) {
            IconButton(onClick = { all = mainActivity.getAllValuesFromDB().reversed() }) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = stringResource(R.string.refresh)
                )
            }
        }
        RecordsList(
            modifier = Modifier.padding(horizontal = 10.dp),
            records = all
        )
    }
}
