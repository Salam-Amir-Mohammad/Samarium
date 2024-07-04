package com.example.myapplication.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NetworkInfoDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "networkinfo.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "allinfo"
        private const val COLUMN_ID = "id"
        private const val COLUMN_EVENT_TIME = "eventTime"
        private const val COLUMN_LATITUDE = "latitude"
        private const val COLUMN_LONGITUDE = "longitude"
        private const val COLUMN_CELL_TECHNOLOGY = "cellTechnology"
        private const val COLUMN_PLMN_ID = "plmnId"
        private const val COLUMN_RAC = "rac"
        private const val COLUMN_TAC = "tac"
        private const val COLUMN_LAC = "lac"
        private const val COLUMN_CELL_ID = "cellId"
        private const val COLUMN_SIGNAL_STRENGTH = "signalStrength"
        private const val COLUMN_RSRQ = "rsrq"
        private const val COLUMN_RSRP = "rsrp"
        private const val COLUMN_RSCP = "rscp"
        private const val COLUMN_EC_NO = "ecNo"
        private const val COLUMN_QOS = "qos"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_EVENT_TIME INTEGER NOT NULL,
                $COLUMN_LATITUDE REAL NOT NULL,
                $COLUMN_LONGITUDE REAL NOT NULL,
                $COLUMN_CELL_TECHNOLOGY TEXT NOT NULL,
                $COLUMN_PLMN_ID TEXT,
                $COLUMN_RAC TEXT,
                $COLUMN_TAC TEXT,
                $COLUMN_LAC TEXT,
                $COLUMN_CELL_ID TEXT,
                $COLUMN_SIGNAL_STRENGTH INTEGER,
                $COLUMN_RSRQ INTEGER,
                $COLUMN_RSRP INTEGER,
                $COLUMN_RSCP INTEGER,
                $COLUMN_EC_NO INTEGER,
                $COLUMN_QOS TEXT NOT NULL
            )
        """.trimIndent()
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertInfo(netinfo: NetworkInfo) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_EVENT_TIME, netinfo.eventTime)
            put(COLUMN_LATITUDE, netinfo.latitude)
            put(COLUMN_LONGITUDE, netinfo.longitude)
            put(COLUMN_CELL_TECHNOLOGY, netinfo.cellTechnology)
            put(COLUMN_PLMN_ID, netinfo.plmnId)
            put(COLUMN_RAC, netinfo.rac)
            put(COLUMN_TAC, netinfo.tac)
            put(COLUMN_LAC, netinfo.lac)
            put(COLUMN_CELL_ID, netinfo.cellId)
            put(COLUMN_SIGNAL_STRENGTH, netinfo.rsrp)  // Assuming rsrp for signal strength
            put(COLUMN_RSRQ, netinfo.rsrq)
            put(COLUMN_RSRP, netinfo.rsrp)
            put(COLUMN_RSCP, netinfo.rscp)
            put(COLUMN_EC_NO, netinfo.ecNo)
            //put(COLUMN_SIGNAL_QUALITY, netinfo.signalQuality)
            put(COLUMN_QOS, netinfo.qos)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }
}
