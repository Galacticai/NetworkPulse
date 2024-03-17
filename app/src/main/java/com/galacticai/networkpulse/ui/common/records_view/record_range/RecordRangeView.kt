package com.galacticai.networkpulse.ui.common.records_view.record_range

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.galacticai.networkpulse.common.ui.graphing.bar_chart.BarData
import com.galacticai.networkpulse.databse.models.SpeedRecord
import com.galacticai.networkpulse.databse.models.SpeedRecordUtils.sorted
import com.galacticai.networkpulse.ui.common.records_view.record_range.RecordRangeType.Companion.toColorChartData
import com.galacticai.networkpulse.util.Consistent
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Locale
import java.util.SortedSet

/** Type of record range (Type of pre-processing) */
enum class RecordRangeType {
    /** Take input as is (all values without averaging anything) */
    All,

    /** Take 1st hour and average each minute */
    Hour,

    /** Take 1st day and average each hour */
    Day;

    companion object {
        //? might be better to move somewhere else but it's here for now
        fun SortedSet<SpeedRecord>.toColorChartData(rangeType: RecordRangeType, zoneId: ZoneId) =
            when (rangeType) {
                All -> this.map { it.down }
                Day -> this.toDayColorChartData(zoneId)
                Hour -> this.toHourColorChartData(zoneId)
            }
    }
}

@Composable
fun RecordRangeView(
    records: List<SpeedRecord>,
    modifier: Modifier = Modifier,
    rangeType: RecordRangeType = RecordRangeType.All,
    colorChartOverlay: (@Composable BoxScope.() -> Unit)? = null,
    onRecordDeleted: ((SpeedRecord) -> Unit)? = null,
    parser: ((SpeedRecord) -> BarData)? = null,
    colorMaxValue: Float? = null,
) {
    var showMore by remember { mutableStateOf(false) }

    val zoneId = ZoneId.systemDefault() //? much better for performance
    val sorted = records.sorted()
    val chartData = sorted.toColorChartData(rangeType, zoneId)

    if (showMore) {
        ModalRecordRange(records, chartData, onRecordDeleted, parser) {
            showMore = false
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        shape = Consistent.shape,
        onClick = { showMore = !showMore },
    ) {
        SimpleColorChart(
            data = chartData,
            maxValuePreferred = colorMaxValue,
            overlay = colorChartOverlay,
        )
    }
}


@Composable
fun RowScope.Filler() = Spacer(Modifier.weight(1f))

private fun formatDateTime(stamp: Long, pattern: String): String =
    SimpleDateFormat(pattern, Locale.getDefault()).format(stamp)

fun formatDate(stamp: Long): String = formatDateTime(stamp, "dd/MM/yyyy\nEEEE")
fun formatTime(stamp: Long): String = formatDateTime(stamp, "h:mm:ss\na")

