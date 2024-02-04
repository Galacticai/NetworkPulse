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
    companion object {
        private const val SelectAll = "SELECT * FROM ${SpeedRecord.tableName}"
        private const val DeleteAll = "DELETE FROM ${SpeedRecord.tableName}"
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg records: SpeedRecord)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnore(vararg records: SpeedRecord)

    @Update
    fun update(vararg record: SpeedRecord)


    @Query(SelectAll)
    fun getAll(): List<SpeedRecord>

    @Query("$SelectAll WHERE ${SpeedRecord.statusColumn} = 'Success'")
    fun getAllSuccessful(): List<SpeedRecord>

    @Query("$SelectAll WHERE ${SpeedRecord.timeColumn} = :time")
    fun get(time: Long): SpeedRecord?

    @Query("$SelectAll WHERE ${SpeedRecord.timeColumn} > :time")
    fun getAfter(time: Long): List<SpeedRecord>

    @Query("$SelectAll WHERE ${SpeedRecord.timeColumn} < :time")
    fun getBefore(time: Long): List<SpeedRecord>

    @Query("$SelectAll WHERE ${SpeedRecord.timeColumn} BETWEEN :from AND :to")
    fun getBetween(from: Long, to: Long): List<SpeedRecord>

    @Query("$SelectAll WHERE ${SpeedRecord.upColumn} > :up")
    fun getFasterUpload(up: Float): List<SpeedRecord>

    @Query("$SelectAll WHERE ${SpeedRecord.upColumn} < :up")
    fun getSlowerUpload(up: Float): List<SpeedRecord>

    @Query("$SelectAll WHERE ${SpeedRecord.upColumn} BETWEEN :from AND :to")
    fun getBetweenUpload(from: Float, to: Float): List<SpeedRecord>

    @Query("$SelectAll WHERE ${SpeedRecord.downColumn} > :down")
    fun getFasterDownload(down: Float): List<SpeedRecord>

    @Query("$SelectAll WHERE ${SpeedRecord.downColumn} < :down")
    fun getSlowerDownload(down: Float): List<SpeedRecord>

    @Query("$SelectAll WHERE ${SpeedRecord.downColumn} BETWEEN :from AND :to")
    fun getBetweenDownload(from: Float, to: Float): List<SpeedRecord>

    @Delete
    fun delete(vararg record: SpeedRecord)

    @Query(DeleteAll)
    fun deleteAll()

    @Query("$DeleteAll WHERE time < :time")
    fun deleteOlderThan(time: Long)
}
