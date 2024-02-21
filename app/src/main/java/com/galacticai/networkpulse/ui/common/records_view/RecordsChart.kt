package com.galacticai.networkpulse.ui.common.records_view

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.models.bit_value.BitUnit
import com.galacticai.networkpulse.common.models.bit_value.BitUnitBase
import com.galacticai.networkpulse.common.models.bit_value.BitUnitExponent
import com.galacticai.networkpulse.common.models.bit_value.BitValue
import com.galacticai.networkpulse.common.ui.graphing.bar_chart.BarChart
import com.galacticai.networkpulse.common.ui.graphing.bar_chart.BarChartStyle
import com.galacticai.networkpulse.common.ui.graphing.bar_chart.BarData
import com.galacticai.networkpulse.common.ui.graphing.bar_chart.BarHScaleStyle
import com.galacticai.networkpulse.common.ui.graphing.bar_chart.BarScrollButtonStyle
import com.galacticai.networkpulse.common.ui.graphing.bar_chart.BarStyle
import com.galacticai.networkpulse.common.ui.graphing.bar_chart.BarValueStyle
import com.galacticai.networkpulse.databse.models.SpeedRecord
import com.galacticai.networkpulse.models.settings.Setting
import com.galacticai.networkpulse.ui.common.localized
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordRangeChart(
    modifier: Modifier = Modifier,
    records: List<SpeedRecord>,
    onRecordDeleted: ((SpeedRecord) -> Unit)? = null,
    parser: ((SpeedRecord) -> BarData)? = null
) {
    val context = LocalContext.current

    var graphHeight by remember { mutableIntStateOf(Setting.GraphHeight.defaultValue) }
    var graphScaleLinesCount by remember { mutableIntStateOf(Setting.GraphScaleLinesCount.defaultValue) }
    var graphCellSize by remember { mutableIntStateOf(Setting.GraphCellSize.defaultValue) }
    var graphCellSpacing by remember { mutableIntStateOf(Setting.GraphCellSpacing.defaultValue) }
    LaunchedEffect(Unit) {
        graphCellSize = Setting.GraphCellSize.get(context)
        graphScaleLinesCount = Setting.GraphScaleLinesCount.get(context)
        graphHeight = Setting.GraphHeight.get(context)
        graphCellSpacing = Setting.GraphCellSpacing.get(context)
    }

    val detailedState = rememberModalBottomSheetState()
    var detailedRecord by remember { mutableStateOf<SpeedRecord?>(null) }
    val showDetailedRecord by remember(detailedRecord) { derivedStateOf { detailedRecord != null } }
    LaunchedEffect(showDetailedRecord) {
        if (showDetailedRecord) detailedState.show()
        else detailedState.hide()
    }
    if (showDetailedRecord) {
        ModalRecordDetails(
            state = detailedState,
            record = detailedRecord!!,
            onRecordDeleted = { onRecordDeleted?.invoke(detailedRecord!!) }
        ) { detailedRecord = null }
    }

    BarChart(
        modifier = modifier,
        startAsScrolledToEnd = true,
        style = BarChartStyle(
            bgColor = colorResource(R.color.background),
            bar = BarStyle(
                color = { _, _ -> Color(context.getColor(R.color.primary)).copy(alpha = .85f) },
                heightMax = graphHeight.dp,
                width = graphCellSize.dp,
                radius = 20.dp,
                spacing = graphCellSpacing.dp,
            ),
            horizontalScale = BarHScaleStyle(
                count = graphScaleLinesCount,
                thickness = 1.dp,
                color = colorResource(R.color.secondary).copy(.15f),
            ),
            yValue = BarValueStyle.YBarValueStyle(
                bgColor = { _, _, _ ->
                    Color(context.getColor(R.color.secondaryContainer)).copy(alpha = .8f)
                },
                color = { _, _, _ ->
                    Color(context.getColor(R.color.secondary))
                },
                format = { v, _, i ->
                    val n = BitValue(v, BitUnit(BitUnitExponent.Metric.Kilo, BitUnitBase.Byte))
                        .toNearestUnit()
                    "${n.value.toInt()}" +
                            if (i == graphScaleLinesCount) "\u2009${n.unit.name.short}/${context.getString(R.string.second_suffix)}"
                            else ""
                },
            ),
            xValue = BarValueStyle.XBarValueStyle(
                bgColor = { record, _, _ ->
                    if (record.isSuccess) Color(context.getColor(R.color.onPrimary)).copy(alpha = .75f)
                    else Color(context.getColor(R.color.error)).copy(.5f)
                },
                color = { record, _, _ ->
                    if (record.isSuccess) Color(context.getColor(R.color.primary))
                    else Color(context.getColor(R.color.onError))
                },
                fontWeight = FontWeight.Normal,
                format = { _, d, _ -> d.label },
            ),
            scrollButton = BarScrollButtonStyle(
                color = colorResource(R.color.secondary),
                bgColor = colorResource(R.color.secondaryContainer).copy(.6f),
            ),
        ),
        parser = parser ?: {
            val unit = TimeUnit.MILLISECONDS
            val m = (unit.toMinutes(it.time) % 60).localized()
            val s = (unit.toSeconds(it.time) % 60).localized()
            val mSuffix = context.getString(R.string.minute_suffix)
            val sSuffix = context.getString(R.string.second_suffix)
            val label = "$m$mSuffix $s$sSuffix"
            val value = it.down ?: 0f
            BarData(label, value)
        },
        onBarClick = { record, _, _ -> detailedRecord = record },
        data = records
    )
}
