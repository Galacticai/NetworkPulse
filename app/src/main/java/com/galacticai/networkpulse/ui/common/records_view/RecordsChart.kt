package com.galacticai.networkpulse.ui.common.records_view

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import com.galacticai.networkpulse.common.fromUTC
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
import com.galacticai.networkpulse.databse.models.SpeedRecordUtils.isSuccess
import com.galacticai.networkpulse.models.settings.Setting
import com.galacticai.networkpulse.util.localized
import java.util.SortedSet
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordRangeChart(
    records: SortedSet<SpeedRecord>,
    modifier: Modifier = Modifier,
    onRecordDeleted: ((SpeedRecord) -> Unit)? = null,
    parser: ((SpeedRecord) -> BarData)? = null
) {
    val context = LocalContext.current

    val graphHeight by Setting.GraphHeight.remember()
    val graphScaleLinesCount by Setting.GraphScaleLinesCount.remember()
    val graphCellSize by Setting.GraphCellSize.remember()
    val graphCellSpacing by Setting.GraphCellSpacing.remember()

    val selectedState = rememberModalBottomSheetState()
    var selectedRecord by remember { mutableStateOf<SpeedRecord?>(null) }
    val showDetailedRecord by remember(selectedRecord) { derivedStateOf { selectedRecord != null } }
    LaunchedEffect(showDetailedRecord) {
        if (showDetailedRecord) selectedState.show()
        else selectedState.hide()
    }
    if (showDetailedRecord) {
        ModalRecordDetails(
            state = selectedState,
            record = selectedRecord!!,
            onRecordDeleted = { onRecordDeleted?.invoke(selectedRecord!!) }
        ) { selectedRecord = null }
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
            val timeMS = it.time.fromUTC()
            val m = (unit.toMinutes(timeMS) % 60).localized()
            val s = (unit.toSeconds(timeMS) % 60).localized()
            val mSuffix = context.getString(R.string.minute_suffix)
            val sSuffix = context.getString(R.string.second_suffix)
            val label = "$m$mSuffix $s$sSuffix"
            val value = it.down ?: 0f
            BarData(label, value)
        },
        onBarClick = { record, _, _ -> selectedRecord = record },
        data = records
    )
}
