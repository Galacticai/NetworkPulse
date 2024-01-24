package com.galacticai.networkpulse.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.ui.CubicChart
import com.galacticai.networkpulse.common.ui.CubicChartData

@Composable
fun HourGraph(data: CubicChartData) {
    Surface(
        color = colorResource(R.color.background),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, colorResource(R.color.primaryContainer)),
    ) {
        CubicChart(
            modifier = Modifier.padding(5.dp),
            data = data,
            height = 300.dp,
            style = defaultChartStyle(LocalContext.current),
        )
    }
}