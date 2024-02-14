package com.galacticai.networkpulse.ui.common.records_view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.databse.models.SpeedRecord
import kotlinx.coroutines.runBlocking


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalRecordDetails(record: SpeedRecord, state: SheetState, onDismissRequest: () -> Unit) {
    ModalBottomSheet(
        sheetState = state,
        dragHandle = null,
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(20.dp),
    ) {
        RecordDetails(
            modifier = Modifier.padding(
                top = 15.dp, bottom = 5.dp,
                start = 20.dp, end = 20.dp,
            ),
            headBodySpacing = 10.dp,
            record = record
        )
        TextButton(
            modifier = Modifier
                .align(Alignment.End)
                .padding(
                    top = 5.dp, bottom = 25.dp,
                    start = 20.dp, end = 20.dp,
                ),
            onClick = onDismissRequest
        ) {
            Text(stringResource(R.string.ok))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun ModalRecordDetailsPreview1() {
    val state = rememberModalBottomSheetState()
    ModalRecordDetails(
        state = state,
        record = SpeedRecord.Success(System.currentTimeMillis(), 2800, 43.02132f, 210.91234f)
    ) {}
    runBlocking { state.show() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun ModalRecordDetailsPreview2() {
    val state = rememberModalBottomSheetState()
    ModalRecordDetails(
        state = state,
        record = SpeedRecord.Timeout(System.currentTimeMillis(), 2800)
    ) {}
    runBlocking { state.show() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun ModalRecordDetailsPreview3() {
    val state = rememberModalBottomSheetState()
    ModalRecordDetails(
        state = state,
        record = SpeedRecord.Error(System.currentTimeMillis(), 2800)
    ) {}
    runBlocking { state.show() }
}