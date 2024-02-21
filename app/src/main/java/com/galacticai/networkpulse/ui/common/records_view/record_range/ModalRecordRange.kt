package com.galacticai.networkpulse.ui.common.records_view.record_range

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.format
import com.galacticai.networkpulse.common.models.bit_value.BitValue
import com.galacticai.networkpulse.common.ui.graphing.bar_chart.BarData
import com.galacticai.networkpulse.databse.models.SpeedRecord
import com.galacticai.networkpulse.models.records_summary.RecordsSummary
import com.galacticai.networkpulse.ui.common.durationSuffixes
import com.galacticai.networkpulse.ui.common.localized
import com.galacticai.networkpulse.ui.common.localizedDot
import com.galacticai.networkpulse.ui.common.records_view.RecordRangeChart
import kotlin.time.Duration


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalRecordRange(
    records: List<SpeedRecord>,
    onRecordDeleted: ((SpeedRecord) -> Unit)? = null,
    parser: ((SpeedRecord) -> BarData)? = null,
    onDismissRequest: () -> Unit,
) {
    val durationSuffixes = durationSuffixes(LocalContext.current)
    val durationSeparator = "${stringResource(R.string.comma)} "

    fun durationFormat(duration: Duration) =
        duration.format(durationSuffixes, durationSeparator, 2)
        { it.localized() }

    fun formatSpeed(speed: BitValue) =
        "${speed.value.localizedDot(2)}\n${speed.unit.name.short}/${durationSuffixes.seconds}"

    val summary = RecordsSummary.ofRecords(records.toSortedSet())
    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        @Composable
        fun space() = Spacer(modifier = Modifier.height(10.dp))
        fun LazyListScope.ispace() = item { space() }

        RecordRangeChart(
            records = records,
            onRecordDeleted = onRecordDeleted,
            parser = parser,
        )
        space()
        LazyColumn(
            modifier = Modifier
                .heightIn(max = 600.dp)
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { CountStats(summary) }
            ispace()
            //item { UploadSpeedStats(summary, ::formatSpeed) }
            //space()
            item { DownloadSpeedStats(summary, ::formatSpeed) }
            ispace()
            item { UpDownloadTotalStats(summary) }
            ispace()
            item { RuntimeMSStats(summary, ::durationFormat) }
            ispace()
            item { LongestSuccessStats(summary) }
            ispace()
            item { LongestFailStats(summary) }
        }
        TextButton(onClick = onDismissRequest) {
            Text(stringResource(R.string.ok))
        }
    }
}