package com.galacticai.networkpulse.databse

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.galacticai.networkpulse.databse.models.SpeedRecordEntity

@Dao
interface SpeedRecordsDAO {
    companion object {
        private const val SelectAll = "SELECT * FROM logs"
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg records: SpeedRecordEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnore(vararg records: SpeedRecordEntity)

    @Update
    fun update(vararg record: SpeedRecordEntity)


    @Query(SelectAll)
    fun getAll(): List<SpeedRecordEntity>


    @Query("$SelectAll WHERE time = :time")
    fun get(time: Long): SpeedRecordEntity?

    @Query("$SelectAll WHERE time > :time")
    fun getAfter(time: Long): List<SpeedRecordEntity>

    @Query("$SelectAll WHERE time < :time")
    fun getBefore(time: Long): List<SpeedRecordEntity>

    @Query("$SelectAll WHERE time BETWEEN :from AND :to")
    fun getBetween(from: Long, to: Long): List<SpeedRecordEntity>

    @Query("$SelectAll WHERE up > :up")
    fun getFasterUpload(up: Float): List<SpeedRecordEntity>

    @Query("$SelectAll WHERE up < :up")
    fun getSlowerUpload(up: Float): List<SpeedRecordEntity>

    @Query("$SelectAll WHERE up BETWEEN :from AND :to")
    fun getBetweenUpload(from: Float, to: Float): List<SpeedRecordEntity>

    @Query("$SelectAll WHERE down > :down")
    fun getFasterDownload(down: Float): List<SpeedRecordEntity>

    @Query("$SelectAll WHERE down < :down")
    fun getSlowerDownload(down: Float): List<SpeedRecordEntity>

    @Query("$SelectAll WHERE down BETWEEN :from AND :to")
    fun getBetweenDownload(from: Float, to: Float): List<SpeedRecordEntity>

    @Delete
    fun delete(vararg record: SpeedRecordEntity)

    @Query("DELETE FROM logs")
    fun deleteAll()

    @Query("DELETE FROM logs WHERE time < :time")
    fun deleteOlderThan(time: Long)
}
