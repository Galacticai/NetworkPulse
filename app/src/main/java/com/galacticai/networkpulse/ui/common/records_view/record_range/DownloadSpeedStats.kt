package com.galacticai.networkpulse.ui.common.records_view.record_range

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.models.bit_value.BitUnit
import com.galacticai.networkpulse.common.models.bit_value.BitUnitBase
import com.galacticai.networkpulse.common.models.bit_value.BitUnitExponent
import com.galacticai.networkpulse.common.models.bit_value.BitValue
import com.galacticai.networkpulse.models.records_summary.RecordsSummary

@Composable
fun DownloadSpeedStats(summary: RecordsSummary, formatter: (BitValue) -> String) {
    StatContainer(
        stringResource(R.string.download_speed),
        listOf({
            val unit = BitUnit(BitUnitExponent.Metric.Kilo, BitUnitBase.Byte)
            val min = BitValue(summary.down.min.value, unit).toNearestUnit()
            val average = BitValue(summary.down.average, unit).toNearestUnit()
            val max = BitValue(summary.down.max.value, unit).toNearestUnit()

            Stat(stringResource(R.string.minimum), formatter(min))
            Stat(
                stringResource(R.string.average),
                formatter(average),
                colorResource(R.color.primary)
            )
            Stat(stringResource(R.string.maximum), formatter(max))
        }, {
            val minDate = formatDate(summary.down.min.time)
            val maxDate = formatDate(summary.down.max.time)
            Stat(null, minDate)
            Filler()
            Stat(null, maxDate)
        }, {
            val minTime = formatTime(summary.down.min.time)
            val maxTime = formatTime(summary.down.max.time)
            Stat(null, minTime)
            Filler()
            Stat(null, maxTime)
        })
    )
}
