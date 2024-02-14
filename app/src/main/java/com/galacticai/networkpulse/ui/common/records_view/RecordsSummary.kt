package com.galacticai.networkpulse.ui.common.records_view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.format
import com.galacticai.networkpulse.common.models.data_value.BitUnit
import com.galacticai.networkpulse.common.models.data_value.BitUnitBase
import com.galacticai.networkpulse.common.models.data_value.BitUnitExponent
import com.galacticai.networkpulse.common.models.data_value.BitValue
import com.galacticai.networkpulse.databse.models.SpeedRecord
import com.galacticai.networkpulse.models.SpeedRecordSummary
import com.galacticai.networkpulse.ui.common.durationSuffixes
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun RecordsSummary(records: List<SpeedRecord>) {
    val summary = SpeedRecordSummary.summarize(records)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        @Composable
        fun statRow(content: @Composable () -> Unit) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) { content() }
        }

        @Composable
        fun stat(text: String, value: Any, color: Color) {
            Surface(
                modifier = Modifier.padding(2.dp),
                shape = RoundedCornerShape(10.dp),
            ) {
                Column(
                    modifier = Modifier.padding(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text, fontSize = 10.sp)
                    Text(value.toString(), color = color)
                }
            }
        }
        //        Text(
        //            stringResource(R.string.summary),
        //            color = colorResource(R.color.primary),
        //            fontSize = 18.sp,
        //            fontWeight = FontWeight.SemiBold
        //        )
        //        Spacer(modifier = Modifier.height(10.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            statRow {
                stat(
                    stringResource(R.string.total),
                    summary.allCount,
                    colorResource(R.color.primary)
                )
                stat(
                    stringResource(R.string.success),
                    summary.successCount,
                    colorResource(R.color.success)
                )
                stat(
                    stringResource(R.string.fail),
                    summary.failedCount,
                    colorResource(R.color.error)
                )
            }
            val durationSuffixes = durationSuffixes(LocalContext.current)
            val separator = "${stringResource(R.string.comma)} "
            fun durationFormat(duration: Duration) =
                duration.format(durationSuffixes, separator, 2)
            statRow {
                val down = BitValue(
                    summary.downAverage,
                    BitUnit(BitUnitExponent.Binary.Kibi, BitUnitBase.Byte)
                ).toNearestUnit()
                stat(
                    stringResource(R.string.average_download),
                    "$down/s",
                    colorResource(R.color.primary)
                )
                //stat("Average upload", summary.downAverage, colorResource(R.color.primary))
                val runtimeMSSuccessAverage = summary.runtimeMSSuccessAverage.milliseconds
                stat(
                    stringResource(R.string.average_time),
                    durationFormat(runtimeMSSuccessAverage),
                    colorResource(R.color.success)
                )
            }
            statRow {
                val longestSuccessStreak = summary.longestSuccessStreakMS.milliseconds
                val longestFailStreak = summary.longestFailStreakMS.milliseconds
                stat(
                    stringResource(R.string.longest_success_streak),
                    durationFormat(longestSuccessStreak),
                    colorResource(R.color.success)
                )
                stat(
                    stringResource(R.string.longest_failure_streak),
                    durationFormat(longestFailStreak),
                    colorResource(R.color.error)
                )
            }
        }
    }
}
