package com.ppzhu.calendar.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ppzhu.calendar.constants.ConstData;

/**
 * 保存SharedPreferences工具类
 */
public class SharedPrefUtil {
    /**
     * 一周
     */
    public static final long DEFAULT_TIME = 604800000;
    private static final String CALENDAR_SHAREDPREFERENCE = "calendar_sharedpreference";
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mSharedPreferences;

    public SharedPrefUtil(Context context, String name) {
        mSharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public static void writeShareIsFirst(Context context, boolean isFirst) {
        SharedPreferences sp = context.getSharedPreferences(CALENDAR_SHAREDPREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(ConstData.IS_FIRST, isFirst);
        editor.commit();
    }

    public static boolean getShareIsFirst(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                CALENDAR_SHAREDPREFERENCE, 0);
        return sharedPreferences.getBoolean(ConstData.IS_FIRST, true);
    }

    public void putString(String key, String value) {
        mEditor.putString(key, value);
    }

    public void putInt(String key, int value) {
        mEditor.putInt(key, value);
    }

    public void putFloat(String key, float value) {
        mEditor.putFloat(key, value);
    }

    public void putLong(String key, long value) {
        mEditor.putLong(key, value);
    }

    public void putBoolean(String key, boolean value) {
        mEditor.putBoolean(key, value);
    }

    public int getInt(String key, int defaultValue) {
        return mSharedPreferences.getInt(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mSharedPreferences.getBoolean(key, defaultValue);
    }

    public float getFloat(String key, float defaultValue) {
        return mSharedPreferences.getFloat(key, defaultValue);
    }

    public long getLong(String key, long defaultValue) {
        return mSharedPreferences.getLong(key, defaultValue);
    }

    public String getString(String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    public void commit() {
        mEditor.commit();
    }
}
