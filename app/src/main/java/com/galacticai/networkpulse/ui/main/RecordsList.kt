package com.galacticai.networkpulse.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.ui.graphing.bar_chart.BarData
import com.galacticai.networkpulse.databse.models.SpeedRecord
import com.galacticai.networkpulse.ui.common.records_view.RecordsView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecordsList(
    modifier: Modifier = Modifier,
    records: List<SpeedRecord>,
    reversed: Boolean = false
) {
    Surface(
        color = colorResource(R.color.primaryContainer),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(2.dp, colorResource(R.color.primaryContainer)),
        modifier = Modifier
            .padding(top = 10.dp)
            .graphicsLayer(clip = true)
            .then(modifier)
    ) {
        val recordsInOrder = if (reversed) records.reversed() else records
        LazyColumn(modifier = Modifier.fillMaxWidth()) { // Group records by day
            val groupedByDay = recordsInOrder.groupBy { record ->
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = record.time
                calendar[Calendar.DAY_OF_YEAR] // You can use Calendar.DAY_OF_MONTH for day of the month instead
            }
            val firstDay = groupedByDay.keys.first()
            groupedByDay.forEach { (day, dayRecords) ->
                if (day != firstDay)
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                val groupedByHour = dayRecords.groupBy { record ->
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = record.time
                    calendar[Calendar.HOUR_OF_DAY]
                }
                stickyHeader {
                    DayHeader(day, dayRecords, groupedByHour)
                }


                groupedByHour.forEach { (hour, hourRecords) ->
                    stickyHeader { HourHeader(hour, hourRecords.size) }
                    item {
                        RecordsView(
                            modifier = Modifier.padding(horizontal = 2.dp),
                            records = hourRecords.reversed()
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun DayHeader(day: Int, dayRecords: List<SpeedRecord>, groupedByHour: Map<Int, List<SpeedRecord>>) {
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
                        ).format(
                            Calendar.getInstance().apply { set(Calendar.DAY_OF_YEAR, day) }.time
                        ),
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
                    RecordsView(
                        records = hourlyAverage.reversed(),
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
fun HourHeader(hour: Int, recordsCount: Int) {
    val hourText = SimpleDateFormat(
        "h a", Locale.getDefault()
    ).format(
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
        }.time
    )

    Surface(
        color = colorResource(R.color.primaryContainer),
        contentColor = colorResource(R.color.primary),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(
                text = hourText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "$recordsCount ${stringResource(R.string.records)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
            )
        }
    }
}
