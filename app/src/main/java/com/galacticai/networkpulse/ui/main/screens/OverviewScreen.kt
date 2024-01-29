package com.galacticai.networkpulse.ui.main.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.galacticai.networkpulse.ui.MainActivity
import com.galacticai.networkpulse.ui.common.AppTitle
import com.galacticai.networkpulse.ui.main.HourGraph

@Composable
fun OverviewScreen(mainActivity: MainActivity) {
    val timeAgo by remember {
        mutableStateOf(MainActivity.formatTimeAgo(MainActivity.chartAfterTime))
    }
    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
        AppTitle(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 10.dp, bottom = 20.dp,
                    start = 10.dp, end = 10.dp,
                )
        )
        HourGraph(data = mainActivity.chartData)
        Spacer(modifier = Modifier.padding(5.dp))
        Text("Records in the last ${timeAgo}: (${mainActivity.chartValues.size})")
        Spacer(modifier = Modifier.padding(5.dp))
        Row {
            Button(onClick = {

            }) {
                Text("Export CSV")
            }
            Button(onClick = {

            }) {
                Text("Clear database")
            }
        }
    }
}