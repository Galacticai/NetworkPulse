package com.galacticai.networkpulse.ui.common.records_view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.atStartOfDayMS
import com.galacticai.networkpulse.databse.models.SpeedRecord
import com.galacticai.networkpulse.ui.common.localized
import com.galacticai.networkpulse.ui.main.RecordRangeList
import com.guru.fontawesomecomposelib.FaIcon
import com.guru.fontawesomecomposelib.FaIconType
import com.guru.fontawesomecomposelib.FaIcons
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RecordsPager(
    modifier: Modifier = Modifier,
    recordsByDay: Map<Long, List<SpeedRecord>>,
    onRecordDeleted: ((SpeedRecord) -> Unit)? = null
) {
    fun dayKey(i: Int) = recordsByDay.keys.toList()[i]

    val pageCount by remember(recordsByDay) { derivedStateOf { recordsByDay.size } }

    val pagerState = rememberPagerState(pageCount - 1, pageCount = { pageCount })

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
            RecordRangeList(
                modifier = Modifier.fillMaxSize(),
                records = dayRecords,
                reversed = true,
                onRecordDeleted = onRecordDeleted,
            )
        }

        Row(
            modifier = Modifier.padding(top = 5f.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val canScrollStart by remember(pagerState) {
                derivedStateOf { pagerState.currentPage > 1 }
            }
            val canScrollBackward by remember(pagerState) {
                derivedStateOf { pagerState.currentPage > 0 }
            }
            val canScrollForward by remember(pagerState) {
                derivedStateOf { pagerState.currentPage < pagerState.pageCount - 1 }
            }
            val canScrollEnd by remember(pagerState) {
                derivedStateOf { pagerState.currentPage < pagerState.pageCount - 2 }
            }

            var pickingDay by remember { mutableStateOf(false) }
            val sheetState = rememberModalBottomSheetState()
            if (pickingDay) {
                DayPicker(
                    sheetState = sheetState,
                    currentDay = dayKey(pagerState.currentPage),
                    days = recordsByDay,
                    onDismissRequest = { pickingDay = false },
                ) {
                    scrollTo(it)
                    pickingDay = false
                }
            }

            val secondary = colorResource(R.color.secondary)
            val startIcon: FaIconType
            val previousIcon: FaIconType
            val nextIcon: FaIconType
            val endIcon: FaIconType
            val ll = FaIcons.AngleDoubleLeft
            val l = FaIcons.AngleLeft
            val r = FaIcons.AngleRight
            val rr = FaIcons.AngleDoubleRight
            if (LocalLayoutDirection.current == LayoutDirection.Ltr) {
                startIcon = ll
                previousIcon = l
                nextIcon = r
                endIcon = rr
            } else {
                startIcon = rr
                previousIcon = r
                nextIcon = l
                endIcon = ll
            }

            IconButton(
                onClick = { scrollTo(0) },
                enabled = canScrollStart
            ) { FaIcon(startIcon, tint = secondary.copy(LocalContentColor.current.alpha)) }
            IconButton(
                onClick = { scrollRelative(-1) },
                enabled = canScrollBackward
            ) { FaIcon(previousIcon, tint = secondary.copy(LocalContentColor.current.alpha)) }

            ElevatedButton(
                modifier = Modifier.weight(1f),
                onClick = { pickingDay = true },
            ) {
                val currentDayString = SimpleDateFormat("dd/M/yyyy", Locale.getDefault())
                    .format(dayKey(pagerState.currentPage))
                Text(currentDayString)
                Divider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 5.dp),
                    color = colorResource(R.color.primary).copy(.25f),
                )
                Text(
                    (pagerState.currentPage + 1).localized(),
                    fontWeight = FontWeight.W900,
                )
                Text(
                    " ${stringResource(R.string.of)} ${pagerState.pageCount.localized()}"
                )
            }

            IconButton(
                onClick = { scrollRelative(1) },
                enabled = canScrollForward
            ) { FaIcon(nextIcon, tint = secondary.copy(LocalContentColor.current.alpha)) }
            IconButton(
                onClick = { scrollTo(pagerState.pageCount - 1) },
                enabled = canScrollEnd
            ) { FaIcon(endIcon, tint = secondary.copy(LocalContentColor.current.alpha)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayPicker(
    sheetState: SheetState,
    currentDay: Long,
    days: Map<Long, List<SpeedRecord>>,
    onDismissRequest: () -> Unit,
    onPicked: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    LaunchedEffect(Unit) {
        listState.scrollToItem(days.keys.indexOf(currentDay))
    }
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(20.dp),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            text = stringResource(R.string.pick_a_day),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        val otherTextColor = colorResource(R.color.onBackground).copy(.75f)
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .heightIn(max = 900.dp),
            state = listState,
        ) {
            items(days.size) {
                val day = days.keys.elementAt(it)
                val isCurrent = day == currentDay
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    border = if (!isCurrent) null
                    else BorderStroke(1.dp, colorResource(R.color.primary).copy(alpha = .25f)),
                    color = colorResource(
                        if (isCurrent) R.color.primaryContainer
                        else R.color.background
                    ),
                    shadowElevation = 5.dp,
                    onClick = { onPicked(it) },
                ) {
                    Row(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                        val title = SimpleDateFormat("dd/MM/yyyy EEEE", Locale.getDefault())
                            .format(day)
                        Text(
                            modifier = Modifier.weight(1f),
                            text = title,
                            fontSize = 16.sp,
                            color = if (isCurrent) colorResource(R.color.primary) else otherTextColor,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                        )
                        Text(
                            "${days[day]!!.size} ${stringResource(R.string.records)}",
                            color = if (isCurrent) colorResource(R.color.secondary) else otherTextColor,
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecordsPagerPreview() {
    Scaffold { scaffoldPadding ->
        RecordsPager(
            modifier = Modifier.padding(scaffoldPadding),
            recordsByDay = listOf(
                SpeedRecord.Success(System.currentTimeMillis() + 2000L, 1000, 1f, 29f),
                SpeedRecord.error(System.currentTimeMillis() - 1200000L, 1000),
                SpeedRecord.error(System.currentTimeMillis() - 1200090L, 1000),
                SpeedRecord.error(System.currentTimeMillis() - 1200091L, 1000),
            ).groupBy { it.time.atStartOfDayMS() }
        )
    }
}
