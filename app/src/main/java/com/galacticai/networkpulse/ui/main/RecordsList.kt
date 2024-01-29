package com.galacticai.networkpulse.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.number_with_unit.NumberUnit
import com.galacticai.networkpulse.common.number_with_unit.NumberUnitBase
import com.galacticai.networkpulse.common.number_with_unit.NumberUnitPower
import com.galacticai.networkpulse.common.number_with_unit.NumberWithUnit
import com.galacticai.networkpulse.common.ui.CubicChart
import com.galacticai.networkpulse.common.ui.CubicChartData
import com.galacticai.networkpulse.common.ui.CubicChartItem
import com.galacticai.networkpulse.databse.models.SpeedRecordEntity
import com.galacticai.networkpulse.models.settings.Setting
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecordsList(modifier: Modifier = Modifier, records: List<SpeedRecordEntity>) {
    val ctx = LocalContext.current
    var isFirstDay by remember { mutableStateOf(true) }

    var graphWidth by remember { mutableIntStateOf(Setting.GraphWidth.defaultValue) }
    LaunchedEffect(Unit) { graphWidth = Setting.GraphWidth.get(ctx) }

    Surface(
        color = colorResource(R.color.primaryContainer),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, colorResource(R.color.primaryContainer)),
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

            for ((day, dayRecords) in groupedByDay) {
                if (isFirstDay) isFirstDay = false
                else item { Spacer(modifier = Modifier.height(20.dp)) }
                stickyHeader {
                    DayHeader(day)
                }

                val groupedByHour = dayRecords.groupBy { record ->
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = record.time
                    calendar[Calendar.HOUR_OF_DAY]
                }

                for ((hour, hourRecords) in groupedByHour) {
                    stickyHeader { HourHeader(hour, hourRecords.size) }
                    item { HourItems(hourRecords.reversed(), graphWidth) }
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
            .padding(1.dp)
            .height(40.dp),
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = hourText,
            fontSize = 14.sp,
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
                "$recordsCount records",
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
            )
        }
    }
}


@Composable
fun HourItems(hourRecords: List<SpeedRecordEntity>, graphWidth: Int) {
    val ctx = LocalContext.current
    Surface(
        color = colorResource(R.color.background),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyRow {
            item {
                CubicChart(
                    modifier = Modifier
                        .padding(5.dp)
                        .width((hourRecords.size * graphWidth).dp),
                    style = defaultChartStyle(ctx),
                    height = 200.dp,

                    data = CubicChartData(hourRecords.map {
                        val msUnit = TimeUnit.MILLISECONDS
                        val m = msUnit.toMinutes(it.time) % 60
                        val s = msUnit.toSeconds(it.time) % 60
                        val label = "${m}:${s}s"
                        val value = it.down ?: 0f
                        return@map CubicChartItem(label, value)
                    }),
                )
            }
        }
    }
}

fun generateColor(x: Float, xMin: Float, xMax: Float, colorMin: Color, colorMax: Color): Color {
    if (x < xMin) return colorMin
    if (x > xMax) return colorMax

    val fraction = (x - xMin) / (xMax - xMin)
    return Color(
        red = (colorMin.red - colorMax.red) * fraction + colorMax.red,
        green = (colorMin.green - colorMax.green) * fraction + colorMax.green,
        blue = (colorMin.blue - colorMax.blue) * fraction + colorMax.blue
    )
}

@Composable
fun RecordItem(record: SpeedRecordEntity) {
    val hourText = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(record.time)

    @Composable
    fun color(): Color = generateColor(
        x = record.down ?: 0f,
        xMin = 0f,
        xMax = 120f,
        colorMin = colorResource(R.color.error),
        colorMax = colorResource(R.color.success)
    )

    val iconVector: ImageVector
    val iconDescription: String
    if (record.down == null) {
        iconVector = Icons.Filled.Warning
        iconDescription = "Status: no data"
    } else {
        iconVector = Icons.Filled.Check
        iconDescription = "Status: ok"
    }

    //    Column {
    //        Icon(imageVector = iconVector, contentDescription = iconDescription)
    //        Text(hourText, color = color())
    //        if (record.down != null) {
    //            val down = NetSpeed(
    //                record.down.toDouble(), NumberUnit(NumberUnitPower.Binary.Kibi, NumberUnitBase.Byte)
    //            ).toNearestUnit()
    //            Text(down.toString(), color = color())
    //        }
    //    }
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()

    ) {
        Icon(imageVector = iconVector, contentDescription = iconDescription)
        Text(hourText, color = color())
        Divider(
            color = colorResource(R.color.secondary).copy(alpha = .2f),
            modifier = Modifier
                .height(1.dp)
                .weight(1f)
        )
        if (record.down != null) {
            val down = NumberWithUnit(
                record.down.toDouble(), NumberUnit(NumberUnitPower.Binary.Kibi, NumberUnitBase.Byte)
            ).toNearestUnit()
            Text("$down/s", color = color())
        }
    }
}