package com.galacticai.networkpulse.databse

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.galacticai.networkpulse.databse.models.SpeedRecord

@Dao
interface SpeedRecordsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg records: SpeedRecord)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnore(vararg records: SpeedRecord)

    @Update
    fun update(vararg record: SpeedRecord)

    @Query("SELECT MIN(time) FROM logs")
    fun getOldestTime(): Long

    @Query("SELECT MAX(time) FROM logs")
    fun getNewestTime(): Long

    @Query("SELECT * FROM logs WHERE time=:time")
    fun get(time: Long): SpeedRecord

    @Query("SELECT * FROM logs")
    fun getAll(): List<SpeedRecord>

    @Query("SELECT COUNT(*) FROM logs")
    fun countAll(): Int

    @Query("SELECT COUNT(*) FROM logs WHERE time BETWEEN :from AND :to")
    fun countBetween(from: Long, to: Long): Int

    /** Nearest time after to the provided [time] or null */
    @Query("SELECT MIN(time) FROM logs WHERE time > :time")
    fun getNearestNext(time: Long): Long?

    /** Nearest time prior to the provided [time] or null */
    @Query("SELECT MAX(time) FROM logs WHERE time < :time")
    fun getNearestPrior(time: Long): Long?

    @Query("SELECT * FROM logs WHERE time BETWEEN :from AND :to")
    fun getBetween(from: Long, to: Long): List<SpeedRecord>

    @Query("SELECT * FROM logs WHERE time BETWEEN :from AND :to AND up>:up AND down>:down")
    fun getBetweenFasterThan(
        from: Long, to: Long,
        up: Float = 0f, down: Float = 0f
    ): List<SpeedRecord>

    @Query("SELECT * FROM logs WHERE time BETWEEN :from AND :to AND up<:up AND down<:down")
    fun getBetweenSlowerThan(
        from: Long, to: Long,
        up: Float = Float.MAX_VALUE, down: Float = Float.MAX_VALUE
    ): List<SpeedRecord>

    @Delete
    fun delete(vararg record: SpeedRecord)

    @Query("DELETE FROM logs WHERE time IN (:times)")
    fun delete(vararg times: Long)

    @Query("DELETE FROM logs WHERE time < :time")
    fun deleteOlderThan(time: Long)

    //    @Query("$SelectAll WHERE ${SpeedRecord.statusColumn} = 1")
    //    fun getAllSuccessful(): List<SpeedRecord>
    //    @Query("$SelectAll WHERE ${SpeedRecord.statusColumn} = 2")
    //    fun getAllTimeout(): List<SpeedRecord>
    //    @Query("$SelectAll WHERE ${SpeedRecord.statusColumn} = 3")
    //    fun getAllError(): List<SpeedRecord>

    //    @Query("$SelectAll WHERE ${SpeedRecord.timeColumn} = :time")
    //    fun get(time: Long): SpeedRecord?
    //    @Query("$SelectAll WHERE ${SpeedRecord.timeColumn} > :time")
    //    fun getAfter(time: Long): List<SpeedRecord>
    //    @Query("$SelectAll WHERE ${SpeedRecord.timeColumn} < :time")
    //    fun getBefore(time: Long): List<SpeedRecord>


    //    @Query("$SelectAll WHERE ${SpeedRecord.upColumn} > :up")
    //    fun getFasterUpload(up: Float): List<SpeedRecord>
    //    @Query("$SelectAll WHERE ${SpeedRecord.upColumn} < :up")
    //    fun getSlowerUpload(up: Float): List<SpeedRecord>
    //    @Query("$SelectAll WHERE ${SpeedRecord.upColumn} BETWEEN :from AND :to")
    //    fun getBetweenUpload(from: Float, to: Float): List<SpeedRecord>

    //    @Query("$SelectAll WHERE ${SpeedRecord.downColumn} > :down")
    //    fun getFasterDownload(down: Float): List<SpeedRecord>
    //    @Query("$SelectAll WHERE ${SpeedRecord.downColumn} < :down")
    //    fun getSlowerDownload(down: Float): List<SpeedRecord>
    //    @Query("$SelectAll WHERE ${SpeedRecord.downColumn} BETWEEN :from AND :to")
    //    fun getBetweenDownload(from: Float, to: Float): List<SpeedRecord>
}
