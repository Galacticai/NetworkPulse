package com.galacticai.networkpulse.ui.main

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.ui.CubicChartScaleStyle
import com.galacticai.networkpulse.common.ui.CubicChartStyle

fun defaultChartStyle(context: Context): CubicChartStyle {
    return CubicChartStyle(
        bgColor = Color(context.getColor(R.color.background)),
        graphColor = Color(context.getColor(R.color.primary)),
        pointColor = Color(context.getColor(R.color.secondary)).copy(alpha = .75f),

        yScaleStyle = CubicChartScaleStyle.CubicChartYScaleStyle(
            lineColor = Color(context.getColor(R.color.secondary)).copy(alpha = .4f),
            lineCount = 6,
            lineWidth = 1.5f,
            textSize = 32f,
            textColor = Color(context.getColor(R.color.primary)),
            unit = "KiB/s",
        ),
        xScaleStyle = CubicChartScaleStyle.CubicChartXScaleStyle(
            lineColor = Color(context.getColor(R.color.secondary)).copy(alpha = .4f),
            lineWidth = 1.5f,
            textSize = 32f,
            textColor = Color(context.getColor(R.color.primary)),
            unit = "",
        )
    )
}