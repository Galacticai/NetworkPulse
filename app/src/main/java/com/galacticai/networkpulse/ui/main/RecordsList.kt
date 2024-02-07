package com.galacticai.networkpulse.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.databse.models.SpeedRecord
import com.galacticai.networkpulse.models.settings.Setting
import com.galacticai.networkpulse.ui.common.records_view.RecordsView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecordsList(modifier: Modifier = Modifier, records: List<SpeedRecord>) {
    val ctx = LocalContext.current

    var graphSize by remember { mutableIntStateOf(Setting.GraphSize.defaultValue) }
    LaunchedEffect(Unit) { graphSize = Setting.GraphSize.get(ctx) }

    Surface(
        color = colorResource(R.color.primaryContainer),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(2.dp, colorResource(R.color.primaryContainer)),
        modifier = Modifier
            .padding(top = 10.dp)
            .graphicsLayer(clip = true)
            .then(modifier)
    ) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) { // Group records by day
            val groupedByDay = records.groupBy { record ->
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = record.time
                calendar[Calendar.DAY_OF_YEAR] // You can use Calendar.DAY_OF_MONTH for day of the month instead
            }
            val firstDay = groupedByDay.keys.first()
            for ((day, dayRecords) in groupedByDay) {
                if (day != firstDay)
                    item { Spacer(modifier = Modifier.height(20.dp)) }

                stickyHeader { DayHeader(day) }

                val groupedByHour = dayRecords.groupBy { record ->
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = record.time
                    calendar[Calendar.HOUR_OF_DAY]
                }

                for ((hour, hourRecords) in groupedByHour) {
                    stickyHeader { HourHeader(hour, hourRecords.size) }
                    item { RecordsView(hourRecords.reversed(), graphSize) }
                }
            }
        }
    }
}


@Composable
fun DayHeader(day: Int) {
    val hourText = SimpleDateFormat(
        "EEEE dd/MM/yyyy", Locale.getDefault()
    ).format(
        Calendar.getInstance().apply {
            set(Calendar.DAY_OF_YEAR, day)
        }.time
    )

    Surface(
        color = colorResource(R.color.secondaryContainer),
        border = BorderStroke(1.dp, colorResource(R.color.onSecondaryContainer)),
        contentColor = colorResource(R.color.onSecondaryContainer),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .height(40.dp),
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = hourText,
            fontSize = 12.sp,
            letterSpacing = .2f.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        )
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
