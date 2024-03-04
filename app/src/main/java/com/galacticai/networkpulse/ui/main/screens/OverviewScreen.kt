package com.galacticai.networkpulse.ui.main.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.galacticai.networkpulse.common.fromUTC
import com.galacticai.networkpulse.common.ui.graphing.bar_chart.BarData
import com.galacticai.networkpulse.databse.models.SpeedRecordUtils.sorted
import com.galacticai.networkpulse.ui.MainActivity
import com.galacticai.networkpulse.ui.common.AppTitle
import com.galacticai.networkpulse.ui.common.NoRecordsMessage
import com.galacticai.networkpulse.ui.common.records_view.record_range.RecordRangeDetailsView
import com.galacticai.networkpulse.ui.common.records_view.record_range.toColorChartDataPerMinute
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun OverviewScreen() {
    val activity = LocalContext.current as MainActivity
    val recentRecords by activity.viewModel.repo.recentRecordsLive.observeAsState(emptyList())

    Column(
        modifier = Modifier.padding(
            horizontal = 10.dp
        )
    ) {
        AppTitle(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 10.dp, bottom = 20.dp,
                    start = 10.dp, end = 10.dp,
                )
        )
        AnimatedContent(
            targetState = recentRecords.isNullOrEmpty(),
            label = "OverviewScreenAnimation"
        ) { isEmpty ->
            if (isEmpty) {
                NoRecordsMessage()
                return@AnimatedContent
            }

            RecordRangeDetailsView(
                records = recentRecords,
                recordsSimplified = recentRecords.sorted().toColorChartDataPerMinute(),
                onRecordDeleted = {
                    val list = activity.viewModel.recentRecords.value
                        .orEmpty().toMutableList()
                    list.remove(it)
                    activity.viewModel.recentRecords.value = list
                },
            ) {
                val timestamp = SimpleDateFormat("h:mm:ss a", Locale.getDefault())
                    .format(it.time.fromUTC())
                val value = it.down ?: 0f
                BarData(timestamp, value)
            }
        }
    }
}
