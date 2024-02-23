package com.galacticai.networkpulse.ui.common.records_view.record_range

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.atStartOfDayMS
import com.galacticai.networkpulse.common.atStartOfHourMS
import com.galacticai.networkpulse.common.getHour
import com.galacticai.networkpulse.common.getMinute
import com.galacticai.networkpulse.databse.models.SpeedRecord
import com.galacticai.networkpulse.databse.models.SpeedRecordUtils
import com.galacticai.networkpulse.databse.models.SpeedRecordUtils.sorted
import java.time.ZoneId
import java.util.SortedSet

typealias ColorChartData = List<Float?>

@Composable
fun SimpleColorChart(
    data: ColorChartData,
    modifier: Modifier = Modifier,
    height: Dp = 40.dp,
    maxValuePreferred: Float? = null,
    overlay: (@Composable BoxScope.() -> Unit)? = null,
) {
    assert(maxValuePreferred == null || maxValuePreferred > 0f)
    val maxValue = maxValuePreferred
        ?: data
            .maxOfOrNull { it ?: 0f } //? max of data
            .takeIf { it?.let { it >= 0f } == true } //? only if >= 0
        ?: 0f //? 0 if null

    val color = colorResource(R.color.primary)
    val colorNone = colorResource(R.color.warningContainer)
    val colorFail = colorResource(R.color.error).copy(.5f)

    val isLTR = LocalLayoutDirection.current == LayoutDirection.Ltr

    Box(
        modifier = modifier
            .fillMaxSize()
            .height(height)
            .then(modifier)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val barWidth = size.width / data.size

            data.forEachIndexed { i, value ->
                val startX =
                    if (isLTR) (barWidth * i)
                    else (size.width - (barWidth * (i + 1)))
                val endX = startX + barWidth
                val barRect = Rect(
                    left = startX,
                    top = 0f,
                    right = endX,
                    bottom = size.height
                )
                drawRect(
                    color =
                    if (value == null) colorNone
                    else if (value <= 0f) colorFail
                    else color.copy(.1f + (value / maxValue) * .9f),
                    topLeft = barRect.topLeft,
                    size = barRect.size
                )
            }
        }
        if (overlay != null) overlay()
    }
}

@Composable
fun ColorChartOverlayText(
    text: String,
    modifier: Modifier = Modifier,
    elevation: Dp = 0.dp
) {
    val secondaryContainer = colorResource(R.color.secondaryContainer)
    Surface(
        modifier = modifier,
        color = if (elevation > 0.dp) secondaryContainer
        else secondaryContainer.copy(.8f),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = elevation
    ) {
        Text(
            text, Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
            color = colorResource(R.color.onSecondaryContainer),
            fontSize = 12.sp
        )
    }
}

//? These can be made as 1 function but im currently too lazy

fun SortedSet<SpeedRecord>.toColorChartDataPerMinute(
    zoneId: ZoneId = ZoneId.systemDefault()
): ColorChartData {
    val map = mutableMapOf<Int, MutableList<Float>>()
    for (record in this) {
        val minute = record.time.getMinute(zoneId)
        map.getOrPut(minute) { mutableListOf() }
            .add(record.down ?: 0f)
    }
    return map.map { (_, values) ->
        if (values.isEmpty()) null
        else values.sum() / values.size
    }
}

fun SortedSet<SpeedRecord>.toHourColorChartData(
    zoneId: ZoneId = ZoneId.systemDefault()
): ColorChartData {
    val range = 1 until 60
    val hour = firstOrNull()?.time?.atStartOfHourMS(zoneId)
        ?: return range.map { null }
    val map = mutableMapOf<Int, MutableList<Float>>()
    for (record in this) {
        val hourR = record.time.atStartOfHourMS(zoneId)
        if (hourR != hour) break
        val minute = record.time.getMinute(zoneId)
        map.getOrPut(minute) { mutableListOf() }
            .add(record.down ?: 0f)
    }
    return range.map {
        val values = map[it]
        if (values.isNullOrEmpty()) null
        else values.sum() / values.size
    }
}


fun SortedSet<SpeedRecord>.toDayColorChartData(
    zoneId: ZoneId = ZoneId.systemDefault()
): ColorChartData {
    val range = 1 until 24
    val day = firstOrNull()?.time?.atStartOfDayMS(zoneId)
        ?: return range.map { null }
    val map = mutableMapOf<Int, MutableList<Float>>()
    for (record in this) {
        val dayR = record.time.atStartOfDayMS(zoneId)
        if (dayR != day) break
        val hour = record.time.getHour(zoneId)
        map.getOrPut(hour) { mutableListOf() }
            .add(record.down ?: 0f)
    }
    return range.map {
        val values = map[it]
        if (values.isNullOrEmpty()) null
        else values.sum() / values.size
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSimpleColorChart() {
    SimpleColorChart(
        data = listOf(
            SpeedRecordUtils.success(1000 * 60, 1000, 0f, 60f),
            SpeedRecordUtils.success(2 * 60 * 1000, 1000, 0f, 550f),
            SpeedRecordUtils.success(3 * 60 * 1000, 1000, 0f, 450f),
            SpeedRecordUtils.success(4 * 60 * 1000, 1000, 0f, 550f),
            SpeedRecordUtils.success(7 * 60 * 1000, 1000, 0f, 40f),
            SpeedRecordUtils.success(8 * 60 * 1000, 1000, 0f, 50f),
            SpeedRecordUtils.success(10 * 60 * 1000, 1000, 0f, 50f),
            SpeedRecordUtils.success(50 * 60 * 1000, 1000, 0f, 70f),
            SpeedRecordUtils.success(17 * 60 * 1000, 1000, 0f, 50f),
            SpeedRecordUtils.success(2 * 60 * 1000, 1000, 0f, 50f),
            SpeedRecordUtils.success(3 * 60 * 1000, 1000, 0f, 50f),
            SpeedRecordUtils.success(16 * 60 * 1000, 1000, 0f, 50f),
            SpeedRecordUtils.success(7 * 60 * 1000, 1000, 0f, 50f),
            SpeedRecordUtils.success(40 * 60 * 1000, 1000, 0f, 50f),
            SpeedRecordUtils.success(45 * 60 * 1000, 1000, 0f, 50f),
            SpeedRecordUtils.success(55 * 60 * 1000, 1000, 0f, 550f),
            SpeedRecordUtils.success(59 * 60 * 1000, 1000, 0f, 0f),
            SpeedRecordUtils.success(41 * 60 * 1000, 1000, 0f, 0f),
            SpeedRecordUtils.success(46 * 60 * 1000, 1000, 0f, 0f),
            SpeedRecordUtils.success(56 * 60 * 1000, 1000, 0f, 0f),
            SpeedRecordUtils.success(58 * 60 * 1000, 1000, 0f, 0f),
        ).sorted().toHourColorChartData(),
    )
}