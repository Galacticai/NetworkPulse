package com.galacticai.networkpulse.databse.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import okhttp3.Response

/** A snapshot of the network speed and status at a given time
 * @param time unix timestamp in milliseconds (in UTC)
 * @param status result status
 * @param runtimeMS duration (in milliseconds) it took to get this snapshot
 * @param up upload speed in bytes per second or null if status is not successful
 * @param down download speed in bytes per second or null if status is not successful */
@Entity(tableName = SpeedRecordUtils.tableName)
data class SpeedRecord(
    @PrimaryKey
    @ColumnInfo(name = SpeedRecordUtils.timeColumn)
    val time: Long,
    @ColumnInfo(name = SpeedRecordUtils.statusColumn)
    val status: Int,
    @ColumnInfo(name = SpeedRecordUtils.runtimeMSColumn)
    val runtimeMS: Int,
    @ColumnInfo(name = SpeedRecordUtils.upColumn)
    val up: Float?,
    @ColumnInfo(name = SpeedRecordUtils.downColumn)
    val down: Float?,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Float::class.java.classLoader) as? Float,
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(time)
        parcel.writeInt(status)
        parcel.writeInt(runtimeMS)
        parcel.writeValue(up)
        parcel.writeValue(down)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<SpeedRecord> {
        override fun createFromParcel(parcel: Parcel): SpeedRecord =

            SpeedRecord(parcel)

        override fun newArray(size: Int): Array<SpeedRecord?> =
            arrayOfNulls(size)
    }
}

object SpeedRecordUtils {
    const val tableName = "logs"
    const val timeColumn = "time"
    const val statusColumn = "status"
    const val runtimeMSColumn = "runtimeMS"
    const val upColumn = "up"
    const val downColumn = "down"

    val SpeedRecord.isSuccess get() = SpeedRecordStatus.isSuccess(status)
    val SpeedRecord.isTimeout get() = SpeedRecordStatus.isTimeout(status)
    val SpeedRecord.isError get() = SpeedRecordStatus.isError(status)
    val SpeedRecord.isOther get() = !(isSuccess || isTimeout || isError)
    val SpeedRecord.runtimeInSeconds get() = runtimeMS.toFloat() / 1000
    val SpeedRecord.downSize get() = (down ?: 0f) * runtimeInSeconds
    val SpeedRecord.upSize get() = (up ?: 0f) * runtimeInSeconds
    fun getDownSpeed(response: Response, runtimeMS: Int): Float =
        (response.body?.string()?.length?.toFloat() ?: 0f) / runtimeMS

    fun success(time: Long, runtimeMS: Int, up: Float, down: Float) =
        SpeedRecord(time, SpeedRecordStatus.Success.toInt(), runtimeMS, up, down)

    fun timeout(time: Long, runtimeMS: Int) =
        SpeedRecord(time, SpeedRecordStatus.Timeout.toInt(), runtimeMS, null, null)

    fun error(time: Long, runtimeMS: Int) =
        SpeedRecord(time, SpeedRecordStatus.Error.toInt(), runtimeMS, null, null)

    val List<SpeedRecord>.downMax get() = maxOfOrNull { it.down ?: 0f } ?: 0f
    val List<SpeedRecord>.upMax get() = maxOfOrNull { it.up ?: 0f } ?: 0f

    fun Iterable<SpeedRecord>.sorted() = toSortedSet(compareBy { it.time })
    fun List<SpeedRecord>.average(): SpeedRecord {
        var upTotal = 0.0
        var downTotal = 0.0
        var runtimeMSSuccessTotal = 0L
        var runtimeMSFailTotal = 0L
        var success = 0
        var fail = 0
        for (record in this) {
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
        val time = first().time
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