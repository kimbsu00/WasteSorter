package com.butter.wastesorter.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.butter.wastesorter.data.Record
import com.butter.wastesorter.data.Trash

class MyDBHelper(val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        val DB_NAME = "wastesorter.db"
        val DB_VERSION = 1
        val TABLE_NAME = arrayOf("record")
        val CODE = "code"
        val TIME = "time"
    }

    fun getRecord(): Record {
        val plastic: ArrayList<String> = ArrayList()
        val paper: ArrayList<String> = ArrayList()
        val cardboard: ArrayList<String> = ArrayList()
        val can: ArrayList<String> = ArrayList()
        val glass: ArrayList<String> = ArrayList()
        val metal: ArrayList<String> = ArrayList()
        val trash: ArrayList<String> = ArrayList()

        val strsql = "select * from ${TABLE_NAME[0]};"
        val db = readableDatabase
        val cursor = db.rawQuery(strsql, null)
        cursor.moveToFirst()
        if (cursor.count != 0) {
            do {
                val code: Int = cursor.getInt(0)
                val time: String = cursor.getString(1)

                when (code) {
                    Trash.PLASTIC -> {
                        plastic.add(time)
                    }
                    Trash.PAPER -> {
                        paper.add(time)
                    }
                    Trash.CARDBOARD -> {
                        cardboard.add(time)
                    }
                    Trash.CAN -> {
                        can.add(time)
                    }
                    Trash.GLASS -> {
                        glass.add(time)
                    }
                    Trash.METAL -> {
                        metal.add(time)
                    }
                    Trash.TRASH -> {
                        trash.add(time)
                    }
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return Record(plastic, paper, cardboard, can, glass, metal, trash)
    }

    fun insertRecord(code: Int, time: String): Boolean {
        val values = ContentValues()
        values.put(CODE, code)
        values.put(TIME, time)

        val db = writableDatabase
        val ret = db.insert(TABLE_NAME[0], null, values) > 0
        db.close()
        return ret
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "create table if not exists ${TABLE_NAME[0]}($CODE INTEGER, $TIME text)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }


}