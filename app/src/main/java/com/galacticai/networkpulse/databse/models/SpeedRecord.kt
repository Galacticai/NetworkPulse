package com.galacticai.networkpulse.databse.models

import androidx.room.ColumnInfo
import androidx.room.Entity
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
    val runtimeMS: Int? = null,
    @ColumnInfo(name = upColumn)
    val up: Float? = null,
    @ColumnInfo(name = downColumn)
    val down: Float? = null,
) {
    val isSuccess get() = Status.isSuccess(status)
    val isTimeout get() = Status.isTimeout(status)
    val isError get() = Status.isError(status)
    val isOther get() = !(isSuccess || isTimeout || isError)

    companion object {
        const val tableName = tableName_
        const val timeColumn = "time"
        const val statusColumn = "status"
        const val runtimeMSColumn = "runtimeMS"
        const val upColumn = "up"
        const val downColumn = "down"

        fun getDownSpeed(response: Response, runtimeMS: Int): Float =
            (response.body?.string()?.length?.toFloat() ?: 0f) / runtimeMS
    }

    enum class Status {
        Success, Timeout, Error;

        fun toInt() = ordinal
        override fun toString() = toInt().toString()

        companion object {
            fun isSuccess(i: Int) = i == Success.toInt()
            fun isTimeout(i: Int) = i == Timeout.toInt()
            fun isError(i: Int) = i == Error.toInt()
            fun isOther(i: Int) = !(isSuccess(i) || isTimeout(i) || isError(i))
        }
    }
}