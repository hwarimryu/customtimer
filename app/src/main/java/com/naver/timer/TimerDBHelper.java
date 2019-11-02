package com.naver.timer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TimerDBHelper extends SQLiteOpenHelper {
    public TimerDBHelper(Context context) {
        super(context, "CTimerDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table myTimerNames(tName varchar(20) primary key);");
        db.execSQL("create table myTimers(id integer primary key, tName varchar(20), tNumber integer, tString varchar(6));");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
