package com.galacticai.networkpulse.ui.common.records_view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.databse.models.SpeedRecord
import com.galacticai.networkpulse.databse.models.SpeedRecordUtils
import com.galacticai.networkpulse.ui.MainActivity
import com.galacticai.networkpulse.ui.common.ConfirmationButtons
import com.galacticai.networkpulse.ui.util.Consistent
import kotlinx.coroutines.runBlocking


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalRecordDetails(
    record: SpeedRecord,
    state: SheetState,
    onRecordDeleted: (() -> Unit)? = null,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    ModalBottomSheet(
        sheetState = state,
        dragHandle = null,
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(topStart = Consistent.radius, topEnd = Consistent.radius),
    ) {
        RecordDetails(
            modifier = Modifier.padding(
                top = 15.dp, bottom = 5.dp,
                start = 20.dp, end = 20.dp,
            ),
            headBodySpacing = 10.dp,
            record = record
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 5.dp, bottom = 25.dp,
                    start = 20.dp, end = 20.dp,
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            ConfirmationButtons(stringResource(R.string.delete)) {
                (context as MainActivity).viewModel.dao.delete(record)
                onRecordDeleted?.invoke()
                onDismissRequest()
            }

            Spacer(modifier = Modifier.width(10.dp))
            Button(onClick = onDismissRequest) {
                Text(stringResource(R.string.ok))
            }
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
        record = SpeedRecordUtils.success(System.currentTimeMillis(), 2800, 43.02132f, 210.91234f)
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
        record = SpeedRecordUtils.timeout(System.currentTimeMillis(), 2800)
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
        record = SpeedRecordUtils.error(System.currentTimeMillis(), 2800)
    ) {}
    runBlocking { state.show() }
}