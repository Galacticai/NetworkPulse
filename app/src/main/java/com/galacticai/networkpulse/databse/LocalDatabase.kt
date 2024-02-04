package com.galacticai.networkpulse.databse

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.galacticai.networkpulse.databse.models.SpeedRecord

@Database(
    entities = [SpeedRecord::class],
    version = 2,
)
abstract class LocalDatabase : RoomDatabase() {
    companion object {
        private val MigrationFrom1To2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `logs` ADD COLUMN `status` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `logs` ADD COLUMN `runtimeMS` INTEGER")
            }
        }

        private fun getBuilder(context: Context) = Room.databaseBuilder(
            context,
            LocalDatabase::class.java, "local.db"
        ).addMigrations(
            MigrationFrom1To2
        )

        fun getDB(context: Context) =
            getBuilder(context).build()

        fun getDBMainThread(context: Context) =
            getBuilder(context).allowMainThreadQueries().build()
    }

    abstract fun speedRecordsDAO(): SpeedRecordsDAO
}
