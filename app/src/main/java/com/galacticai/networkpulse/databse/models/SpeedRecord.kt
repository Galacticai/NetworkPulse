package com.galacticai.networkpulse.databse.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import okhttp3.Response

private const val tableName_ = "logs"

@Entity(tableName = tableName_)
data class SpeedRecord(
    @PrimaryKey
    @ColumnInfo(name = timeColumn)
    val time: Long,
    @ColumnInfo(name = statusColumn)
    val status: Int,
    @ColumnInfo(name = runtimeMSColumn)
    val runtimeMS: Int,
    @ColumnInfo(name = upColumn)
    val up: Float?,
    @ColumnInfo(name = downColumn)
    val down: Float?,
) : Comparable<SpeedRecord> {

    @Ignore
    override fun compareTo(other: SpeedRecord): Int =
        time.compareTo(other.time)

    @get:Ignore
    val isSuccess get() = SpeedRecordStatus.isSuccess(status)

    @get:Ignore
    val isTimeout get() = SpeedRecordStatus.isTimeout(status)

    @get:Ignore
    val isError get() = SpeedRecordStatus.isError(status)

    @get:Ignore
    val isOther get() = !(isSuccess || isTimeout || isError)

    @get:Ignore
    val runtimeInSeconds get() = runtimeMS.toFloat() / 1000

    @get:Ignore
    val downSize get() = (down ?: 0f) * runtimeInSeconds

    @get:Ignore
    val upSize get() = (up ?: 0f) * runtimeInSeconds

    companion object {
        @Ignore
        const val tableName = tableName_

        @Ignore
        const val timeColumn = "time"

        @Ignore
        const val statusColumn = "status"

        @Ignore
        const val runtimeMSColumn = "runtimeMS"

        @Ignore
        const val upColumn = "up"

        @Ignore
        const val downColumn = "down"

        @Ignore
        fun getDownSpeed(response: Response, runtimeMS: Int): Float =
            (response.body?.string()?.length?.toFloat() ?: 0f) / runtimeMS

        @Ignore
        fun success(time: Long, runtimeMS: Int, up: Float, down: Float) =
            SpeedRecord(time, SpeedRecordStatus.Success.toInt(), runtimeMS, up, down)

        @Ignore
        fun timeout(time: Long, runtimeMS: Int) =
            SpeedRecord(time, SpeedRecordStatus.Timeout.toInt(), runtimeMS, null, null)

        @Ignore
        fun error(time: Long, runtimeMS: Int) =
            SpeedRecord(time, SpeedRecordStatus.Error.toInt(), runtimeMS, null, null)

        @Ignore
        fun average(records: List<SpeedRecord>): SpeedRecord {
            var upTotal = 0.0
            var downTotal = 0.0
            var runtimeMSSuccessTotal = 0L
            var runtimeMSFailTotal = 0L
            var success = 0
            var fail = 0
            for (record in records) {
                if (record.isSuccess) {
                    upTotal += record.up!!
                    downTotal += record.down!!
                    runtimeMSSuccessTotal += record.runtimeMS
                    success++
                } else {
                    runtimeMSFailTotal += record.runtimeMS
                    fail++
                }
            }
            val consideredSuccess = success > 0
            val time = records[0].time
            val status: Int
            val runtimeMS: Int
            val up: Float?
            val down: Float?
            if (consideredSuccess) {
                status = SpeedRecordStatus.Success.toInt()
                runtimeMS = (runtimeMSSuccessTotal / success).toInt()
                up = (upTotal / success).toFloat()
                down = (downTotal / success).toFloat()
            } else {
                status = SpeedRecordStatus.Error.toInt()
                runtimeMS = (runtimeMSFailTotal / fail).toInt()
                up = null
                down = null
            }
            return SpeedRecord(time, status, runtimeMS, up, down)
        }
    }
}
