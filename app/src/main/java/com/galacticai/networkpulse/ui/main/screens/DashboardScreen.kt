package com.galacticai.networkpulse.ui.main.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.ui.MainActivity
import com.galacticai.networkpulse.ui.common.ScreenTitle
import com.galacticai.networkpulse.ui.common.records_view.RecordsPager

@Composable
fun DashboardScreen() {
    val context = LocalContext.current as MainActivity
    val dao = context.viewModel.dao
    val records by rememberSaveable { mutableStateOf(dao.getAll()) }

    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth(),
    ) {
        ScreenTitle(stringResource(R.string.dashboard))
        Spacer(modifier = Modifier.height(10.dp))
        RecordsPager(
            modifier = Modifier.weight(1f),
            records = records
        )
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DashboardScreen() {
//    val context = LocalContext.current as MainActivity
//
//    var allowedRange by rememberSaveable { mutableStateOf(0L to 0L) }
//    var selectedRange by rememberSaveable { mutableStateOf(0L to 0L) }
//    var recordsCount by rememberSaveable { mutableIntStateOf(0) }
//    var recordsInRangeCount by rememberSaveable { mutableIntStateOf(0) }
//    var recordsInRange by rememberSaveable { mutableStateOf<List<SpeedRecord>?>(null) }
//
//    val dao = context.viewModel.dao
//    fun updateCount() = Thread {
//        recordsInRangeCount = dao.countBetween(selectedRange.first, selectedRange.second)
//    }.start()
//
//    fun updateRecords() = Thread {
//        recordsInRange = dao.getBetween(selectedRange.first, selectedRange.second)
//    }.start()
//
//    LaunchedEffect(Unit) {
//        recordsCount = dao.countAll()
//        allowedRange = dao.getOldestTime() to dao.getNewestTime()
//        val hour = 60 * 60 * 1000
//        val hourAgo = System.currentTimeMillis() - hour
//        //? allowedRange end:: start of day
//        var atStartOfDay = allowedRange.second.atStartOfDayMS()
//        //? if too recent (less than 1h ago)
//        if (atStartOfDay > hourAgo) {
//            //? push back 24h so it is 00:00 of yesterday
//            atStartOfDay = System.currentTimeMillis() - hour * 24
//        }
//        //? Stay within allowed range
//        val allowedOrDayStart = max(allowedRange.first, atStartOfDay)
//        selectedRange = allowedOrDayStart to allowedRange.second
//        updateCount()
//        updateRecords()
//    }
//    //
//    //    //    allowedRange
//    //    //    selectedRange
//    //    //    recordsCount
//    //    //    recordsInRangeCount
//    //    //    recordsInRange
//    //    LaunchedEffect(Unit) {
//    //        allowedRange = dao.getOldestTime() to dao.getNewestTime()
//    //        val hour = 60 * 60 * 1000
//    //        val hourAgo = System.currentTimeMillis() - hour
//    //        //? allowedRange end:: start of day
//    //        var atStartOfDay = allowedRange.second.atStartOfDayMS()
//    //        //? if too recent (less than 1h ago)
//    //        if (atStartOfDay > hourAgo) {
//    //            //? push back 24h so it is 00:00 of yesterday
//    //            atStartOfDay = System.currentTimeMillis() - hour * 24
//    //        }
//    //        //? Stay within allowed range
//    //        val allowedOrDayStart = max(allowedRange.first, atStartOfDay)
//    //    }
//    //    LaunchedEffect(allowedRange) {
//    //        recordsCount = dao.countAll()
//    //
//    //        val start = max(allowedRange.first, selectedRange.first)
//    //        val end = min(allowedRange.second, selectedRange.second)
//    //        selectedRange = start to end
//    //    }
//    //    LaunchedEffect(selectedRange) {
//    //        recordsInRangeCount = dao.countBetween(selectedRange.first, selectedRange.second)
//    //    }
//
//    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
//        TopBar(stringResource(R.string.dashboard)) {
//            IconButton(onClick = { updateRecords() }) {
//                Icon(
//                    imageVector = Icons.Rounded.Refresh,
//                    contentDescription = stringResource(R.string.refresh)
//                )
//            }
//        }
//        ExpandableItem(title = stringResource(R.string.time_range)) { itemPadding ->
//            Column(
//                modifier = Modifier
//                    .padding(itemPadding)
//                    .fillMaxWidth()
//            ) {
//                Row {
//                    var picking by remember { mutableIntStateOf(-1) }
//                    if (picking >= 0) {
//                        ModalBottomSheet(
//                            containerColor = colorResource(R.color.background),
//                            onDismissRequest = { picking = -1 },
//                            shape = RoundedCornerShape(20.dp),
//                        ) {
//                            DateTimePicker(
//                                modifier = Modifier.padding(horizontal = 10.dp),
//                                initialValue =
//                                if (picking == 0) selectedRange.first
//                                else selectedRange.second,
//                                cancelText = { Text(text = stringResource(R.string.cancel)) },
//                                confirmText = { Text(text = stringResource(R.string.select)) },
//                                allowedRange = allowedRange,
//                                onDismissRequest = { picking = -1 },
//                                onPicked = {
//                                    selectedRange =
//                                        if (picking == 0) it to selectedRange.second
//                                        else selectedRange.first to it
//                                    updateCount()
//                                }
//                            )
//                        }
//                    }
//
//                    @Composable
//                    fun pickingButton(i: Int) {
//                        if (i != 0 && i != 1) throw IndexOutOfBoundsException()
//                        Column(
//                            modifier = Modifier.weight(.5f),
//                            horizontalAlignment = Alignment.CenterHorizontally
//                        ) {
//                            Text(
//                                modifier = Modifier.fillMaxWidth(),
//                                text = stringResource(if (i == 0) R.string.from else R.string.to),
//                                textAlign = TextAlign.Center,
//                            )
//                            TextButton(
//                                modifier = Modifier.fillMaxWidth(),
//                                onClick = { picking = i }
//                            ) {
//                                Text(
//                                    text = SimpleDateFormat(
//                                        "EEEE\nyyyy-MM-dd\nHH:mm:ss", Locale.getDefault()
//                                    ).format(if (i == 0) selectedRange.first else selectedRange.second),
//                                    fontSize = 12.sp,
//                                    textAlign = TextAlign.Center,
//                                )
//                            }
//                        }
//                    }
//                    pickingButton(0)
//                    pickingButton(1)
//                }
//                Spacer(modifier = Modifier.height(5.dp))
//                Text(
//                    modifier = Modifier.fillMaxWidth(),
//                    text = "Records: $recordsInRangeCount selected / $recordsCount Total",
//                    textAlign = TextAlign.Center,
//                )
//                Spacer(modifier = Modifier.height(5.dp))
//                Button(
//                    modifier = Modifier.align(Alignment.CenterHorizontally),
//                    onClick = { updateRecords() }
//                ) {
//                    Text(stringResource(R.string.load))
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(10.dp))
//
//        AnimatedContent(targetState = recordsInRange == null, label = "recordsNoneToList") {
//            if (it) Text(
//                modifier = Modifier.fillMaxWidth(),
//                text = stringResource(R.string.overview_idle_text),
//                textAlign = TextAlign.Center,
//            ) else RecordsList(records = recordsInRange!!, reversed = true)
//        }
//    }
//}
