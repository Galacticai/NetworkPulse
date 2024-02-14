package com.galacticai.networkpulse.ui.main.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.ui.graphing.bar_chart.BarData
import com.galacticai.networkpulse.databse.models.SpeedRecord
import com.galacticai.networkpulse.ui.MainActivity
import com.galacticai.networkpulse.ui.common.AppTitle
import com.galacticai.networkpulse.ui.common.records_view.RecordsChart
import com.galacticai.networkpulse.ui.common.records_view.RecordsSummary
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun OverviewScreen() {
    val mainActivity = LocalContext.current as MainActivity
    var recentRecords by remember { mutableStateOf<List<SpeedRecord>>(emptyList()) }
    LaunchedEffect(mainActivity.viewModel.recentRecords) {
        mainActivity.viewModel.recentRecords.observe(mainActivity) {
            recentRecords = it
        }
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
        AnimatedVisibility(visible = recentRecords.isNotEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = colorResource(R.color.background),
                border = BorderStroke(2.dp, colorResource(R.color.primaryContainer)),
                shape = RoundedCornerShape(20.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                ) {
                    RecordsChart(
                        records = recentRecords,
                        parser = {
                            val timestamp = SimpleDateFormat("h:mm:ss a", Locale.getDefault())
                                .format(it.time)
                            val label =
                                "$timestamp (${it.runtimeMS}${mainActivity.getString(R.string.millisecond_suffix)})"
                            val value = it.down ?: 0f
                            BarData(label, value)
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    RecordsSummary(records = recentRecords)
                }
            }
        }
    }
}
