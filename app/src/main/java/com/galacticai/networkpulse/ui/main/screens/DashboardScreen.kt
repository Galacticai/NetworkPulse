package com.galacticai.networkpulse.ui.main.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.ui.activities.MainActivity
import com.galacticai.networkpulse.ui.common.TopBar
import com.galacticai.networkpulse.ui.common.records_view.RecordsPager
import com.guru.fontawesomecomposelib.FaIcon
import com.guru.fontawesomecomposelib.FaIcons

@Composable
fun DashboardScreen() {
    val activity = LocalContext.current as MainActivity
    val recordsCount by activity.repo.countAllLive.observeAsState(0)

    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth(),
    ) {
        TopBar(
            stringResource(R.string.dashboard),
            // listOf {
            //     IconButton(onClick = { reloadRecords() }) {
            //         Icon(
            //             imageVector = Icons.Rounded.Refresh,
            //             contentDescription = stringResource(R.string.refresh)
            //         )
            //     }
            // }
        )
        AnimatedContent(
            modifier = Modifier.weight(1f),
            targetState = recordsCount <= 0,
            label = "DashboardPagerAnimation"
        ) {
            if (it) EmptyPagerMessage()
            else RecordsPager()
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

