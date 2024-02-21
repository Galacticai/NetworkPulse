package com.galacticai.networkpulse.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.atStartOfDayMS
import com.galacticai.networkpulse.common.atStartOfHourMS
import com.galacticai.networkpulse.common.models.bit_value.BitUnit
import com.galacticai.networkpulse.common.models.bit_value.BitUnitBase
import com.galacticai.networkpulse.common.models.bit_value.BitUnitExponent
import com.galacticai.networkpulse.common.models.bit_value.BitValue
import com.galacticai.networkpulse.common.ui.graphing.bar_chart.BarData
import com.galacticai.networkpulse.databse.models.SpeedRecord
import com.galacticai.networkpulse.ui.common.durationSuffixes
import com.galacticai.networkpulse.ui.common.localized
import com.galacticai.networkpulse.ui.common.localizedDot
import com.galacticai.networkpulse.ui.common.records_view.record_range.RecordRangeView
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun RecordRangeList(
    modifier: Modifier = Modifier,
    records: List<SpeedRecord>,
    reversed: Boolean = false,
    onRecordDeleted: ((SpeedRecord) -> Unit)? = null,
) {
    Surface(
        color = colorResource(R.color.surface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(2.dp, colorResource(R.color.surface)),
        modifier = Modifier
            .graphicsLayer(clip = true)
            .then(modifier)
    ) {
        val zoneId = ZoneId.systemDefault()
        val recordsInOrder = if (reversed) records.reversed() else records

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            val groupedByDay = recordsInOrder.groupBy { record -> record.time.atStartOfDayMS(zoneId) }
            var addSpace = false
            groupedByDay.forEach { (day, dayRecords) ->
                if (addSpace)
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                val groupedByHour = dayRecords.groupBy { record -> record.time.atStartOfHourMS(zoneId) }
                stickyHeader { DayHeader(day, groupedByHour) }

                groupedByHour.forEach { (hour, hourRecords) ->
                    //stickyHeader { HourHeader(hour, hourRecords) }
                    item {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(2.dp)
                                .height(40.dp),
                        ) {
                            RecordRangeView(
                                modifier = Modifier.padding(horizontal = 2.dp),
                                records = hourRecords.reversed(),
                                onRecordDeleted = onRecordDeleted,
                            )
                            Surface(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp)
                                    .height(40.dp)
                                    .pointerInteropFilter { false },
                                color = colorResource(R.color.surface).copy(.85f),
                                contentColor = colorResource(R.color.primary),
                                shape = RoundedCornerShape(20.dp),
                                shadowElevation = 10.dp,
                            ) { HourHeader(hour, hourRecords) }
                        }
                    }
                }
                if (!addSpace) addSpace = true
            }
        }
    }
}


@Composable
fun DayHeader(dayMS: Long, groupedByHour: Map<Long, List<SpeedRecord>>) {
    var showSummary by remember { mutableStateOf(true) }

    Surface(
        color = colorResource(R.color.secondaryContainer),
        border = BorderStroke(1.dp, colorResource(R.color.onSecondaryContainer)),
        contentColor = colorResource(R.color.onSecondaryContainer),
        shape = RoundedCornerShape(20.dp),
        onClick = { showSummary = !showSummary },
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .heightIn(min = 40.dp)
    ) {
        AnimatedContent(
            targetState = showSummary,
            label = "DayHeaderAnimation",
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = SimpleDateFormat(
                            "EEEE dd/MM/yyyy", Locale.getDefault()
                        ).format(dayMS),
                        modifier = Modifier.weight(1f),
                        fontSize = 12.sp,
                        letterSpacing = .2f.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = if (it) FontWeight.W100 else FontWeight.Bold,
                    )
                    Icon(
                        modifier = Modifier
                            .size(18.dp)
                            .padding(horizontal = 10.dp),
                        imageVector = if (it) Icons.Rounded.KeyboardArrowUp
                        else Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                    )
                }
                if (it) {
                    val hourlyAverage = groupedByHour.map { (_, hourRecords) ->
                        SpeedRecord.average(hourRecords)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    RecordRangeView(
                        records = hourlyAverage.reversed(),
                        hourly = false,
                        parser = { record ->
                            val label = SimpleDateFormat("h a", Locale.getDefault())
                                .format(record.time) + " - ${record.runtimeMS}ms"
                            BarData(label, record.down ?: 0f)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HourHeader(hourMS: Long, records: List<SpeedRecord>) {
    val hourText = SimpleDateFormat("h a", Locale.getDefault())
        .format(hourMS)

    val maxValue = records.maxBy { it.down ?: 0f }.down
    val max = if (maxValue == null) null
    else BitValue(maxValue, BitUnit(BitUnitExponent.Metric.Kilo, BitUnitBase.Byte))
        .toNearestUnit()


    Row(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = hourText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))

        if (max != null) {
            Text(
                text = stringResource(R.string.maximum) +
                        ": " +
                        max.value.localizedDot(2) +
                        ' ' + max.unit.name.short +
                        '/' + durationSuffixes(LocalContext.current).seconds,
                color = colorResource(R.color.primary),
                fontWeight = FontWeight.Light,
                fontSize = 12.sp,
                letterSpacing = .2f.sp,
            )
            Text(
                " — ",
                fontWeight = FontWeight.Light,
                fontSize = 12.sp,
                color = colorResource(R.color.primary).copy(.5f),
            )
        }
        Text(
            "${records.size.localized()} ${stringResource(R.string.records)}",
            fontSize = 12.sp,
        )
    }
}
