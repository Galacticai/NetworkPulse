package com.galacticai.networkpulse.databse

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.galacticai.networkpulse.databse.models.SpeedRecord
import com.galacticai.networkpulse.models.DayRange
import kotlinx.coroutines.flow.Flow

@Dao
interface SpeedRecordsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg records: SpeedRecord)

    @Update
    fun update(vararg record: SpeedRecord)

    @Query("select MIN(time) from logs")
    fun getOldestTime(): Long

    @Query("select MAX(time) from logs")
    fun getNewestTime(): Flow<Long>

    @Query("select * from logs where time=:time")
    fun get(time: Long): SpeedRecord

    /** Get the ranges of days (first and last time recorded per day as [DayRange]) */
    @Query(
        "select" +
                " min(time) as first," +
                " max(time) as last" +
                " from logs" +
                " group by cast((time/86400000) as int)" //? 24 * 60 * 60 * 1000
    )
    fun getDays(): Flow<List<DayRange>>


    @Query("select count(*) from logs")
    fun countAll(): Flow<Int>

    @Query(
        "select count(*) from logs" +
                " where time between :from and :to"
    )
    fun countBetween(from: Long, to: Long): Flow<Int>

    /** Nearest time after to the provided [time] or null */
    @Query(
        "select min(time) from logs" +
                " where time > :time"
    )
    fun getNearestNext(time: Long): Long?

    /** Nearest time prior to the provided [time] or null */
    @Query(
        "select max(time) from logs" +
                " where time < :time"
    )
    fun getNearestPrior(time: Long): Long?

    @Query(
        "select * from logs" +
                " where time between :from and :to"
    )
    fun getBetween(from: Long, to: Long): Flow<List<SpeedRecord>>

    @Query(
        "select * from logs " +
                " where time between :from and :to" +
                " and up>:up" +
                " and down>:down"
    )
    fun getBetweenFasterThan(
        from: Long, to: Long,
        up: Float = 0f, down: Float = 0f
    ): List<SpeedRecord>

    @Query(
        "select * from logs" +
                " where time" +
                " between :from and :to" +
                " and up<:up" +
                " and down<:down"
    )
    fun getBetweenSlowerThan(
        from: Long, to: Long,
        up: Float = Float.MAX_VALUE, down: Float = Float.MAX_VALUE
    ): List<SpeedRecord>

    @Delete
    fun delete(vararg record: SpeedRecord)

    @Query("delete from logs where time IN(:times)")
    fun delete(vararg times: Long)

    @Query("delete from logs where time<:time")
    fun deleteOlderThan(time: Long)
}
