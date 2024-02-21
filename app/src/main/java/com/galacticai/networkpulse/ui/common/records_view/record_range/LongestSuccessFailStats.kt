package com.galacticai.networkpulse.ui.common.records_view.record_range

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.models.records_summary.RecordsSummary


@Composable
fun LongestSuccessStats(summary: RecordsSummary) {
    StatContainer(
        stringResource(R.string.longest_success_streak),
        listOf({
            val fromDate = formatDate(summary.longestSuccessStreak.from)
            val value = summary.longestSuccessStreak.value
            val toDate = formatDate(summary.longestSuccessStreak.to)
            Stat(null, fromDate)
            Stat(
                stringResource(R.string.maximum),
                value,
                colorResource(R.color.success)
            )
            Stat(null, toDate)
        }, {
            val fromDate = formatTime(summary.longestSuccessStreak.from)
            val toDate = formatTime(summary.longestSuccessStreak.to)
            Stat(null, fromDate)
            Filler()
            Stat(null, toDate)
        })
    )
}

@Composable
fun LongestFailStats(summary: RecordsSummary) {
    StatContainer(
        stringResource(R.string.longest_failure_streak),
        listOf({
            val fromDate = formatDate(summary.longestFailStreak.from)
            val value = summary.longestFailStreak.value
            val toDate = formatDate(summary.longestFailStreak.to)
            Stat(stringResource(R.string.from), fromDate)
            Stat(
                stringResource(R.string.maximum),
                value,
                colorResource(R.color.error)
            )
            Stat(stringResource(R.string.to), toDate)
        }, {
            val fromDate = formatTime(summary.longestFailStreak.from)
            val toDate = formatTime(summary.longestFailStreak.to)
            Stat(null, fromDate)
            Filler()
            Stat(null, toDate)
        })
    )
}

