package com.galacticai.networkpulse.common.ui.graphing.bar_chart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.galacticai.networkpulse.common.ui.graphing.vertical
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
fun <T> BarChart(
    modifier: Modifier = Modifier,
    startAsScrolledToEnd: Boolean = false,
    style: BarChartStyle<T> = BarChartStyle(),
    parser: (T) -> BarData,
    data: List<T>,
    onBarClick: ((data: T, parsed: BarData, i: Int) -> Unit)? = null,
) {
    var max by remember { mutableFloatStateOf(Float.MIN_VALUE) }
    var min by remember { mutableFloatStateOf(Float.MAX_VALUE) }
    fun range() = min..max
    val dataParsed by remember(data) {
        derivedStateOf {
            data.map {
                val barData = parser(it)
                if (barData.value > max) max = barData.value
                if (barData.value < min) min = barData.value
                return@map barData
            }
        }
    }
    //? Not sure i need this after adding `remember(data)` but too afraid to remove
    LaunchedEffect(data) {
        @Suppress("UNUSED_EXPRESSION")
        dataParsed //? Trigger derivedStateOf
    }
    if (max < min) return //? Brief moment before max and min are calculated


    val rowState = rememberLazyListState()
    suspend fun scrollToEnd() = rowState.scrollToItem(rowState.layoutInfo.totalItemsCount - 1)
    if (startAsScrolledToEnd) {
        LaunchedEffect(rowState, data) {
            if (rowState.layoutInfo.totalItemsCount > 0) scrollToEnd()
        }
    }
    var scaleValuesWidth by remember { mutableStateOf(0.dp) }

    @Composable
    fun scaleLines() {
        if (style.horizontalScale == null) return
        val ySectionHeight = style.bar.heightMax / style.horizontalScale.count
        Box {
            Column(
                modifier = Modifier
                    .height(style.bar.heightMax)
                    .fillMaxWidth()
            ) {
                @Composable
                fun line() = Divider(
                    modifier = Modifier.padding(0.dp),
                    thickness = style.horizontalScale.thickness,
                    color = style.horizontalScale.color
                )
                line()
                repeat(style.horizontalScale.count) {
                    Spacer(modifier = Modifier.height(ySectionHeight - style.horizontalScale.thickness))
                    line()
                }
            }
        }
    }

    @Composable
    fun scaleValues() {
        val context = LocalContext.current
        val direction = LocalLayoutDirection.current
        if (style.horizontalScale == null) return
        if (style.yValue == null) return

        val ySectionHeight = style.bar.heightMax / style.horizontalScale.count
        Column(
            modifier = Modifier.height(style.bar.heightMax),
            verticalArrangement = Arrangement.Bottom
        ) {
            (style.horizontalScale.count downTo 1).forEach { i ->
                val value = max / style.horizontalScale.count * i
                Box(modifier = Modifier.height(ySectionHeight)) {
                    val startPadding = style.yValue.margin.calculateStartPadding(direction)
                    val endPadding = style.yValue.margin.calculateEndPadding(direction)
                    Surface(
                        modifier = Modifier
                            .heightIn(max = ySectionHeight)
                            .padding(
                                bottom = style.yValue.margin.calculateBottomPadding(),
                                start = startPadding,
                                end = endPadding,
                            ),
                        shape = RoundedCornerShape(
                            topStart = style.yValue.bgRadius / 2,
                            topEnd = style.yValue.bgRadius / 2,
                            bottomStart = style.yValue.bgRadius,
                            bottomEnd = style.yValue.bgRadius
                        ),
                        color = style.yValue.bgColor(value, range(), i),
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(style.yValue.padding)
                                .onGloballyPositioned {
                                    if (value != max) return@onGloballyPositioned
                                    val width = it.size.width
                                    val density = context.resources.displayMetrics.density
                                    scaleValuesWidth = (width / density).dp + startPadding + endPadding + 2.dp
                                },
                            text = style.yValue.format(value, range(), i),
                            color = style.yValue.color(value, range(), i),
                            fontSize = style.yValue.fontSize,
                            fontWeight = style.yValue.fontWeight
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun scrollToEndButton(modifier: Modifier) {
        if (style.scrollButton == null) return
        val canScroll = rowState.canScrollForward
        AnimatedVisibility(
            modifier = modifier,
            visible = canScroll
        ) {
            val direction = LocalLayoutDirection.current
            IconButton(
                modifier = Modifier
                    .shadow(
                        10.dp,
                        shape = CircleShape,
                        ambientColor = style.bgColor,
                        spotColor = style.bgColor,
                    ),
                onClick = { if (canScroll) MainScope().launch { scrollToEnd() } },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = style.scrollButton.bgColor,
                    contentColor = style.scrollButton.color
                ),
            ) {
                Icon(
                    imageVector = if (direction == LayoutDirection.Ltr)
                        Icons.Rounded.KeyboardArrowRight
                    else Icons.Rounded.KeyboardArrowLeft,
                    contentDescription = null,
                    modifier = Modifier.padding(horizontal = 10.dp),
                )
            }
        }
    }

    @Composable
    fun bars() {
        val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
        LazyRow(
            modifier = Modifier
                .height(style.bar.heightMax)
                .fillMaxWidth(),
            state = rowState,
            verticalAlignment = Alignment.Bottom
        ) {
            item { Spacer(modifier = Modifier.width(scaleValuesWidth)) }
            data.forEachIndexed { i, dataItem ->
                if (i > dataParsed.lastIndex) return@forEachIndexed
                val parsedItem = dataParsed[i]
                val barHeight = style.bar.heightMax * (parsedItem.value / max)

                if (i > 0) item { Spacer(modifier = Modifier.width(style.bar.spacing)) }
                item {
                    Box(
                        modifier = Modifier
                            .height(style.bar.heightMax)
                            .clickable { onBarClick?.invoke(dataItem, parsedItem, i) },
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        val bottomRadius = min(style.bar.radius / 5, 2.dp)
                        Surface(
                            modifier = Modifier.size(style.bar.width, barHeight),
                            color = style.bar.color(dataItem, range()),
                            shape = RoundedCornerShape(
                                topStart = style.bar.radius, topEnd = style.bar.radius,
                                bottomStart = bottomRadius, bottomEnd = bottomRadius,
                            ),
                            border = style.bar.border
                        ) {}
                        if (style.xValue != null) {
                            Surface(
                                modifier = Modifier
                                    .vertical()
                                    .rotate(90f * (if (isLtr) -1 else 1))
                                    .heightIn(max = style.bar.heightMax)
                                    .padding(style.xValue.margin),
                                shape = RoundedCornerShape(style.xValue.bgRadius),
                                color = style.xValue.bgColor(dataItem, parsedItem, range()),
                            ) {
                                Text(
                                    modifier = Modifier
                                        .padding(style.xValue.padding),
                                    text = style.xValue.format(dataItem, parsedItem, range()),
                                    color = style.xValue.color(dataItem, parsedItem, range()),
                                    fontSize = style.xValue.fontSize,
                                    fontWeight = style.xValue.fontWeight
                                )
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.width(style.bar.width / 2)) }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(style.bar.heightMax)
            .background(style.bgColor)
            .then(modifier),
    ) {
        scaleLines()
        bars()
        scaleValues()
        scrollToEndButton(Modifier.align(Alignment.CenterEnd))
    }
}


@Preview(showBackground = true)
@Composable
fun BarChartPreview() {
    val data = (10 downTo 0).map { it.toFloat() }
    BarChart(
        parser = { BarData(it.toString(), it) },
        data = data
    )
}