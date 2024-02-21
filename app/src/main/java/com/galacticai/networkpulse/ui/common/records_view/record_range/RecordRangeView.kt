package com.galacticai.networkpulse.ui.common.records_view.record_range

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.galacticai.networkpulse.common.ui.graphing.bar_chart.BarData
import com.galacticai.networkpulse.databse.models.SpeedRecord
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Locale

@Composable
fun RecordRangeView(
    records: List<SpeedRecord>,
    modifier: Modifier = Modifier,
    hourly: Boolean = true,
    onRecordDeleted: ((SpeedRecord) -> Unit)? = null,
    parser: ((SpeedRecord) -> BarData)? = null,
) {
    var showMore by remember { mutableStateOf(false) }

    if (showMore) {
        ModalRecordRange(records, onRecordDeleted, parser) {
            showMore = false
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        shape = RoundedCornerShape(20.dp),
        onClick = { showMore = !showMore },
    ) {
        val zoneId = ZoneId.systemDefault() //? avoid recreating it on every call inside the function
        val sorted = records.toSortedSet()
        val chartData =
            if (hourly) sorted.toHourColorChartData(zoneId)
            else sorted.toDayColorChartData(zoneId)
        SimpleColorChart(chartData)
    }
}


@Composable
fun RowScope.Filler() = Spacer(Modifier.weight(1f))

private fun formatDateTime(stamp: Long, pattern: String): String =
    SimpleDateFormat(pattern, Locale.getDefault()).format(stamp)

fun formatDate(stamp: Long): String = formatDateTime(stamp, "dd/MM/yyyy\nEEEE")
fun formatTime(stamp: Long): String = formatDateTime(stamp, "h:mm:ss\na")

