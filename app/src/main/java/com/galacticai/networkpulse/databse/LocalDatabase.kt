package com.galacticai.networkpulse.databse

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.galacticai.networkpulse.databse.models.SpeedRecord
import java.time.ZoneId
import java.time.ZonedDateTime

@Database(
    entities = [SpeedRecord::class],
    version = 5,
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun speedRecordsDAO(): SpeedRecordsDAO

    companion object {
        @Volatile
        private var instance: LocalDatabase? = null

        private fun getBuilder(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            LocalDatabase::class.java, "local.db"
        ).addMigrations(
            Migrations.From1To2_StatusRuntimeColumns,
            Migrations.From2To3_NotNullStatus,
            Migrations.From3To4_SupportStatus0,
            Migrations.From4To5_TimeToUTC,
        )

        fun getDB(context: Context): LocalDatabase {
            return instance ?: synchronized(this) {
                val i = getBuilder(context).build()
                instance = i
                instance!!
            }
            //fun getDBMainThread(context: Context) = getBuilder(context).allowMainThreadQueries().build()
        }

        object Migrations {
            val From1To2_StatusRuntimeColumns = object : Migration(1, 2) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    // add status column
                    //? this should not have not null but i'm too afraid to change
                    db.execSQL("ALTER TABLE logs ADD COLUMN status INTEGER NOT NULL DEFAULT 0")
                    // add runtimeMS column
                    db.execSQL("ALTER TABLE logs ADD COLUMN runtimeMS INTEGER")
                }
            }
            val From2To3_NotNullStatus = object : Migration(2, 3) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    // backup and add replace null with 0 in status and runtimeMS
                    db.execSQL("CREATE TABLE logs_backup AS SELECT time, COALESCE(status, 0) AS status, COALESCE(runtimeMS, 0) AS runtimeMS, up, down FROM logs");
                    // remove old table
                    db.execSQL("DROP TABLE IF EXISTS logs");
                    // create new table with new status constraints
                    db.execSQL("CREATE TABLE logs(time INTEGER NOT NULL PRIMARY KEY, status INTEGER NOT NULL DEFAULT 0, runtimeMS INTEGER NOT NULL DEFAULT 0, up REAL, down REAL)");
                    // copy backup to new table
                    db.execSQL("INSERT INTO logs SELECT * FROM logs_backup");
                    // drop backup table
                    db.execSQL("DROP TABLE logs_backup");
                }
            }
            val From3To4_SupportStatus0 = object : Migration(3, 4) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("UPDATE logs SET status = status+1 WHERE down IS NOT NULL AND up IS NOT NULL")
                }
            }
            val From4To5_TimeToUTC = object : Migration(4, 5) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    val offsetMS = ZonedDateTime
                        .now(ZoneId.systemDefault())
                        .offset.totalSeconds * 1000
                    db.execSQL("UPDATE logs SET time=time-$offsetMS")
                }
            }
        }
    }
}
