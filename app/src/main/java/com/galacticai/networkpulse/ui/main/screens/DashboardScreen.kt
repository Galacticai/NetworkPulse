package com.galacticai.networkpulse.ui.main.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.atStartOfDayMS
import com.galacticai.networkpulse.databse.models.SpeedRecord
import com.galacticai.networkpulse.ui.MainActivity
import com.galacticai.networkpulse.ui.common.TopBar
import com.galacticai.networkpulse.ui.common.records_view.RecordsPager
import com.guru.fontawesomecomposelib.FaIcon
import com.guru.fontawesomecomposelib.FaIcons

@Composable
fun DashboardScreen() {
    val context = LocalContext.current as MainActivity
    var records by rememberSaveable { mutableStateOf<List<SpeedRecord>>(emptyList()) }
    fun reloadRecords() {
        records = context.viewModel.dao.getAll()
    }
    LaunchedEffect(Unit) { reloadRecords() }

    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth(),
    ) {
        TopBar(stringResource(R.string.dashboard)) {
            IconButton(onClick = { reloadRecords() }) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = stringResource(R.string.refresh)
                )
            }
        }
        AnimatedContent(
            modifier = Modifier.weight(1f),
            targetState = records.isEmpty(),
            label = "DashboardPagerAnimation"
        ) {
            if (it) EmptyPagerMessage()
            else RecordsPager(
                recordsByDay = records.groupBy { r -> r.time.atStartOfDayMS() },
                onRecordDeleted = { reloadRecords() }
            )
        }
    }
}

@Composable
private fun EmptyPagerMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FaIcon(
            FaIcons.Ghost,
            size = 64.dp,
            tint = colorResource(R.color.surface),
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.no_records),
            fontSize = 18.sp,
            color = colorResource(R.color.primary)
        )
    }
}

