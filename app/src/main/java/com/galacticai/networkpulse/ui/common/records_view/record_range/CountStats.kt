package com.galacticai.networkpulse.ui.common.records_view.record_range

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.models.records_summary.RecordsSummary

@Composable
fun CountStats(summary: RecordsSummary) {
    StatContainer(rowsContent = listOf {
        Stat(
            stringResource(R.string.total),
            summary.allCount,
            colorResource(R.color.primary)
        )
        Stat(
            stringResource(R.string.success),
            summary.successCount,
            colorResource(R.color.success)
        )
        Stat(
            stringResource(R.string.fail),
            summary.failCount,
            colorResource(R.color.error)
        )
    })
}