package com.galacticai.networkpulse.models

import com.galacticai.networkpulse.databse.models.SpeedRecord
import kotlin.math.max

data class SpeedRecordSummary(
    val upAverage: Float,
    val downAverage: Float,
    val runtimeMSSuccessAverage: Long,
    val runtimeMSTimeoutAverage: Long,
    val successCount: Int,
    val timeoutCount: Int,
    val errorCount: Int,
    val otherCount: Int,
    /** [timeoutCount] + [errorCount] */
    val failedCount: Int,
    /** [successCount] + [failedCount] */
    val allCount: Int,
    /** The longest streak of successful records in MS */
    val longestSuccessStreakMS: Long,
    /** The longest streak of failed records in MS */
    val longestFailStreakMS: Long,
) {
    companion object {
        fun summarize(records: List<SpeedRecord>): SpeedRecordSummary {
            var upTotal = 0f
            var downTotal = 0f
            var runtimeMSSuccessTotal = 0L
            var runtimeMSTimeoutTotal = 0L
            var successCount = 0
            var timeoutCount = 0
            var errorCount = 0
            var otherCount = 0
            var longestSuccessStreakMS = 0L
            var currentSuccessStreakMS = 0L
            var longestFailStreakMS = 0L
            var currentFailStreakMS = 0L

            fun updateSuccessStreak(runtimeMS: Int) {
                currentSuccessStreakMS += runtimeMS
                longestSuccessStreakMS = max(longestSuccessStreakMS, currentSuccessStreakMS)
            }

            fun updateFailStreak(runtimeMS: Int) {
                currentFailStreakMS += runtimeMS
                longestFailStreakMS = max(longestFailStreakMS, currentFailStreakMS)
            }

            for (record in records) {
                if (record.isSuccess) {
                    successCount++
                    upTotal += record.up!!
                    downTotal += record.down!!
                    runtimeMSSuccessTotal += record.runtimeMS
                    updateSuccessStreak(record.runtimeMS)
                    currentFailStreakMS = 0L
                } else if (record.isTimeout) {
                    timeoutCount++
                    runtimeMSTimeoutTotal += record.runtimeMS
                    updateFailStreak(record.runtimeMS)
                    currentSuccessStreakMS = 0L
                } else if (record.isError) {
                    errorCount++
                    updateFailStreak(record.runtimeMS)
                    currentSuccessStreakMS = 0L
                } else {
                    otherCount++
                    updateFailStreak(record.runtimeMS)
                    currentSuccessStreakMS = 0L
                }
            }
            val failedCount = timeoutCount + errorCount
            val allCount = successCount + failedCount
            val upAverage: Float
            val downAverage: Float
            val runtimeMSSuccessAverage: Long
            val runtimeMSTimeoutAverage: Long
            if (successCount > 0) {
                upAverage = upTotal / successCount
                downAverage = downTotal / successCount
                runtimeMSSuccessAverage = runtimeMSSuccessTotal / successCount
                runtimeMSTimeoutAverage = runtimeMSTimeoutTotal / successCount
            } else {
                upAverage = 0f
                downAverage = 0f
                runtimeMSSuccessAverage = 0
                runtimeMSTimeoutAverage = 0
            }

            return SpeedRecordSummary(
                upAverage,
                downAverage,
                runtimeMSSuccessAverage,
                runtimeMSTimeoutAverage,
                successCount,
                timeoutCount,
                errorCount,
                otherCount,
                failedCount,
                allCount,
                longestSuccessStreakMS,
                longestFailStreakMS,
            )
        }
    }
}