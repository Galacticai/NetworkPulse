package com.galacticai.networkpulse.ui.common.records_view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.atStartOfDayMS
import com.galacticai.networkpulse.databse.models.SpeedRecord
import com.galacticai.networkpulse.ui.main.RecordsList
import com.guru.fontawesomecomposelib.FaIcon
import com.guru.fontawesomecomposelib.FaIconType
import com.guru.fontawesomecomposelib.FaIcons
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecordsPager(
    modifier: Modifier = Modifier,
    records: List<SpeedRecord>
) {
    if (records.isEmpty()) {
        Text(modifier = modifier, text = "No records")
        return
    }

    val recordsByDay by remember(records) {
        derivedStateOf { records.groupBy { it.time.atStartOfDayMS() } }
    }

    fun dayKey(i: Int) = recordsByDay.keys.toList()[i]

    val pageCount by remember(recordsByDay) { derivedStateOf { recordsByDay.size } }

    val pagerState = rememberPagerState(pageCount - 1, pageCount = { pageCount })

    val canScrollForward by remember(pagerState) {
        derivedStateOf { pagerState.currentPage < pagerState.pageCount - 1 }
    }
    val canScrollBackward by remember(pagerState) {
        derivedStateOf { pagerState.currentPage > 0 }
    }
    val isFirstPage by remember(pagerState) {
        derivedStateOf { pagerState.currentPage == 0 }
    }
    val isLastPage by remember(pagerState) {
        derivedStateOf { pagerState.currentPage == pagerState.pageCount - 1 }
    }


    fun scrollTo(page: Int) {
        if (page < 0 || page >= pagerState.pageCount) return
        MainScope().launch { pagerState.scrollToPage(page) }
    }

    fun scrollRelative(amount: Int) =
        scrollTo(pagerState.currentPage + amount)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        HorizontalPager(
            state = pagerState,
            pageSpacing = 20.dp,
            modifier = Modifier.weight(1f),
        ) { page ->
            val dayRecords = recordsByDay[dayKey(page)]!!
            Box(modifier = Modifier.fillMaxSize()) {
                RecordsList(
                    modifier = Modifier.align(Alignment.TopCenter),
                    records = dayRecords,
                    reversed = true,
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val firstIcon: FaIconType
            val previousIcon: FaIconType
            val nextIcon: FaIconType
            val lastIcon: FaIconType
            if (LocalLayoutDirection.current == LayoutDirection.Ltr) {
                firstIcon = FaIcons.AngleDoubleLeft
                previousIcon = FaIcons.AngleLeft
                nextIcon = FaIcons.AngleRight
                lastIcon = FaIcons.AngleDoubleRight
            } else {
                firstIcon = FaIcons.AngleDoubleRight
                previousIcon = FaIcons.ArrowRight
                nextIcon = FaIcons.ArrowLeft
                lastIcon = FaIcons.AngleDoubleLeft
            }

            IconButton(
                onClick = { scrollTo(0) },
                enabled = !isFirstPage
            ) { FaIcon(firstIcon, tint = LocalContentColor.current) }
            IconButton(
                onClick = { scrollRelative(-1) },
                enabled = canScrollBackward
            ) { FaIcon(previousIcon, tint = LocalContentColor.current) }
            Text(
                modifier = Modifier.weight(1f),
                text = "${pagerState.currentPage + 1}/${pagerState.pageCount}",
                color = colorResource(R.color.primary),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            )
            IconButton(
                onClick = { scrollRelative(1) },
                enabled = canScrollForward
            ) {
                FaIcon(
                    nextIcon,
                    tint = colorResource(R.color.secondary).copy(LocalContentColor.current.alpha)
                )
            }
            IconButton(
                onClick = { scrollTo(pagerState.pageCount - 1) },
                enabled = !isLastPage
            ) { FaIcon(lastIcon, tint = LocalContentColor.current) }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun RecordsPagerPreview() {
    RecordsPager(records = emptyList())
}

@Preview(showBackground = true)
@Composable
private fun RecordsPagerPreview2() {
    Scaffold { scaffoldPadding ->
        RecordsPager(
            modifier = Modifier.padding(scaffoldPadding),
            records = listOf(
                SpeedRecord.Success(System.currentTimeMillis() + 2000L, 1000, 1f, 29f),
                SpeedRecord.Error(System.currentTimeMillis() - 1200000L, 1000),
                SpeedRecord.Error(System.currentTimeMillis() - 1200090L, 1000),
                SpeedRecord.Error(System.currentTimeMillis() - 1200091L, 1000),
            )
        )
    }
}