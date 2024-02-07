package com.galacticai.networkpulse.ui.common.records_view

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.number_with_unit.NumberUnit
import com.galacticai.networkpulse.common.number_with_unit.NumberUnitBase
import com.galacticai.networkpulse.common.number_with_unit.NumberUnitPower
import com.galacticai.networkpulse.common.number_with_unit.NumberWithUnit
import com.galacticai.networkpulse.common.ui.graphing.BarChart
import com.galacticai.networkpulse.common.ui.graphing.BarData
import com.galacticai.networkpulse.common.ui.graphing.BarStyle
import com.galacticai.networkpulse.common.ui.graphing.BarValueStyle
import com.galacticai.networkpulse.databse.models.SpeedRecord
import java.util.concurrent.TimeUnit


@Composable
fun RecordsChart(hourRecords: List<SpeedRecord>, graphSize: Int) {
    val ctx = LocalContext.current
    BarChart(
        startAsScrolledToEnd = true,
        bgColor = colorResource(R.color.background),
        barStyle = BarStyle(
            color = { d, _ -> Color(ctx.getColor(R.color.primary)).copy(alpha = .85f) },
            heightMax = 200.dp,
            width = graphSize.dp,
            radius = 20.dp,
        ),
        yValueStyle = BarValueStyle.YBarValueStyle(
            bgColor = { _, _ ->
                Color(ctx.getColor(R.color.secondaryContainer)).copy(alpha = .8f)
            },
            color = { _, _ ->
                Color(ctx.getColor(R.color.secondary))
            },
            format = { v, _ ->
                val n = NumberWithUnit(
                    v.toDouble(),
                    NumberUnit(NumberUnitPower.Binary.Kibi, NumberUnitBase.Byte)
                ).toNearestUnit() //TODO: use after fixing
                "${n.value.toInt()} ${n.unit.shortUnit}/s"
            },
        ),
        xValueStyle = BarValueStyle.XBarValueStyle(
            bgColor = { record, _, _ ->
                if (record.isError) Color(ctx.getColor(R.color.onError))
                else if (record.isTimeout) Color(ctx.getColor(R.color.onWarning))
                else Color(ctx.getColor(R.color.onPrimary)).copy(alpha = .75f)
            },
            color = { record, _, _ ->
                if (record.isError) Color(ctx.getColor(R.color.error))
                else if (record.isTimeout) Color(ctx.getColor(R.color.warning))
                else Color(ctx.getColor(R.color.primary))
            },
            fontWeight = FontWeight.Normal,
            format = { _, d, _ -> d.label },
        ),
        parser = {
            val unit = TimeUnit.MILLISECONDS
            val m = unit.toMinutes(it.time) % 60
            val s = unit.toSeconds(it.time) % 60
            val mSuffix = ctx.getString(R.string.minute_suffix)
            val sSuffix = ctx.getString(R.string.second_suffix)
            val label = "${m}${mSuffix} ${s}${sSuffix}"
            val value = it.down ?: 0f
            BarData(label, value)
        },
        data = hourRecords
    )
}
