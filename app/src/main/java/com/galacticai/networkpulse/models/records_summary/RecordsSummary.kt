package com.galacticai.networkpulse.models.records_summary

import com.galacticai.networkpulse.common.fromUTC
import com.galacticai.networkpulse.databse.models.SpeedRecord
import com.galacticai.networkpulse.databse.models.SpeedRecordUtils.downSize
import com.galacticai.networkpulse.databse.models.SpeedRecordUtils.isSuccess
import com.galacticai.networkpulse.databse.models.SpeedRecordUtils.upSize
import java.util.SortedSet
import kotlin.math.max

data class RecordsSummary(
    val time: Long,
    val allCount: Int,
    val successCount: Int,
    val failCount: Int,
    val up: AverageInfo<Float>,
    val down: AverageInfo<Float>,
    val runtimeMSSuccess: AverageInfo<Int>,
    val runtimeMSFail: AverageInfo<Int>,
    val downTotalSize: Double,
    val upTotalSize: Double,
    val longestSuccessStreak: TimedRangeValue<Int>,
    val longestFailStreak: TimedRangeValue<Int>,
) : Comparable<RecordsSummary> {
    override fun compareTo(other: RecordsSummary): Int =
        time.compareTo(other.time)

    companion object {
        fun ofSummaries(summaries: SortedSet<RecordsSummary>): RecordsSummary {
            val time = summaries.first().time

            var allCount = 0
            var successCount = 0
            var failCount = 0

            var upTotal = 0.0
            var downTotal = 0.0
            var runtimeMSSuccessTotal = 0L
            var runtimeMSFailTotal = 0L

            var downTotalData = 0.0
            var upTotalData = 0.0

            var longestSuccessStreak = 0
            var longestSuccessStreakFrom = 0L
            var longestSuccessStreakTo = 0L

            var longestFailStreak = 0
            var longestFailStreakFrom = 0L
            var longestFailStreakTo = 0L

            for (summary in summaries) {
                allCount += summary.allCount
                successCount += summary.successCount
                failCount += summary.failCount

                upTotal += summary.up.average * summary.successCount
                downTotal += summary.down.average * summary.successCount
                runtimeMSSuccessTotal += summary.runtimeMSSuccess.average.toLong() * summary.successCount
                runtimeMSFailTotal += summary.runtimeMSFail.average.toLong() * summary.failCount

                downTotalData += summary.downTotalSize
                upTotalData += summary.upTotalSize

                // Update longest success streak if necessary
                if (summary.longestSuccessStreak.value > longestSuccessStreak) {
                    longestSuccessStreak = summary.longestSuccessStreak.value
                    longestSuccessStreakFrom = summary.longestSuccessStreak.from
                    longestSuccessStreakTo = summary.longestSuccessStreak.to
                }

                // Update longest fail streak if necessary
                if (summary.longestFailStreak.value > longestFailStreak) {
                    longestFailStreak = summary.longestFailStreak.value
                    longestFailStreakFrom = summary.longestFailStreak.from
                    longestFailStreakTo = summary.longestFailStreak.to
                }
            }

            val up = AverageInfo(
                if (successCount == 0) 0f
                else (upTotal / successCount).toFloat(),
                TimedValue(0f, 0L), TimedValue(0f, 0L)
            )
            val down = AverageInfo(
                if (successCount == 0) 0f
                else (downTotal / successCount).toFloat(),
                TimedValue(0f, 0L), TimedValue(0f, 0L)
            )
            val runtimeMSSuccess = AverageInfo(
                if (successCount == 0) 0
                else (runtimeMSSuccessTotal / successCount).toInt(),
                TimedValue(0, 0L), TimedValue(0, 0L)
            )
            val runtimeMSFail = AverageInfo(
                if (failCount == 0) 0
                else (runtimeMSFailTotal / failCount).toInt(),
                TimedValue(0, 0L), TimedValue(0, 0L)
            )

            // Create TimedRangeValue objects for longest success and fail streaks
            val longestSuccessStreakValue = TimedRangeValue(
                longestSuccessStreak,
                longestSuccessStreakFrom, longestSuccessStreakTo
            )
            val longestFailStreakValue = TimedRangeValue(
                longestFailStreak,
                longestFailStreakFrom, longestFailStreakTo
            )

            return RecordsSummary(
                time.fromUTC(),
                allCount,
                successCount,
                failCount,
                up,
                down,
                runtimeMSSuccess,
                runtimeMSFail,
                downTotalData,
                upTotalData,
                longestSuccessStreakValue,
                longestFailStreakValue
            )
        }

        fun ofRecords(records: SortedSet<SpeedRecord>): RecordsSummary {
            val time = records.first().time

            var successCount = 0
            var failCount = 0

            var upTotal = 0.0
            var downTotal = 0.0
            var runtimeMSSuccessTotal = 0L
            var runtimeMSFailTotal = 0L

            var upMaxTime = 0L
            var upMinTime = 0L
            var upMax = Float.MIN_VALUE
            var upMin = Float.MAX_VALUE

            var downMaxTime = 0L
            var downMinTime = 0L
            var downMax = Float.MIN_VALUE
            var downMin = Float.MAX_VALUE

            var runtimeMSSuccessMaxTime = 0L
            var runtimeMSSuccessMinTime = 0L
            var runtimeMSSuccessMax = Int.MIN_VALUE
            var runtimeMSSuccessMin = Int.MAX_VALUE

            var runtimeMSFailMaxTime = 0L
            var runtimeMSFailMinTime = 0L
            var runtimeMSFailMax = Int.MIN_VALUE
            var runtimeMSFailMin = Int.MAX_VALUE

            var downTotalSize = 0.0
            var upTotalSize = 0.0

            var longestSuccessStreak = 0
            var longestSuccessStreakFrom = Long.MAX_VALUE
            var longestSuccessStreakTo = Long.MIN_VALUE

            var longestSuccessStreakCurrent = 0
            var longestSuccessStreakCurrentFrom = Long.MAX_VALUE
            var longestSuccessStreakCurrentTo = Long.MIN_VALUE

            var longestFailStreak = 0
            var longestFailStreakFrom = Long.MAX_VALUE
            var longestFailStreakTo = Long.MIN_VALUE

            var longestFailStreakCurrent = 0
            var longestFailStreakCurrentFrom = Long.MAX_VALUE
            var longestFailStreakCurrentTo = Long.MIN_VALUE

            for (record in records) {
                if (record.isSuccess) {
                    successCount++

                    upTotal += record.up!!
                    downTotal += record.down!!

                    runtimeMSSuccessTotal += record.runtimeMS
                    runtimeMSFailTotal += record.runtimeMS

                    downTotalSize += record.downSize
                    upTotalSize += record.upSize

                    if (record.up > upMax) {
                        upMaxTime = record.time
                        upMax = record.up
                    }
                    if (record.up < upMin) {
                        upMinTime = record.time
                        upMin = record.up
                    }
                    if (record.down > downMax) {
                        downMaxTime = record.time
                        downMax = record.down
                    }
                    if (record.down < downMin) {
                        downMinTime = record.time
                        downMin = record.down
                    }
                    if (record.runtimeMS > runtimeMSSuccessMax) {
                        runtimeMSSuccessMaxTime = record.time
                        runtimeMSSuccessMax = record.runtimeMS
                    }
                    if (record.runtimeMS < runtimeMSSuccessMin) {
                        runtimeMSSuccessMinTime = record.time
                        runtimeMSSuccessMin = record.runtimeMS
                    }

                    longestFailStreakCurrent = 0
                    longestSuccessStreakCurrent++

                    longestSuccessStreakCurrentTo = record.time
                    if (longestSuccessStreakCurrentTo < longestSuccessStreakCurrentFrom)
                        longestSuccessStreakCurrentFrom = record.time

                    if (longestSuccessStreakCurrent > longestSuccessStreak) {
                        longestSuccessStreak = longestSuccessStreakCurrent
                        longestSuccessStreakFrom = longestSuccessStreakCurrentFrom
                        longestSuccessStreakTo = longestSuccessStreakCurrentTo
                    }

                } else {
                    failCount++

                    if (record.runtimeMS > runtimeMSFailMax) {
                        runtimeMSFailMaxTime = record.time
                        runtimeMSFailMax = record.runtimeMS
                    }
                    if (record.runtimeMS < runtimeMSFailMin) {
                        runtimeMSFailMinTime = record.time
                        runtimeMSFailMin = record.runtimeMS
                    }

                    longestSuccessStreakCurrent = 0
                    longestFailStreakCurrent++

                    longestFailStreakCurrentTo = record.time
                    if (longestFailStreakCurrentTo < longestFailStreakCurrentFrom)
                        longestFailStreakCurrentFrom = record.time

                    if (longestFailStreakCurrent > longestFailStreak) {
                        longestFailStreak = longestFailStreakCurrent
                        longestFailStreakFrom = longestFailStreakCurrentFrom
                        longestFailStreakTo = longestFailStreakCurrentTo
                    }
                }
            }

            val allCount: Int = successCount + failCount

            val up: AverageInfo<Float> = AverageInfo(
                if (successCount == 0) 0f
                else (upTotal / successCount).toFloat(),
                TimedValue(upMax, upMaxTime.fromUTC()),
                TimedValue(upMin, upMinTime.fromUTC()),
            )
            val down: AverageInfo<Float> = AverageInfo(
                if (successCount == 0) 0f
                else (downTotal / successCount).toFloat(),
                TimedValue(downMax, downMaxTime.fromUTC()),
                TimedValue(downMin, downMinTime.fromUTC()),
            )
            val runtimeMSSuccess: AverageInfo<Int> = AverageInfo(
                if (successCount == 0) 0
                else (runtimeMSSuccessTotal / successCount).toInt(),
                TimedValue(runtimeMSSuccessMax, runtimeMSSuccessMaxTime),
                TimedValue(runtimeMSSuccessMin, runtimeMSSuccessMinTime),
            )
            val runtimeMSFail: AverageInfo<Int> = AverageInfo(
                if (failCount == 0) 0
                else (runtimeMSFailTotal / failCount).toInt(),
                TimedValue(runtimeMSFailMax, runtimeMSFailMaxTime),
                TimedValue(runtimeMSFailMin, runtimeMSFailMinTime),
            )

            val longestSuccessStreakValue = TimedRangeValue(
                longestSuccessStreak,
                if (longestSuccessStreakFrom == Long.MAX_VALUE) 0L
                else longestSuccessStreakFrom.fromUTC(),
                max(0L, longestSuccessStreakTo.fromUTC()),
            )
            val longestFailStreakValue = TimedRangeValue(
                longestFailStreak,
                if (longestFailStreakFrom == Long.MAX_VALUE) 0L
                else longestFailStreakFrom.fromUTC(),
                max(0L, longestFailStreakTo.fromUTC()),
            )

            return RecordsSummary(
                time.fromUTC(),
                allCount,
                successCount,
                failCount,
                up,
                down,
                runtimeMSSuccess,
                runtimeMSFail,
                downTotalSize,
                upTotalSize,
                longestSuccessStreakValue,
                longestFailStreakValue,
            )
        }
    }
}
