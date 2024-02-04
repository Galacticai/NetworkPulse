package com.galacticai.networkpulse.common.ui.graphing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import com.galacticai.networkpulse.R
import org.jetbrains.annotations.Range


@Composable
fun <T> BarChart(
    modifier: Modifier = Modifier,
    barStyle: BarStyle<T> = BarStyle(),
    xValueStyle: BarValueStyle.XBarValueStyle<T>? = BarValueStyle.XBarValueStyle(),
    yValueStyle: BarValueStyle.YBarValueStyle? = BarValueStyle.YBarValueStyle(),
    bgColor: Color = MaterialTheme.colorScheme.primary,
    horizontalScaleStyle: BarHScaleStyle? = BarHScaleStyle(),
    startAsScrolledToEnd: Boolean = false,
    parser: (T) -> BarData,
    data: @Range(from = 0L, to = Long.MAX_VALUE) List<T>,
) {
    val parsed = data.map { parser(it) }
    val max = parsed.maxOf { it.value }
    val min = parsed.minOf { it.value }
    val range = min..max

    val rowState = rememberLazyListState()
    if (startAsScrolledToEnd) {
        LaunchedEffect(rowState) {
            if (rowState.layoutInfo.totalItemsCount > 0)
                rowState.scrollToItem(rowState.layoutInfo.totalItemsCount - 1)
        }
    }

    var scaleValuesWidth by remember { mutableStateOf(0.dp) }

    @Composable
    fun scaleLines() {
        if (horizontalScaleStyle == null) return
        val ySectionHeight = barStyle.heightMax / horizontalScaleStyle.count
        Box {
            Column(
                modifier = Modifier
                    .height(barStyle.heightMax)
                    .fillMaxWidth()
            ) {
                @Composable
                fun line() = Divider(
                    modifier = Modifier.padding(0.dp),
                    thickness = horizontalScaleStyle.thickness,
                    color = horizontalScaleStyle.color
                )
                line()
                repeat(horizontalScaleStyle.count) {
                    Spacer(modifier = Modifier.height(ySectionHeight - horizontalScaleStyle.thickness))
                    line()
                }
            }
        }
    }

    @Composable
    fun scaleValues() {
        val context = LocalContext.current
        val direction = LocalLayoutDirection.current
        if (horizontalScaleStyle == null) return
        if (yValueStyle == null) return

        val ySectionHeight = barStyle.heightMax / horizontalScaleStyle.count
        Column(
            modifier = Modifier.height(barStyle.heightMax),
            verticalArrangement = Arrangement.Bottom
        ) {
            for (i in horizontalScaleStyle.count downTo 1) {
                val value = max / horizontalScaleStyle.count * i
                Box(modifier = Modifier.height(ySectionHeight)) {
                    Surface(
                        modifier = Modifier
                            .heightIn(max = ySectionHeight)
                            .padding(
                                bottom = yValueStyle.margin.calculateBottomPadding(),
                                start = yValueStyle.margin.calculateStartPadding(direction),
                                end = yValueStyle.margin.calculateEndPadding(direction),
                            ),
                        shape = RoundedCornerShape(
                            topStart = yValueStyle.bgRadius / 2,
                            topEnd = yValueStyle.bgRadius / 2,
                            bottomStart = yValueStyle.bgRadius,
                            bottomEnd = yValueStyle.bgRadius
                        ),
                        color = yValueStyle.bgColor(value, range),
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(yValueStyle.padding)
                                .onGloballyPositioned {
                                    if (value != max) return@onGloballyPositioned
                                    val width = it.size.width
                                    val density = context.resources.displayMetrics.density
                                    scaleValuesWidth = (width / density).dp
                                },
                            text = yValueStyle.format(value, range),
                            color = yValueStyle.color(value, range),
                            fontSize = yValueStyle.fontSize,
                            fontWeight = yValueStyle.fontWeight
                        )
                    }
                }
            }
        }
    }


    @Composable
    fun bars() {
        fun calcBarHeight(value: Float): Dp {
            return max(
                barStyle.heightMax * (value / max),
                min(
                    barStyle.radius,
                    min(
                        barStyle.heightMax / 100,
                        5.dp
                    )
                )
            )
        }

        val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
        LazyRow(
            modifier = Modifier
                .height(barStyle.heightMax)
                .fillMaxWidth(),
            state = rowState,
            verticalAlignment = Alignment.Bottom
        ) {
            item { Spacer(modifier = Modifier.width(scaleValuesWidth)) }

            data.forEachIndexed { i, dataItem ->
                val parsedItem = parsed[i]
                val barHeight = calcBarHeight(parsedItem.value)

                if (i > 0) item { Spacer(modifier = Modifier.width(barStyle.spacing)) }
                item {
                    Box(
                        modifier = Modifier.height(barStyle.heightMax),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        Surface(
                            modifier = Modifier.size(barStyle.width, barHeight),
                            shape = RoundedCornerShape(
                                topStart = barStyle.radius,
                                topEnd = barStyle.radius
                            ),
                            border = barStyle.border,
                            color = barStyle.color(dataItem, range),
                        ) {}
                        if (xValueStyle != null) {
                            Surface(
                                modifier = Modifier
                                    .vertical()
                                    .rotate(90f * (if (isLtr) -1 else 1))
                                    .heightIn(max = barStyle.heightMax)
                                    .padding(xValueStyle.margin), //.width(barStyle.heightMax),
                                shape = RoundedCornerShape(xValueStyle.bgRadius),
                                color = xValueStyle.bgColor(dataItem, parsedItem, range),
                            ) {
                                Text(
                                    modifier = Modifier
                                        .padding(xValueStyle.padding),
                                    text = xValueStyle.format(dataItem, parsedItem, range),
                                    color = xValueStyle.color(dataItem, parsedItem, range),
                                    fontSize = xValueStyle.fontSize,
                                    fontWeight = xValueStyle.fontWeight
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(barStyle.heightMax)
            .then(modifier),
        color = bgColor,
    ) {
        scaleLines()
        bars()
        scaleValues()
    }
}


@Preview(showBackground = true)
@Composable
fun BarChartPreview() {
    val ctx = LocalContext.current
    val data = (10 downTo 0).map { it.toFloat() }
    BarChart(
        bgColor = Color(ctx.getColor(R.color.background)),
        parser = { BarData(it.toString(), it) },
        data = data
    )
}