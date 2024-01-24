package com.galacticai.networkpulse.databse.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.galacticai.networkpulse.models.speed_record.TimedSpeedRecord

@Entity(tableName = "logs")
data class SpeedRecordEntity(
    @PrimaryKey
    @ColumnInfo(name = "time")
    val time: Long,

    @ColumnInfo(name = "up")
    val up: Float?,
    @ColumnInfo(name = "down")
    val down: Float?

) {
    @Ignore
    fun toModel() =
        TimedSpeedRecord(time, up ?: 0f, down ?: 0f)

    companion object {
        @Ignore
        fun fromModel(model: TimedSpeedRecord) =
            SpeedRecordEntity(model.time, model.up, model.down)
    }
}