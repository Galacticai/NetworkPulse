package com.galacticai.networkpulse.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.ui.CustomTabRow
import com.guru.fontawesomecomposelib.FaIcon
import com.guru.fontawesomecomposelib.FaIconType
import com.guru.fontawesomecomposelib.FaIcons
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.math.min


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DateTimePicker(
    initialValue: Long,
    allowedRange: ClosedRange<Long> = 0L..Long.MAX_VALUE,
    confirmText: @Composable () -> Unit,
    cancelText: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    onPicked: (Long) -> Unit,
) {
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxWidth()) {
        @Composable
        fun tabContent(selected: Boolean, icon: FaIconType, title: String) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FaIcon(icon)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    modifier = Modifier.weight(1f),
                    text = title,
                    textAlign = TextAlign.Center,
                    color = colorResource(
                        if (selected) R.color.onBackground
                        else R.color.onPrimaryContainer
                    )
                )
            }
        }
        CustomTabRow(
            insetPadding = 5.dp,
            containerColor = colorResource(R.color.primaryContainer),
            selectedContainerColor = colorResource(R.color.background),
            items = listOf<@Composable (Boolean) -> Unit>(
                { tabContent(it, FaIcons.Clock, stringResource(R.string.date)) },
                { tabContent(it, FaIcons.Calendar, stringResource(R.string.time)) },
            )
        ) {
            coroutineScope.launch { pagerState.scrollToPage(it) }
        }
        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            state = pagerState
        ) {
            when (it) {
                1 -> TimePicker(state = timePickerState)
                0 -> DatePicker(
                    state = datePickerState,
                    dateValidator = { time ->
                        allowedRange.start <= time && time <= allowedRange.endInclusive
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onDismissRequest) {
                cancelText()
            }
            Button(onClick = {
                val unit = TimeUnit.MILLISECONDS
                val maxHour = (unit.toHours(allowedRange.endInclusive) % 24).toInt()
                val maxMinute = (unit.toMinutes(allowedRange.endInclusive) % 60).toInt()

                val date = datePickerState.selectedDateMillis ?: initialValue
                val hour = min(timePickerState.hour, maxHour)
                val minute = min(timePickerState.minute, maxMinute)

                val calendar = Calendar.getInstance().apply {
                    timeInMillis = date
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                }
                val timestamp = calendar.timeInMillis
                onPicked(timestamp)
                onDismissRequest()
            }) {
                confirmText()
            }
        }
    }
}
