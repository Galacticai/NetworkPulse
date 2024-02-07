package com.galacticai.networkpulse.ui.main.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.ui.ExpandableItem
import com.galacticai.networkpulse.common.ui.dialogs.DateTimePicker
import com.galacticai.networkpulse.databse.LocalDatabase
import com.galacticai.networkpulse.databse.models.SpeedRecord
import com.galacticai.networkpulse.ui.MainActivity
import com.galacticai.networkpulse.ui.common.AppTitle
import com.galacticai.networkpulse.ui.common.TopBar
import com.galacticai.networkpulse.ui.main.RecordsList
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(mainActivity: MainActivity) {
    val dao = LocalDatabase
        .getDBMainThread(LocalContext.current)
        .speedRecordsDAO()

    var allowedRange by remember { mutableStateOf(0L to 0L) }
    var selectedRange by remember { mutableStateOf(0L to 0L) }
    var records by remember { mutableStateOf<List<SpeedRecord>?>(null) }

    LaunchedEffect(Unit) {
        allowedRange = dao.getOldestTime() to dao.getNewestTime()
        selectedRange = allowedRange
    }

    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
        AppTitle(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 10.dp, bottom = 20.dp,
                    start = 10.dp, end = 10.dp,
                )
        )
        ExpandableItem(
            title = stringResource(R.string.time_range),
            expandedFontWeight = FontWeight.W500,
        ) { itemPadding ->
            Column(
                modifier = Modifier
                    .padding(itemPadding)
                    .fillMaxWidth()
            ) {
                Row {
                    var picking by remember { mutableIntStateOf(-1) }
                    if (picking >= 0) {
                        ModalBottomSheet(
                            containerColor = colorResource(R.color.background),
                            onDismissRequest = { picking = -1 },
                            shape = RoundedCornerShape(20.dp),
                        ) {
                            DateTimePicker(
                                initialValue =
                                if (picking == 0) selectedRange.first
                                else selectedRange.second,
                                cancelText = { Text(text = stringResource(R.string.cancel)) },
                                confirmText = { Text(text = stringResource(R.string.select)) },
                                onDismissRequest = { picking = -1 },
                                onPicked = {
                                    selectedRange =
                                        if (picking == 0) it to selectedRange.second
                                        else selectedRange.first to it
                                }
                            )
                        }
                    }

                    @Composable
                    fun pickingButton(i: Int) {
                        if (i != 0 && i != 1) throw IndexOutOfBoundsException()
                        Column(
                            modifier = Modifier.weight(.5f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(if (i == 0) R.string.from else R.string.to),
                                textAlign = TextAlign.Center,
                            )
                            TextButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { picking = i }
                            ) {
                                Text(
                                    text = SimpleDateFormat(
                                        "EEEE yyyy-MM-dd HH:mm:ss", Locale.getDefault()
                                    ).format(if (i == 0) selectedRange.first else selectedRange.second),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                    pickingButton(0)
                    pickingButton(1)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    modifier = Modifier.align(Alignment.End),
                    onClick = {
                        records = dao.getBetween(selectedRange.first, selectedRange.second)
                    }
                ) {
                    Text("Apply range")
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        AnimatedVisibility(visible = records != null) {
            RecordsList(records = records ?: listOf())
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun p() {
    var pickerRange by remember { mutableStateOf(0L to System.currentTimeMillis()) }

    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
        TopBar(stringResource(R.string.overview)) {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = stringResource(R.string.refresh)
                )
            }
        }

        ExpandableItem(
            title = "Time range",
            expandedFontWeight = FontWeight.W500,
        ) { itemPadding ->
            Row(modifier = Modifier.padding(itemPadding)) {
                TextButton(
                    modifier = Modifier.weight(.5f),
                    onClick = {

                    }) {
                    Text("From")
                }
                TextButton(
                    modifier = Modifier.weight(.5f),
                    onClick = { }) {
                    Text("To")
                }
            }
        }
    }
}