package com.galacticai.networkpulse.databse

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.galacticai.networkpulse.databse.models.SpeedRecordEntity

@Database(entities = [SpeedRecordEntity::class], version = 1)
abstract class LocalDatabase : RoomDatabase() {
    companion object {
        private fun getBuilder(context: Context) = Room.databaseBuilder(
            context,
            LocalDatabase::class.java, "local.db"
        )

        fun getDB(context: Context) =
            getBuilder(context).build()

        fun getDBMainThread(context: Context) =
            getBuilder(context).allowMainThreadQueries().build()
    }

    abstract fun speedRecordsDAO(): SpeedRecordsDAO
}
