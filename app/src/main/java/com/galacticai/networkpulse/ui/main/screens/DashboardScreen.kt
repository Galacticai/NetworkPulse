package com.galacticai.networkpulse.ui.main.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.ui.MainActivity
import com.galacticai.networkpulse.ui.common.ScreenTitle
import com.galacticai.networkpulse.ui.main.RecordsList

@Composable
fun DashboardScreen(mainActivity: MainActivity) {
    fun getAll() = mainActivity.getAllValuesFromDB().reversed()
    var all by remember { mutableStateOf(getAll()) }

    Scaffold(topBar = {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ScreenTitle(stringResource(R.string.dashboard))
            IconButton(onClick = { all = getAll() }) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = stringResource(R.string.refresh)
                )
            }
        }
    }) { scaffoldPadding ->
        RecordsList(
            modifier = Modifier
                .padding(scaffoldPadding)
                .padding(10.dp),
            records = all
        )
    }
}