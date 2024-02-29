package com.galacticai.networkpulse.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.ui.CubicChart
import com.galacticai.networkpulse.common.ui.CubicChartData
import com.galacticai.networkpulse.models.settings.Setting
import com.galacticai.networkpulse.ui.util.Consistent

@Composable
fun HourGraph(data: CubicChartData) {
    val ctx = LocalContext.current
    var graphWidth by remember { mutableIntStateOf(Setting.GraphCellSize.defaultValue) }
    LaunchedEffect(Unit) {
        graphWidth = Setting.GraphCellSize.get(ctx)
    }

    Surface(
        color = colorResource(R.color.background),
        shape = Consistent.shape,
        border = BorderStroke(1.dp, colorResource(R.color.surface)),
    ) {
        CubicChart(
            modifier = Modifier
                .padding(5.dp)
                .width((data.items.size * graphWidth).dp),
            data = data,
            height = 300.dp,
            style = defaultChartStyle(LocalContext.current),
        )
    }
}