package com.galacticai.networkpulse.ui.common.records_view.record_range

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.models.records_summary.RecordsSummary
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun RuntimeMSStats(summary: RecordsSummary, formatter: (Duration) -> String) {
    StatContainer(
        stringResource(R.string.duration),
        listOf({
            val min = summary.runtimeMSSuccess.min.value.milliseconds
            val average = summary.runtimeMSSuccess.average.milliseconds
            val max = summary.runtimeMSSuccess.max.value.milliseconds
            Stat(stringResource(R.string.minimum), formatter(min))
            Stat(
                stringResource(R.string.average),
                formatter(average),
                colorResource(R.color.primary)
            )
            Stat(stringResource(R.string.maximum), formatter(max))
        }, {
            val minDate = formatDate(summary.runtimeMSSuccess.min.time)
            val maxDate = formatDate(summary.runtimeMSSuccess.max.time)
            Stat(null, minDate)
            Filler()
            Stat(null, maxDate)
        }, {
            val minTime = formatTime(summary.runtimeMSSuccess.min.time)
            val maxTime = formatTime(summary.runtimeMSSuccess.max.time)
            Stat(null, minTime)
            Filler()
            Stat(null, maxTime)
        })
    )
}


