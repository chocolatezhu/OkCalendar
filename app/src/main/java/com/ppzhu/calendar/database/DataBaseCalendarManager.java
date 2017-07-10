package com.ppzhu.calendar.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.ppzhu.calendar.bean.Festival;
import com.ppzhu.calendar.bean.Schedule;
import com.ppzhu.calendar.constants.FestiValColums;
import com.ppzhu.calendar.constants.ScheduleColums;
import com.ppzhu.calendar.utils.FestivalDatabaseUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class DataBaseCalendarManager implements IDatabaseCalendarReader,ScheduleReader {
    private static SQLiteDatabase mDatabase;
    public ContentResolver mContentResolver = null;
    private Context mContext;
    private static DataBaseCalendarManager instance = null;

    public static DataBaseCalendarManager getInstance(Context context) {
        if (null == instance) {
            synchronized (DataBaseCalendarManager.class) {
                if (null == instance) {
                    instance = new DataBaseCalendarManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    /**
     * 上下文环境
     */
    private DataBaseCalendarManager(Context context) {
        mContentResolver = context.getContentResolver();
        mContext = context;
        FestivalDatabaseUtil mFestivalDatabaseUtil = new FestivalDatabaseUtil();
        if (mDatabase == null) {
            mDatabase = mFestivalDatabaseUtil.openLocalDatabase(context);
        }
    }

    @Override
    public boolean saveFestivalObjList(List<Festival> list) {
        if (null == list) {
            return false;
        }
        synchronized (writeLock) {
            try {
                clearFestiValTable();
                DatabaseCalendarHelper mDbHelper = new DatabaseCalendarHelper(mContext);
                SQLiteDatabase db = mDbHelper.getReadableDatabase();
                SQLiteStatement sqLiteStatement = db.compileStatement("replace into festival(year,month,time,name,isfetival) values(?,?,?,?,?)");
                for (Festival info : list) {
                    sqLiteStatement.bindString(1, info.getYear());
                    sqLiteStatement.bindString(2, info.getMonth());
                    sqLiteStatement.bindString(3, info.getDay());
                    sqLiteStatement.bindString(4, info.getFestival());
                    sqLiteStatement.bindLong(5, info.getIsFestival());
                    sqLiteStatement.execute();
                }
                sqLiteStatement.close();
                closeDB();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * 清空节假日表的数据
     * */
    private void clearFestiValTable() {
        DatabaseCalendarHelper mDbHelper = new DatabaseCalendarHelper(mContext);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        mDbHelper.clearFestivalTable(db);
    }

    /**
     * 获取所有放假信息
     * */
    public List<Festival> getAllFestivalObjList() {
        List<Festival> ret = new ArrayList<>();
        Cursor cursor = null;
        String[] projection = new String[]{FestiValColums.IS_FESTIVAL, FestiValColums.TIME, FestiValColums.YEAR, FestiValColums.MONTH, FestiValColums.NAME};
        try {
            cursor = mContentResolver.query(FestiValColums.CONTENT_URI, projection, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor != null && cursor.getCount() > 0) {

            int timeIndex = cursor.getColumnIndex(FestiValColums.TIME);
            int yearIndex = cursor.getColumnIndex(FestiValColums.YEAR);
            int isFestivalIndex = cursor.getColumnIndex(FestiValColums.IS_FESTIVAL);
            int monthIndex = cursor.getColumnIndex(FestiValColums.MONTH);
            int nameIndex = cursor.getColumnIndex(FestiValColums.NAME);
            Festival festival;
            cursor.moveToFirst();
            do {
                festival = new Festival();
                festival.setDay(cursor.getString(timeIndex));
                festival.setYear(cursor.getString(yearIndex));
                festival.setIsFestival(cursor.getInt(isFestivalIndex));
                festival.setMonth(cursor.getString(monthIndex));
                festival.setFestival(cursor.getString(nameIndex));
                ret.add(festival);
            } while (cursor.moveToNext());
        }
        if (null != cursor) {
            cursor.close();
        }
        closeDB();
        return ret;
    }

    @Override
    public List<Festival> getFestivalObjListByYear(String year) {
        List<Festival> ret = new ArrayList<>();
        Cursor cursor = null;
        String[] projection = new String[]{FestiValColums.IS_FESTIVAL, FestiValColums.TIME, FestiValColums.YEAR, FestiValColums.MONTH, FestiValColums.NAME};
        try {
            String selection = FestiValColums.YEAR;
            String[] selectionArgs = {year};
            cursor = mContentResolver.query(FestiValColums.CONTENT_URI, projection, " year = ? ", selectionArgs, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor != null && cursor.getCount() > 0) {

            int timeIndex = cursor.getColumnIndex(FestiValColums.TIME);
            int yearIndex = cursor.getColumnIndex(FestiValColums.YEAR);
            int isFestivalIndex = cursor.getColumnIndex(FestiValColums.IS_FESTIVAL);
            int monthIndex = cursor.getColumnIndex(FestiValColums.MONTH);
            int nameIndex = cursor.getColumnIndex(FestiValColums.NAME);
            Festival festival;
            cursor.moveToFirst();
            do {
                festival = new Festival();
                festival.setDay(cursor.getString(timeIndex));
                festival.setYear(cursor.getString(yearIndex));
                festival.setIsFestival(cursor.getInt(isFestivalIndex));
                festival.setMonth(cursor.getString(monthIndex));
                festival.setFestival(cursor.getString(nameIndex));
                ret.add(festival);
            } while (cursor.moveToNext());
        }
        if (null != cursor) {
            cursor.close();
        }
        closeDB();
        return ret;
    }

    @Override
    public List<Festival> getFestivalObjListByMonth(String month) {
        synchronized (writeLock) {
            List<Festival> ret = new ArrayList<>();
            Cursor cursor = null;
            DatabaseCalendarHelper mDbHelper = new DatabaseCalendarHelper(mContext);
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            String[] projection = new String[]{FestiValColums.IS_FESTIVAL, FestiValColums.TIME, FestiValColums.YEAR, FestiValColums.MONTH, FestiValColums.NAME};
            try {
                // String selection=FestiValColums.MONTH;
                String[] selectionArgs = {month};
                cursor = db.query(FestiValColums.TABLE_NAME, projection, " month = ? ", selectionArgs, null, null, FestiValColums.TIME);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (cursor != null && cursor.getCount() > 0) {

                int timeIndex = cursor.getColumnIndex(FestiValColums.TIME);
                int isFestivalIndex = cursor.getColumnIndex(FestiValColums.IS_FESTIVAL);
                int yearIndex = cursor.getColumnIndex(FestiValColums.YEAR);
                int monthIndex = cursor.getColumnIndex(FestiValColums.MONTH);
                int nameIndex = cursor.getColumnIndex(FestiValColums.NAME);
                Festival festival;
                cursor.moveToFirst();
                do {
                    festival = new Festival();
                    festival.setDay(cursor.getString(timeIndex));
                    festival.setIsFestival(cursor.getInt(isFestivalIndex));
                    festival.setYear(cursor.getString(yearIndex));
                    festival.setMonth(cursor.getString(monthIndex));
                    festival.setFestival(cursor.getString(nameIndex));
                    ret.add(festival);
                } while (cursor.moveToNext());
            }
            if (cursor != null)
                cursor.close();
            if(db != null)
                db.close();
            closeDB();
            return ret;
        }
    }


    @Override
    public int getFestivalObjByThemeId(String day) {
        synchronized (writeLock) {
            Log.e("CalendarManager", "getFestivalObjByThemeId() called with: " + "day = [" + day + "]");
            DatabaseCalendarHelper mDbHelper = new DatabaseCalendarHelper(mContext);
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            Cursor cursor = null;
            String[] projection = new String[]{FestiValColums.TIME, FestiValColums.IS_FESTIVAL};
            try {
                String[] selectionArgs = {day};
                cursor = db.query(FestiValColums.TABLE_NAME, projection, " time = ? ", selectionArgs, null, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (cursor != null && cursor.getCount() > 0) {
                cursor.close();
                if(db != null)
                    db.close();
                closeDB();
                return 1;
            }
            if(db != null)
                db.close();
            closeDB();
            return 0;
        }
    }


    // TODO Auto-generated method stub


    //	@Override
//	public boolean deleteAllFestivalObjList(Long AddTime) {
//		String[] selectionArgs={AddTime+""};
//		// TODO Auto-generated method stub
//		mContentResolver.delete(FestiValColums.CONTENT_URI, FestiValColums.UPDATE_TIME_LONG+" < ? ", selectionArgs);
//		return false;
//	}
    public void closeDB() {
        if (mDatabase == null) {
            return;
        }
        mDatabase.close();
    }

    private Set<String> getKey(){
        Set<String> set = new HashSet<>();
        Cursor cursor = null;
        DatabaseCalendarHelper mDbHelper = new DatabaseCalendarHelper(mContext);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        try{

            cursor = db.query(ScheduleColums.TABLE_NAME, null, null, null, null, null, null);
            if(cursor != null && cursor.getCount() > 0){
                int date = cursor.getColumnIndex(ScheduleColums.DATE);
                cursor.moveToFirst();
                do{
                   set.add(cursor.getString(date));
                }while (cursor.moveToNext());
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if (cursor != null)
                cursor.close();
            if(db !=null)
                db.close();
            closeDB();
        }
       return set;
    }

    /**
     * 这个接口返回所有日程的封装数据有空值
     * */
    @Deprecated
    @Override
    public List<Schedule> getAllSchedule() {
        List<Schedule> ret = new ArrayList<>();
        Set<String> set = getKey();
        for(String value : set){
            Schedule schedule = new Schedule();
             schedule.setDate(value);
            ret.add(schedule);
            List<Schedule> list = getDaySchedule(value);
            for(Schedule s : list)
                ret.add(s);
        }
        return ret;
    }

    /**
     * 获取日程表里所有的数据
     * */
    public List<Schedule> getAllScheduleData() {
        List<Schedule> list = new ArrayList<>();
        Cursor cursor = null;
        DatabaseCalendarHelper mDbHelper = new DatabaseCalendarHelper(mContext);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        try {
            cursor = db.query(ScheduleColums.TABLE_NAME, null, null, null, null, null, null);
            if (null != cursor && cursor.getCount() > 0) {
                int id = cursor.getColumnIndex(ScheduleColums._ID);
                int title = cursor.getColumnIndex(ScheduleColums.TITLE);
                int location = cursor.getColumnIndex(ScheduleColums.LOCATION);
                int remark = cursor.getColumnIndex(ScheduleColums.REMARK);
                int startTime = cursor.getColumnIndex(ScheduleColums.STARTTIME);
                int endTime = cursor.getColumnIndex(ScheduleColums.ENDTIME);
                int repeatId = cursor.getColumnIndex(ScheduleColums.REPEATID);
                int repeatMode = cursor.getColumnIndex(ScheduleColums.REPEATMODE);
                int remindId = cursor.getColumnIndex(ScheduleColums.REMINDID);
                int allDay = cursor.getColumnIndex(ScheduleColums.ALLDAY);
                int date = cursor.getColumnIndex(ScheduleColums.DATE);
                int alertTime = cursor.getColumnIndex(ScheduleColums.ALERTTIME);
                int endTimeMill = cursor.getColumnIndex(ScheduleColums.ENDTIMEMILL);

                cursor.moveToFirst();
                do {
                    Schedule schedule = new Schedule();
                    schedule.setId(cursor.getString(id));
                    schedule.setTitle(cursor.getString(title));
                    schedule.setAllDay(cursor.getInt(allDay));
                    schedule.setEndTime(cursor.getString(endTime));
                    schedule.setLocation(cursor.getString(location));
                    schedule.setRemark(cursor.getString(remark));
                    schedule.setRemindId(cursor.getInt(remindId));
                    schedule.setRepeatId(cursor.getString(repeatId));
                    schedule.setRepeatMode(cursor.getInt(repeatMode));
                    schedule.setStartTime(cursor.getString(startTime));
                    schedule.setDate(cursor.getString(date));
                    schedule.setAlertTime(cursor.getLong(alertTime));
                    schedule.setEndTimeMill(cursor.getLong(endTimeMill));
                    list.add(schedule);
                } while (cursor.moveToNext());
            }

            if (null != cursor) {
                cursor.close();
            }
        } catch (Exception e) {
            if (null != cursor) {
                cursor.close();
            }
            if (null != db) {
                db.close();
            }
            closeDB();
        } finally {
            if (null != cursor) {
                cursor.close();
            }
            if (null != db) {
                db.close();
            }
            closeDB();
        }

        return list;
    }


    @Override
    public Schedule getSchedule(String id) {
        Cursor cursor = null;
        Schedule schedule = null;
        DatabaseCalendarHelper mDbHelper = new DatabaseCalendarHelper(mContext);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        try{
            String[] selectionArgs = {id};
            cursor = db.query(ScheduleColums.TABLE_NAME, null, " _id = ?", selectionArgs, null, null, null);
            if(cursor != null && cursor.getCount() > 0){
                //int id = cursor.getColumnIndex(ScheduleColums._ID);
                int title = cursor.getColumnIndex(ScheduleColums.TITLE);
                int location = cursor.getColumnIndex(ScheduleColums.LOCATION);
                int remark = cursor.getColumnIndex(ScheduleColums.REMARK);
                int startTime = cursor.getColumnIndex(ScheduleColums.STARTTIME);
                int endTime = cursor.getColumnIndex(ScheduleColums.ENDTIME);
                int repeatId = cursor.getColumnIndex(ScheduleColums.REPEATID);
                int repeatMode = cursor.getColumnIndex(ScheduleColums.REPEATMODE);
                int remindId = cursor.getColumnIndex(ScheduleColums.REMINDID);
                int allDay = cursor.getColumnIndex(ScheduleColums.ALLDAY);
                int date = cursor.getColumnIndex(ScheduleColums.DATE);
                int alertTime = cursor.getColumnIndex(ScheduleColums.ALERTTIME);
                int endTimeMill = cursor.getColumnIndex(ScheduleColums.ENDTIMEMILL);
                cursor.moveToFirst();
                schedule = new Schedule();
                schedule.setId(id);
                schedule.setTitle(cursor.getString(title));
                schedule.setAllDay(cursor.getInt(allDay));
                schedule.setEndTime(cursor.getString(endTime));
                schedule.setLocation(cursor.getString(location));
                schedule.setRemark(cursor.getString(remark));
                schedule.setRemindId(cursor.getInt(remindId));
                schedule.setRepeatMode(cursor.getInt(repeatMode));
                schedule.setRepeatId(cursor.getString(repeatId));
                schedule.setStartTime(cursor.getString(startTime));
                schedule.setDate(cursor.getString(date));
                schedule.setAlertTime(cursor.getLong(alertTime));
                schedule.setEndTimeMill(cursor.getLong(endTimeMill));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if (cursor != null)
                cursor.close();
            if(db !=null)
                db.close();
            closeDB();
        }
        return schedule;
    }

    @Override
    public List<Schedule> getDaySchedule(String day) {
        List<Schedule> ret = new ArrayList<>();
        Cursor cursor = null;
        DatabaseCalendarHelper mDbHelper = new DatabaseCalendarHelper(mContext);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        try{
            String[] selectionArgs = {day};
            cursor = db.query(ScheduleColums.TABLE_NAME, null, " date = ?", selectionArgs, null, null, null);
            /*String sql = "select * from " + ScheduleColums.TABLE_NAME
                    + " where " + ScheduleColums.STARTTIME + " like '"+day+"%'";//注意：这里有单引号
            cursor = db.rawQuery(sql,null);*/
            if(cursor != null && cursor.getCount() > 0) {
                int id = cursor.getColumnIndex(ScheduleColums._ID);
                int title = cursor.getColumnIndex(ScheduleColums.TITLE);
                int location = cursor.getColumnIndex(ScheduleColums.LOCATION);
                int remark = cursor.getColumnIndex(ScheduleColums.REMARK);
                int startTime = cursor.getColumnIndex(ScheduleColums.STARTTIME);
                int endTime = cursor.getColumnIndex(ScheduleColums.ENDTIME);
                int repeatId = cursor.getColumnIndex(ScheduleColums.REPEATID);
                int repeatMode = cursor.getColumnIndex(ScheduleColums.REPEATMODE);
                int remindId = cursor.getColumnIndex(ScheduleColums.REMINDID);
                int allDay = cursor.getColumnIndex(ScheduleColums.ALLDAY);
                int date = cursor.getColumnIndex(ScheduleColums.DATE);
                int alertTime = cursor.getColumnIndex(ScheduleColums.ALERTTIME);
                int endTimeMill = cursor.getColumnIndex(ScheduleColums.ENDTIMEMILL);
                Schedule schedule;
                cursor.moveToFirst();
                do {
                    schedule = new Schedule();
                    schedule.setId(cursor.getString(id));
                    schedule.setTitle(cursor.getString(title));
                    schedule.setAllDay(cursor.getInt(allDay));
                    schedule.setEndTime(cursor.getString(endTime));
                    schedule.setLocation(cursor.getString(location));
                    schedule.setRemark(cursor.getString(remark));
                    schedule.setRemindId(cursor.getInt(remindId));
                    schedule.setRepeatId(cursor.getString(repeatId));
                    schedule.setRepeatMode(cursor.getInt(repeatMode));
                    schedule.setStartTime(cursor.getString(startTime));
                    schedule.setDate(cursor.getString(date));
                    schedule.setAlertTime(cursor.getLong(alertTime));
                    schedule.setEndTimeMill(cursor.getLong(endTimeMill));
                    ret.add(schedule);
                } while (cursor.moveToNext());
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if (cursor != null)
                cursor.close();
            if(db !=null)
                db.close();
            closeDB();
        }
        return ret;
    }

    @Override
    public int updateSchedule(Schedule schedule) {
        ContentValues values = new ContentValues();
        values.put(ScheduleColums._ID, schedule.getId());
        values.put(ScheduleColums.TITLE, schedule.getTitle());
        values.put(ScheduleColums.LOCATION, schedule.getLocation());
        values.put(ScheduleColums.REMARK, schedule.getRemark());
        values.put(ScheduleColums.STARTTIME, schedule.getStartTime());
        values.put(ScheduleColums.ENDTIME, schedule.getEndTime());
        values.put(ScheduleColums.REMINDID, schedule.getRemindId());
        values.put(ScheduleColums.REPEATID, schedule.getRepeatId());
        values.put(ScheduleColums.REPEATMODE, schedule.getRepeatMode());
        values.put(ScheduleColums.ALLDAY, schedule.getAllDay());
        values.put(ScheduleColums.DATE, schedule.getDate());
        values.put(ScheduleColums.ALERTTIME, schedule.getAlertTime());
        values.put(ScheduleColums.ENDTIMEMILL, schedule.getEndTimeMill());
        DatabaseCalendarHelper mDbHelper = new DatabaseCalendarHelper(mContext);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] args = {schedule.getId()};
        int num = db.update(ScheduleColums.TABLE_NAME,values,ScheduleColums._ID + " = ?",args);
        db.close();
        closeDB();
        return num;
    }

    /**
     * 更新指定id的日程
     * @param values
     * @param selection 更新的字段参数
     * @param selectionArgs 指定更新的条件内容
     * */
    public int updateSchedule(ContentValues values, String selection, String[] selectionArgs) {
        if (null == values) {
            return -1;
        }
        DatabaseCalendarHelper mDbHelper = new DatabaseCalendarHelper(mContext);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        int num = db.update(ScheduleColums.TABLE_NAME, values, selection, selectionArgs);
        db.close();
        closeDB();
        return num;
    }

    @Override
    public int deleteSchedule(String id) {
        DatabaseCalendarHelper mDbHelper = new DatabaseCalendarHelper(mContext);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int num = db.delete(ScheduleColums.TABLE_NAME,
                ScheduleColums._ID + " = ? ", new String[]{id});
        db.close();
        closeDB();
        return num;
    }

    /**
     * 删除指定条件的日程
     * @param selection 删除的参数字段
     * @param selectionArgs 指定删除的条件内容
     * */
    public int deleteSchedule(String selection, String[] selectionArgs) {
        DatabaseCalendarHelper mDbHelper = new DatabaseCalendarHelper(mContext);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int num = db.delete(ScheduleColums.TABLE_NAME, selection, selectionArgs);
        db.close();
        closeDB();
        return num;
    }

    @Override
    public Long saveSchedule(Schedule schedule) {
        ContentValues values = new ContentValues();
        values.put(ScheduleColums._ID, schedule.getId());
        values.put(ScheduleColums.TITLE, schedule.getTitle());
        values.put(ScheduleColums.LOCATION, schedule.getLocation());
        values.put(ScheduleColums.REMARK, schedule.getRemark());
        values.put(ScheduleColums.STARTTIME, schedule.getStartTime());
        values.put(ScheduleColums.ENDTIME, schedule.getEndTime());
        values.put(ScheduleColums.REMINDID, schedule.getRemindId());
        values.put(ScheduleColums.REPEATID, schedule.getRepeatId());
        values.put(ScheduleColums.REPEATMODE, schedule.getRepeatMode());
        values.put(ScheduleColums.ALLDAY, schedule.getAllDay());
        values.put(ScheduleColums.DATE, schedule.getDate());
        values.put(ScheduleColums.ALERTTIME, schedule.getAlertTime());
        values.put(ScheduleColums.ENDTIMEMILL, schedule.getEndTimeMill());
        DatabaseCalendarHelper mDbHelper = new DatabaseCalendarHelper(mContext);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Long num = db.insert(ScheduleColums.TABLE_NAME, null, values);
        db.close();
        closeDB();
        return num;
    }

    /**
     * 插入已经封装好日程数据
     * @param values 封装好的ContentValues数据
     * */
    public long saveSchedule(ContentValues values) {
        if (null == values) {
            return -1;
        }
        DatabaseCalendarHelper mDbHelper = new DatabaseCalendarHelper(mContext);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Long num = db.insert(ScheduleColums.TABLE_NAME, null, values);
        db.close();
        closeDB();
        return num;
    }

    /**
     * 根据条件查询日程
     * <p> 这里能关闭数据库，因为需要返回游标给其他地方使用 </p>
     * */
    public Cursor querySchedule(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        DatabaseCalendarHelper mDbHelper = new DatabaseCalendarHelper(mContext);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        return db.query(ScheduleColums.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public List<Schedule> getSearchSchedule(String titles) {
        List<Schedule> ret = new ArrayList<>();
        Cursor cursor = null;
        Schedule schedule = null;
        DatabaseCalendarHelper mDbHelper = new DatabaseCalendarHelper(mContext);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql = "SELECT  *  FROM "+ScheduleColums.TABLE_NAME +" where "+ScheduleColums.TITLE+" like '%%"+titles+"%%'";

        try{
            cursor = db.rawQuery(sql,null);
            if(cursor != null && cursor.getCount() > 0){
                int id = cursor.getColumnIndex(ScheduleColums._ID);
                int title = cursor.getColumnIndex(ScheduleColums.TITLE);
                int location = cursor.getColumnIndex(ScheduleColums.LOCATION);
                int remark = cursor.getColumnIndex(ScheduleColums.REMARK);
                int startTime = cursor.getColumnIndex(ScheduleColums.STARTTIME);
                int endTime = cursor.getColumnIndex(ScheduleColums.ENDTIME);
                int repeatId = cursor.getColumnIndex(ScheduleColums.REPEATID);
                int repeatMode = cursor.getColumnIndex(ScheduleColums.REPEATMODE);
                int remindId = cursor.getColumnIndex(ScheduleColums.REMINDID);
                int allDay = cursor.getColumnIndex(ScheduleColums.ALLDAY);
                int date = cursor.getColumnIndex(ScheduleColums.DATE);
                int alertTime = cursor.getColumnIndex(ScheduleColums.ALERTTIME);
                int endTimeMill = cursor.getColumnIndex(ScheduleColums.ENDTIMEMILL);
                cursor.moveToFirst();
                do {
                    schedule = new Schedule();
                    schedule.setId(cursor.getString(id));
                    schedule.setTitle(cursor.getString(title));
                    schedule.setAllDay(cursor.getInt(allDay));
                    schedule.setStartTime(cursor.getString(startTime));
                    schedule.setEndTime(cursor.getString(endTime));
                    schedule.setLocation(cursor.getString(location));
                    schedule.setRemark(cursor.getString(remark));
                    schedule.setRemindId(cursor.getInt(remindId));
                    schedule.setRepeatId(cursor.getString(repeatId));
                    schedule.setRepeatMode(cursor.getInt(repeatMode));
                    schedule.setDate(cursor.getString(date));
                    schedule.setAlertTime(cursor.getLong(alertTime));
                    schedule.setEndTimeMill(cursor.getLong(endTimeMill));
                    ret.add(schedule);
                } while (cursor.moveToNext());
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if (cursor != null)
                cursor.close();
            if(db !=null)
                db.close();
            closeDB();
        }
        return dealData(ret);
    }

    private List<Schedule> dealData(List<Schedule> list){
        Collections.sort(list);
        List<Schedule> ret = new ArrayList<>();
      //  Set<String> set = new HashSet<String>();
        Set<String> set = new TreeSet<>(new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });
        try{
            for(Schedule sc : list){
                set.add(sc.getDate());
            }

            for(String value : set){
                Schedule schedule = new Schedule();
                schedule.setDate(value);
                ret.add(schedule);
                for(Schedule s : list){
                    if(s.getDate().equals(value))
                        ret.add(s);
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return ret;
    }

    public static void releaseInstance(){
        if (null != instance){
            instance = null;
        }
    }
}
