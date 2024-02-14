package com.galacticai.networkpulse.databse.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import okhttp3.Response

private const val tableName_ = "logs"
fun List<SpeedRecord>.toMap() = associateBy { it.time }

@Entity(tableName = tableName_)
open class SpeedRecord(
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
) {
    val isSuccess get() = Status.isSuccess(status)
    val isNotSuccess get() = !isSuccess
    val isTimeout get() = Status.isTimeout(status)
    val isError get() = Status.isError(status)
    val isOther get() = !(isSuccess || isTimeout || isError)

    val runtimeInSeconds get() = runtimeMS.toFloat() / 1000
    val downSize get() = (down ?: 0f) * runtimeInSeconds
    val upSize get() = (up ?: 0f) * runtimeInSeconds

    class Success(time: Long, runtimeMS: Int, up: Float, down: Float) :
        SpeedRecord(time, Status.Success.toInt(), runtimeMS, up, down)

    class Timeout(time: Long, runtimeMS: Int) :
        SpeedRecord(time, Status.Timeout.toInt(), runtimeMS, null, null)

    class Error(time: Long, runtimeMS: Int) :
        SpeedRecord(time, Status.Error.toInt(), runtimeMS, null, null)

    companion object {
        const val tableName = tableName_
        const val timeColumn = "time"
        const val statusColumn = "status"
        const val runtimeMSColumn = "runtimeMS"
        const val upColumn = "up"
        const val downColumn = "down"

        fun getDownSpeed(response: Response, runtimeMS: Int): Float =
            (response.body?.string()?.length?.toFloat() ?: 0f) / runtimeMS

        fun average(records: List<SpeedRecord>): SpeedRecord {
            if (records.isEmpty()) return SpeedRecord(0, 0, 0, 0f, 0f)
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
            val consideredSuccess = success >0
            val time = records[0].time
            val status: Int
            val runtimeMS: Int
            val up: Float?
            val down: Float?
            if (consideredSuccess) {
                status = Status.Success.toInt()
                runtimeMS = (runtimeMSSuccessTotal / success).toInt()
                up = (upTotal / success).toFloat()
                down = (downTotal / success).toFloat()
            } else {
                status = Status.Error.toInt()
                runtimeMS = (runtimeMSFailTotal / fail).toInt()
                up = null
                down = null
            }
            return SpeedRecord(time, status, runtimeMS, up, down)
        }
    }

    enum class Status(val value: Int) {
        Success(1), Timeout(2), Error(3);

        fun toInt() = value
        override fun toString() = toInt().toString()

        companion object {
            fun isSuccess(i: Int) = i == Success.toInt()
            fun isNotSucceess(i: Int) = !isSuccess(i)
            fun isTimeout(i: Int) = i == Timeout.toInt()
            fun isError(i: Int) = i == Error.toInt()
            fun isOther(i: Int) = !(isSuccess(i) || isTimeout(i) || isError(i))
        }
    }
}