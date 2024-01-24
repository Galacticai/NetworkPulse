package com.galacticai.networkpulse.databse

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.galacticai.networkpulse.models.speed_record.TimedSpeedRecord

class DB(
    context: Context,
    factory: SQLiteDatabase.CursorFactory? = null

) : SQLiteOpenHelper(context, DatabaseName, factory, DatabaseVersion) {

    companion object {
        private const val DatabaseName = "LocalDB"
        private const val DatabaseVersion = 1
        private const val TableName = "SpeedRecords"
    }

    val database: SQLiteDatabase get() = writableDatabase
    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        val query = "CREATE TABLE IF NOT EXISTS $TableName (" +
                "${Columns.Time} INTEGER PRIMARY KEY, " +
                "${Columns.Upload} REAL," +
                "${Columns.Download} REAL" +
                ")"
        Log.d("DB", query)
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TableName")
        onCreate(db)
    }

    fun get(time: Long): TimedSpeedRecord {
        val cursor = database.rawQuery(
            "SELECT * FROM $TableName WHERE ${Columns.Time} = $time",
            null
        )
        cursor.moveToFirst()
        val record = TimedSpeedRecord(
            cursor.getLong(Columns.Time.indexSQL),
            cursor.getFloat(Columns.Upload.indexSQL),
            cursor.getFloat(Columns.Download.indexSQL),
        )
        cursor.close()
        return record
    }

    // This method is for adding data in our database
    fun add(speedRecord: TimedSpeedRecord) {
        val values = ContentValues().apply {
            put(Columns.Time.nameSQL, speedRecord.time)
            put(Columns.Upload.nameSQL, speedRecord.up)
            put(Columns.Download.nameSQL, speedRecord.down)
        }
        database.insert(TableName, null, values)
        database.close()
    }

    enum class Columns(val nameSQL: String, val indexSQL: Int) {
        Time("time", 0),
        Upload("up", 2),
        Download("down", 3);

        override fun toString(): String = nameSQL
        operator fun invoke(): String = toString()
    }
}
