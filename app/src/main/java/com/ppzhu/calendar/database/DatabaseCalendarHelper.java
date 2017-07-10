package com.ppzhu.calendar.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.ppzhu.calendar.constants.FestiValColums;
import com.ppzhu.calendar.constants.ScheduleColums;


public class DatabaseCalendarHelper extends SQLiteOpenHelper {

    public DatabaseCalendarHelper(Context context, String name) {
        this(context, name, 1);
    }

    // 创建一个表
    private static final String DB_FESTIVAL_CREATE = "CREATE TABLE if not exists "
            + FestiValColums.TABLE_NAME + " ("
            + FestiValColums.YEAR + " TEXT,"
            + FestiValColums.MONTH + " TEXT,"
            + FestiValColums.TIME + "  TEXT PRIMARY KEY,"
            + FestiValColums.NAME + " TEXT,"
            + FestiValColums.IS_FESTIVAL + " INT )";

    private static final String DB_SCHEDULE_CREATE = "CREATE TABLE if not exists "
            + ScheduleColums.TABLE_NAME + " ("
            + ScheduleColums.TITLE + " TEXT,"
            + ScheduleColums.LOCATION + " TEXT,"
            + ScheduleColums.REMARK + " TEXT,"
            + ScheduleColums.STARTTIME + " TEXT,"
            + ScheduleColums.ENDTIME + " TEXT,"
            + ScheduleColums.DATE + " TEXT,"
            + ScheduleColums.REPEATID + " TEXT,"
            + ScheduleColums.REMINDID + " INT,"
            + ScheduleColums.ALLDAY + " INT,"
            + ScheduleColums.ALERTTIME + " INT,"
            + ScheduleColums.ENDTIMEMILL + " INT,"
            + ScheduleColums.REPEATMODE + " INT,"
            + ScheduleColums._ID + "  TEXT PRIMARY KEY );";

    public DatabaseCalendarHelper(Context context, String name,
                                  CursorFactory factory, int version) {
        // 必须通过super调用父类当中的构造函数
        super(context, name, factory, version);
    }

    public DatabaseCalendarHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }

    public DatabaseCalendarHelper(Context context) {
        this(context, FestiValColums.DB_NAME,
                FestiValColums.DB_VERSION);
    }

    /**
     * 创建一个表
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 数据库没有表时创建一个
        db.execSQL(DB_FESTIVAL_CREATE);
        db.execSQL(DB_SCHEDULE_CREATE);
    }

    /**
     * 升级数据库
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + FestiValColums.TABLE_NAME);
//        db.execSQL("DROP TABLE IF EXISTS " + ScheduleColums.TABLE_NAME);
        onCreate(db);
    }

    public void clearFestivalTable(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + FestiValColums.TABLE_NAME);
        db.execSQL(DB_FESTIVAL_CREATE);
    }


    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //复写该方法，允许数据库版本降级
    }

    public void themeTableRecreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + FestiValColums.TABLE_NAME);
        db.execSQL(DB_FESTIVAL_CREATE);
    }


    public boolean tabIsExist(String tabName) {
        boolean result = false;
        if (tabName == null) {
            return false;
        }
        SQLiteDatabase db;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"
                    + tabName.trim() + "' ";
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(null != cursor){
                cursor.close();
            }
        }

        return result;
    }

}
