package com.galacticai.networkpulse.ui.common.records_view.record_range

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.format
import com.galacticai.networkpulse.common.models.bit_value.BitValue
import com.galacticai.networkpulse.common.ui.graphing.bar_chart.BarData
import com.galacticai.networkpulse.databse.models.SpeedRecord
import com.galacticai.networkpulse.databse.models.SpeedRecordUtils.sorted
import com.galacticai.networkpulse.models.records_summary.RecordsSummary
import com.galacticai.networkpulse.ui.common.records_view.RecordRangeChart
import com.galacticai.networkpulse.util.Consistent
import com.galacticai.networkpulse.util.durationSuffixes
import com.galacticai.networkpulse.util.localized
import com.galacticai.networkpulse.util.localizedDot
import java.util.SortedSet
import kotlin.time.Duration

@Composable
fun RecordRangeDetailsView(
    records: SortedSet<SpeedRecord>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 10.dp),
    recordsSimplified: ColorChartData? = null,
    onRecordDeleted: ((SpeedRecord) -> Unit)? = null,
    parser: ((SpeedRecord) -> BarData)? = null,
) {
    val durationSuffixes = durationSuffixes(LocalContext.current)
    val durationSeparator = "${stringResource(R.string.comma)} "

    fun durationFormat(duration: Duration) =
        duration.format(durationSuffixes, durationSeparator, 2)
        { it.localized() }

    fun formatSpeed(speed: BitValue) =
        "${speed.value.localizedDot(2)}\n${speed.unit.name.short}/${durationSuffixes.seconds}"

    val summary = RecordsSummary.ofRecords(records)

    Column(
        Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        val surface = colorResource(R.color.surface)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(2.dp, surface),
            shape = Consistent.shape,
            shadowElevation = 5.dp,
        ) {
            RecordRangeChart(
                records = records,
                onRecordDeleted = onRecordDeleted,
                parser = parser,
            )
        }

        if (recordsSimplified != null) {
            Spacer(Modifier.height(5.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                border = BorderStroke(2.dp, surface),
                shape = Consistent.shape,
                shadowElevation = 5.dp,
            ) {
                SimpleColorChart(recordsSimplified)
            }
        }

        Spacer(Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item { CountStats(summary) }

            //if (summary.successCount > 0) {
            //item { UploadSpeedStats(summary, ::formatSpeed) }
            if (summary.successCount > 0) {
                item { DownloadSpeedStats(summary, ::formatSpeed) }
                item { UpDownloadTotalStats(summary) }
            }
            if (summary.allCount > 0)
                item { RuntimeMSStats(summary, ::durationFormat) }

            if (summary.longestSuccessStreak.value > 0)
                item { LongestSuccessStats(summary) }

            if (summary.longestFailStreak.value > 0)
                item { LongestFailStats(summary) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalRecordRange(
    records: List<SpeedRecord>,
    recordsSimplified: ColorChartData? = null,
    onRecordDeleted: ((SpeedRecord) -> Unit)? = null,
    parser: ((SpeedRecord) -> BarData)? = null,
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        shape = Consistent.shape,
        dragHandle = {},
    ) {
        RecordRangeDetailsView(
            records = records.sorted(),
            contentPadding = PaddingValues(horizontal = 10.dp),
            recordsSimplified = recordsSimplified,
            onRecordDeleted = onRecordDeleted,
            parser = parser,
        )

        TextButton(
            onClick = onDismissRequest,
            Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .align(Alignment.End),
        ) {
            Text(stringResource(R.string.ok))
        }
    }
}