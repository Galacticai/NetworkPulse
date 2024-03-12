package com.galacticai.networkpulse.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
import com.galacticai.networkpulse.common.fromUTC
import com.galacticai.networkpulse.common.models.bit_value.BitUnit
import com.galacticai.networkpulse.common.models.bit_value.BitUnitBase
import com.galacticai.networkpulse.common.models.bit_value.BitUnitExponent
import com.galacticai.networkpulse.common.models.bit_value.BitValue
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

@OptIn(ExperimentalFoundationApi::class)
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
    val haFormatter = SimpleDateFormat("h a", locale)

    Surface(
        color = colorResource(R.color.surface),
        shape = Consistent.shape,
        border = BorderStroke(2.dp, colorResource(R.color.surface)),
        modifier = Modifier
            .graphicsLayer(clip = true)
            .then(modifier)
    ) {
        var legendHeight by rememberSaveable { mutableIntStateOf(0) }
        val maxValue by remember(records) { derivedStateOf { records.downMax } }

        val groupedByDay by remember(records) {
            derivedStateOf {
                records.groupBy { r -> r.time.atStartOfDayMS(zoneId) }
            }
        }


        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth()
            ) {

                var addSpace = false
                groupedByDay.forEach { (day, dayRecords) ->
                    if (addSpace) item { Spacer(Modifier.height(20.dp)) }

                    val groupedByHour = dayRecords.groupBy { r ->
                        r.time.atStartOfHourMS(zoneId)
                    }

                    stickyHeader { DayHeader(day, groupedByHour, records.size, locale) }

                    groupedByHour.forEach { (hour, hourRecords) ->
                        item {
                            val ha = haFormatter.format(hour)
                            val haParts = ha.split(' ')
                            val h = haParts[0]
                            val a = haParts[1]
                            val xAxisStart = "$h:$n00 $a"
                            val xAxisEnd = "$h:$n59 $a"
                            RecordRangeView(
                                modifier = Modifier.padding(horizontal = 2.dp),
                                records = hourRecords.reversed(),
                                onRecordDeleted = onRecordDeleted,
                                rangeType = RecordRangeType.Hour,
                                colorMaxValue = maxValue,
                                colorChartOverlay = {
                                    ColorChartOverlayText(
                                        xAxisStart,
                                        Modifier
                                            .align(Alignment.CenterStart)
                                            .padding(start = 10.dp)
                                    )
                                    ColorChartOverlayText(
                                        hourRecords.size.localized(),
                                        Modifier.align(Alignment.Center)
                                    )
                                    ColorChartOverlayText(
                                        xAxisEnd,
                                        Modifier
                                            .align(Alignment.CenterEnd)
                                            .padding(end = 10.dp)
                                    )
                                },
                            )
                        }
                    }

                    item {
                        Spacer(
                            Modifier.height(
                                (legendHeight / context.resources.displayMetrics.density).dp
                            )
                        )
                    }
                    if (!addSpace) addSpace = true
                }
            }
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.BottomCenter),
                visible = !listState.canScrollBackward || !listState.canScrollForward,
                enter = slideIn { IntOffset(0, it.height) } + fadeIn(),
                exit = slideOut { IntOffset(0, it.height) } + fadeOut(),
            ) {
                LegendView(
                    maxValue,
                    Modifier
                        .align(Alignment.BottomCenter)
                        .onGloballyPositioned { legendHeight = it.size.height })
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun LegendView(maxValue: Float, modifier: Modifier = Modifier) {
    val max = BitValue(
        maxValue,
        BitUnit(BitUnitExponent.Metric.Kilo, BitUnitBase.Byte)
    ).toNearestUnit()
    val perSec = "/${durationSuffixes(LocalContext.current).seconds}"

    val primary = colorResource(R.color.primary)
    val warning = colorResource(R.color.warning)
    val error = colorResource(R.color.error)

    @Composable
    fun dot(color: Color) = Canvas(
        Modifier
            .size(15.dp)
            .padding(3.dp)
    ) {
        drawCircle(
            color = color,
            center = Offset(x = 7.5f, y = 7.5f),
            radius = 7.5f
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(7.dp)
            .pointerInteropFilter { false }
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
            val elevation = 5.dp
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp, top = 5.dp)
            ) {
                ColorChartOverlayText(
                    stringResource(R.string.from),
                    Modifier.align(Alignment.CenterStart),
                    elevation,
                )
                ColorChartOverlayText(
                    stringResource(R.string.records_count),
                    Modifier.align(Alignment.Center),
                    elevation,
                )
                ColorChartOverlayText(
                    stringResource(R.string.to),
                    Modifier.align(Alignment.CenterEnd),
                    elevation,
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
                dot(error)
                Text(
                    stringResource(R.string.failAdjective),
                    color = error,
                    fontWeight = weight,
                    fontSize = size,
                    letterSpacing = spacing,
                )
                Filler()

                dot(warning)
                Text(
                    stringResource(R.string.missingAdjective),
                    color = warning,
                    fontWeight = weight,
                    fontSize = size,
                    letterSpacing = spacing,
                )
                Filler()

                dot(primary)
                Text(
                    "${stringResource(R.string.maximum)}: ",
                    color = primary,
                    fontWeight = weight,
                    fontSize = size,
                    letterSpacing = spacing,
                )
                Text(
                    "($max$perSec)",
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
    var showSummary by remember { mutableStateOf(true) }

    val symbols = DateFormatSymbols.getInstance(locale)
    val amPmStrings = symbols.amPmStrings
    val am = amPmStrings[Calendar.AM]
    val pm = amPmStrings[Calendar.PM]

    val n0 = 0.localized()
    val n00 = "$n0$n0"
    val n11 = 11.localized()
    val n12 = 12.localized()
    val n59 = 59.localized()

    val xAxisStart = "$n12:$n00 $am"
    val xAxisEnd = "$n11:$n59 $pm"

    Surface(
        color = colorResource(R.color.secondaryContainer),
        border = BorderStroke(1.dp, colorResource(R.color.onSecondaryContainer).copy(.5f)),
        contentColor = colorResource(R.color.onSecondaryContainer),
        shape = Consistent.shape,
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
                            "EEEE dd/MM/yyyy", locale
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
                        hourRecords.average()
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    RecordRangeView(
                        records = hourlyAverage.reversed(),
                        rangeType = RecordRangeType.Day,
                        colorChartOverlay = {
                            ColorChartOverlayText(
                                xAxisStart,
                                Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 10.dp)
                            )
                            ColorChartOverlayText(
                                totalCount.localized(), //"${totalCount.localized()} ${stringResource(R.string.records)}",
                                Modifier.align(Alignment.Center)
                            )
                            ColorChartOverlayText(
                                xAxisEnd,
                                Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 10.dp)
                            )
                        },
                        parser = { record ->
                            val label = SimpleDateFormat("h a", locale)
                                .format(record.time.fromUTC())
                            BarData(label, record.down ?: 0f)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun HourHeader(hourMS: Long, records: List<SpeedRecord>, locale: Locale) {
    val hourText = SimpleDateFormat("h a", locale)
        .format(hourMS)

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
        Text(
            "${records.size.localized()} ${stringResource(R.string.records)}",
            fontSize = 12.sp,
        )
    }
}
