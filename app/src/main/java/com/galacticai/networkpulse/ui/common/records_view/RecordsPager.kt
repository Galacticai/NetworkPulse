package com.galacticai.networkpulse.ui.common.records_view

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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.models.DayRange
import com.galacticai.networkpulse.ui.activities.MainActivity
import com.galacticai.networkpulse.ui.common.NoRecordsMessage
import com.galacticai.networkpulse.ui.main.RecordRangeList
import com.galacticai.networkpulse.util.Consistent
import com.galacticai.networkpulse.util.Consistent.screenHPadding
import com.galacticai.networkpulse.util.localized
import com.guru.fontawesomecomposelib.FaIcon
import com.guru.fontawesomecomposelib.FaIconType
import com.guru.fontawesomecomposelib.FaIcons
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RecordsPager(modifier: Modifier = Modifier) {
    val activity = LocalContext.current as MainActivity
    val timezoneOffset = TimeZone.getDefault().rawOffset

    val days by activity.repo
        .daysLive(timezoneOffset)
        .observeAsState(emptyList())

    if (days.isEmpty()) {
        NoRecordsMessage()
        return
    }

    val pageCount by remember(days) { derivedStateOf { days.size } }
    val pagerState = rememberPagerState(days.lastIndex, pageCount = { pageCount })

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
            val dayRange by remember(days) { derivedStateOf { days[page] } }
            val dayRecords by activity.repo
                .getBetweenLive(dayRange.first, dayRange.last)
                .observeAsState(emptyList())

            RecordRangeList(
                modifier = Modifier.fillMaxSize(),
                records = dayRecords.reversed(),
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
                    currentPage = pagerState.currentPage,
                    days = days,
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
                    .format(days[pagerState.currentPage].first)
                Text(currentDayString)
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 5.dp),
                    color = colorResource(R.color.primary).copy(.25f)
                )
                Text(
                    (pagerState.currentPage + 1).localized(),
                    color = colorResource(R.color.secondary),
                    fontWeight = FontWeight.W900,
                )
                Text(" ${stringResource(R.string.of)} ${pagerState.pageCount.localized()}")
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
    currentPage: Int,
    days: List<DayRange>,
    onDismissRequest: () -> Unit,
    onPicked: (Int) -> Unit
) {
    val empty by remember(days) { derivedStateOf { days.isEmpty() } }
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        shape = Consistent.shape,
    ) {
        val pad2 = Consistent.screenHorizontalPadding * 2
        Row(
            Modifier
                .fillMaxWidth()
                .padding(
                    start = pad2,
                    end = pad2,
                    bottom = (if (empty) 50 else 20).dp
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(
                    if (empty) R.string.no_days_found
                    else R.string.pick_a_day
                ),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            if (!empty) {
                Text("${days.size.localized()} ${stringResource(R.string.days)}")
            }
        }
        if (empty) return@ModalBottomSheet

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .screenHPadding(),
            color = colorResource(R.color.background),
            shape = Consistent.shape,
        ) {
            val otherTextColor = colorResource(R.color.onBackground).copy(.75f)
            LazyColumn(Modifier.heightIn(max = 600.dp)) {
                for (page in days.size downTo 1) {
                    val pageIndex = page - 1
                    val day = days[pageIndex]
                    val isCurrentPage = currentPage == pageIndex

                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = Consistent.shape,
                            color = colorResource(
                                if (isCurrentPage) R.color.primaryContainer
                                else R.color.background
                            ),
                            onClick = { onPicked(pageIndex) },
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = SimpleDateFormat("EEE dd/MM/yyyy", Locale.getDefault())
                                        .format(day.first),
                                    color = if (isCurrentPage) colorResource(R.color.primary) else otherTextColor,
                                    fontWeight = if (isCurrentPage) FontWeight.Bold else FontWeight.Normal
                                )
                                HorizontalDivider(
                                    Modifier
                                        .weight(1f)
                                        .padding(horizontal = 10.dp),
                                    color = if (isCurrentPage) colorResource(R.color.primary).copy(.5f)
                                    else colorResource(R.color.surface)
                                )
                                Text(
                                    "${day.count.localized()} ${stringResource(R.string.records)}",
                                    color = if (isCurrentPage) colorResource(R.color.secondary) else otherTextColor,
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(45.dp))
    }
}
