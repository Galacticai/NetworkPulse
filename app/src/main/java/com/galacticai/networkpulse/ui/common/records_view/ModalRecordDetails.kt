package com.galacticai.networkpulse.ui.common.records_view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.databse.LocalDatabase
import com.galacticai.networkpulse.databse.models.SpeedRecord
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
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
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
            var confirmingDelete by remember { mutableStateOf(false) }
            AnimatedContent(targetState = confirmingDelete, label = "DeleteConfirmationAnimation",
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                }) {
                Row {
                    ElevatedButton(
                        onClick = { confirmingDelete = !confirmingDelete },
                        elevation = ButtonDefaults.buttonElevation((if (it) 5 else 0).dp),
                    ) {
                        Text(stringResource(if (it) R.string.cancel else R.string.delete))
                    }
                    if (it) {
                        Spacer(modifier = Modifier.width(10.dp))
                        ElevatedButton(
                            onClick = {
                                LocalDatabase.getDBMainThread(context)
                                    .speedRecordsDAO()
                                    .delete(record)
                                confirmingDelete = false
                                onRecordDeleted?.invoke()
                                onDismissRequest()
                            },
                            elevation = ButtonDefaults.buttonElevation(5.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.errorContainer),
                                contentColor = colorResource(R.color.onErrorContainer),
                            )
                        ) {
                            Text(stringResource(R.string.are_you_sure))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            ElevatedButton(
                onClick = onDismissRequest,
                elevation = ButtonDefaults.buttonElevation(0.dp),
            ) {
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
        record = SpeedRecord.timeout(System.currentTimeMillis(), 2800)
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
        record = SpeedRecord.error(System.currentTimeMillis(), 2800)
    ) {}
    runBlocking { state.show() }
}