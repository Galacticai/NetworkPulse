package com.galacticai.networkpulse.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.atStartOfDayMS
import com.galacticai.networkpulse.common.atStartOfHourMS
import com.galacticai.networkpulse.common.atStartOfMinuteMS
import com.galacticai.networkpulse.common.fromUTC
import com.galacticai.networkpulse.common.models.bit_value.BitUnit
import com.galacticai.networkpulse.common.models.bit_value.BitUnitBase
import com.galacticai.networkpulse.common.models.bit_value.BitUnitExponent
import com.galacticai.networkpulse.common.models.bit_value.BitValue
import com.galacticai.networkpulse.common.reduceBetter
import com.galacticai.networkpulse.common.ui.graphing.bar_chart.BarData
import com.galacticai.networkpulse.databse.models.SpeedRecord
import com.galacticai.networkpulse.databse.models.SpeedRecordUtils.average
import com.galacticai.networkpulse.databse.models.SpeedRecordUtils.downMax
import com.galacticai.networkpulse.ui.common.records_view.record_range.ColorChartOverlayText
import com.galacticai.networkpulse.ui.common.records_view.record_range.Filler
import com.galacticai.networkpulse.ui.common.records_view.record_range.RecordRangeType
import com.galacticai.networkpulse.ui.common.records_view.record_range.RecordRangeView
import com.galacticai.networkpulse.util.Consistent
import com.galacticai.networkpulse.util.durationSuffixes
import com.galacticai.networkpulse.util.localized
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun RecordRangeList(
    modifier: Modifier = Modifier,
    records: List<SpeedRecord>,
    onRecordDeleted: ((SpeedRecord) -> Unit)? = null,
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()

    val zoneId = ZoneId.systemDefault()
    val locale = Locale.getDefault()

    val n0 = 0.localized()
    val n00 = "$n0$n0"
    val n59 = 59.localized()
    val hFormatter = SimpleDateFormat("h", locale)
    val aFormatter = SimpleDateFormat("a", locale)

    val legendPadding = 7.dp
    var legendHeight by rememberSaveable { mutableIntStateOf(0) }

    val groupedByDay by remember(records) {
        derivedStateOf { records.groupBy { r -> r.time.atStartOfDayMS(zoneId) } }
    }

    //? For legend: (absolute max)
    val max by remember(records) { derivedStateOf { records.downMax } }

    Surface(
        color = colorResource(R.color.surface),
        shape = Consistent.shape,
        border = BorderStroke(2.dp, colorResource(R.color.surface)),
        modifier = Modifier
            .graphicsLayer(clip = true)
            .then(modifier)
    ) {
        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth()
            ) {
                var addSpace = false
                groupedByDay.forEach { (day, dayRecords) ->
                    if (addSpace) item { Spacer(Modifier.height(20.dp)) }

                    val groupedByHour = dayRecords.groupBy { r -> r.time.atStartOfHourMS(zoneId) }
                    //? For color chart: (max of minutes
                    val maxMinuteAverage = dayRecords
                        .groupBy { r -> r.time.atStartOfMinuteMS(zoneId) }
                        .map { (_, group) ->
                            //!? For performance:
                            //! Not using SpeedRecord.average() because it works a lot more than needed here
                            group.reduceBetter(0f) { a, r -> a + (r.down ?: 0f) }
                        }
                        .maxOrNull() ?: 0f

                    stickyHeader { DayHeader(day, groupedByHour, records.size, locale) }

                    groupedByHour.forEach { (hour, hourRecords) ->
                        val h = hFormatter.format(hour)
                        val a = aFormatter.format(hour)
                        val xAxisStart = "$h:$n00 $a"
                        val xAxisEnd = "$h:$n59 $a"
                        item {
                            RecordRangeView(
                                modifier = Modifier.padding(horizontal = 2.dp),
                                records = hourRecords.reversed(),
                                onRecordDeleted = onRecordDeleted,
                                rangeType = RecordRangeType.Hour,
                                colorMaxValue = maxMinuteAverage,
                                colorChartOverlay = {
                                    ColorChartOverlayText(
                                        xAxisStart,
                                        Modifier
                                            .align(Alignment.CenterStart)
                                            .padding(start = 8.dp)
                                    )
                                    ColorChartOverlayText(
                                        hourRecords.size.localized(),
                                        Modifier.align(Alignment.Center)
                                    )
                                    ColorChartOverlayText(
                                        xAxisEnd,
                                        Modifier
                                            .align(Alignment.CenterEnd)
                                            .padding(end = 8.dp)
                                    )
                                },
                            )
                        }
                    }

                    item {
                        Spacer(
                            Modifier.height(
                                (legendHeight / context.resources.displayMetrics.density).dp +
                                        (legendPadding * 2)
                            )
                        )
                    }

                    if (!addSpace) addSpace = true
                }
            }
            AnimatedVisibility(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .pointerInteropFilter { false },
                visible = !listState.canScrollBackward || !listState.canScrollForward,
                enter = slideIn { IntOffset(0, it.height) } + fadeIn(),
                exit = slideOut { IntOffset(0, it.height) } + fadeOut(),
            ) {
                LegendView(
                    max,
                    Modifier
                        .align(Alignment.BottomCenter)
                        .onGloballyPositioned { legendHeight = it.size.height }
                        .padding(legendPadding)
                )
            }
        }
    }
}

@Composable
private fun LegendView(max: Float, modifier: Modifier = Modifier) {
    val maxValue = BitValue(
        max,
        BitUnit(BitUnitExponent.Metric.Kilo, BitUnitBase.Byte)
    ).toNearestUnit()
    val perSec = "/${durationSuffixes(LocalContext.current).seconds}"

    val primary = colorResource(R.color.primary)
    val warning = colorResource(R.color.warning)
    val error = colorResource(R.color.error)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        color = colorResource(R.color.background),
        shape = Consistent.shape,
        border = BorderStroke(2.dp, colorResource(R.color.primaryContainer)),
        shadowElevation = 5.dp,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 5.dp),
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp, top = 5.dp)
            ) {
                ColorChartOverlayText(
                    stringResource(R.string.from),
                    Modifier.align(Alignment.CenterStart),
                )
                ColorChartOverlayText(
                    stringResource(R.string.records_count),
                    Modifier.align(Alignment.Center),
                )
                ColorChartOverlayText(
                    stringResource(R.string.to),
                    Modifier.align(Alignment.CenterEnd),
                )
            }
            Spacer(Modifier.height(5.dp))
            val weight = FontWeight.Bold
            val size = 12.sp
            val spacing = .5f.sp
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "● ${stringResource(R.string.failAdjective)}",
                    color = error,
                    fontWeight = weight,
                    fontSize = size,
                    letterSpacing = spacing,
                )
                Filler()

                Text(
                    "● ${stringResource(R.string.missingAdjective)}",
                    color = warning,
                    fontWeight = weight,
                    fontSize = size,
                    letterSpacing = spacing,
                )
                Filler()

                Text(
                    "● ${stringResource(R.string.maximum)}: ",
                    color = primary,
                    fontWeight = weight,
                    fontSize = size,
                    letterSpacing = spacing,
                )
                Text(
                    "($maxValue$perSec)",
                    color = primary,
                    fontSize = size,
                    letterSpacing = spacing * .5f,
                )
            }
        }
    }
}

@Composable
private fun DayHeader(
    dayMS: Long,
    groupedByHour: Map<Long, List<SpeedRecord>>,
    totalCount: Int,
    locale: Locale
) {
    val ampm = DateFormatSymbols.getInstance(locale).amPmStrings
    val n0 = 0.localized()
    val n00 = "$n0$n0"
    val n11 = 11.localized()
    val n12 = 12.localized()
    val n59 = 59.localized()
    val xAxisStart = "$n12:$n00 ${ampm[Calendar.AM]}"
    val xAxisEnd = "$n11:$n59 ${ampm[Calendar.PM]}"

    Surface(
        color = colorResource(R.color.secondaryContainer),
        border = BorderStroke(2.dp, colorResource(R.color.onSecondaryContainer).copy(.25f)),
        contentColor = colorResource(R.color.onSecondaryContainer),
        shape = Consistent.shape,
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
        //.heightIn(min = 40.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = SimpleDateFormat("EEEE dd/MM/yyyy", locale)
                    .format(dayMS),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
            )

            val hourlyAverage = groupedByHour.values.map { it.average() }
            val parserLabelFormat = SimpleDateFormat("h a", locale)

            Spacer(modifier = Modifier.height(10.dp))
            RecordRangeView(
                records = hourlyAverage.reversed(),
                rangeType = RecordRangeType.Day,
                colorChartOverlay = {
                    ColorChartOverlayText(
                        xAxisStart,
                        Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 8.dp)
                    )
                    ColorChartOverlayText(
                        totalCount.localized(),
                        Modifier.align(Alignment.Center)
                    )
                    ColorChartOverlayText(
                        xAxisEnd,
                        Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                    )
                },
                parser = { record ->
                    val label = parserLabelFormat.format(record.time.fromUTC())
                    BarData(label, record.down ?: 0f)
                },
            )
        }
    }
}
