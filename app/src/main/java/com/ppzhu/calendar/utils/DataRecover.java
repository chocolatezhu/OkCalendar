package com.ppzhu.calendar.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 用于数据库文件恢复
 * Created by zzl on 2017/1/17.
 */
public class DataRecover {
    //日历数据库
    private static final String CALENDAR_DB_NAME = "calendar.db";

    //数据库存放的文件夹
    private static final String DATABASE_FILE_DIR = "/data/data/com.eebbk.calendar/databases";
    //日历数据库存储路径
    private static final String CALENDAR_DB_PATH = DATABASE_FILE_DIR + File.separator + CALENDAR_DB_NAME;

    /**
     * 检测日历数据库
     * @param context 上下文
     * */
    public static void checkCalendarDB(Context context) {
        if (!isCalendarDbExist()) {
            File file = new File(DATABASE_FILE_DIR);
            if (!file.exists()) {
                file.mkdirs();
            }
            copyCalendarDB(context, CALENDAR_DB_PATH);
        }
    }

    /**
     * 恢复日历数据库
     * @param context 上下文
     * @param outPath 输出路径
     * */
    private static void copyCalendarDB(Context context, String outPath) {
        try {
            int byteRead = 0;
            InputStream inStream = context.getResources().getAssets().open(CALENDAR_DB_NAME);
            FileOutputStream outStream = new FileOutputStream(outPath);
            byte[] buffer = new byte[1024];
            while ((byteRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, byteRead);
            }
            outStream.close();
            inStream.close();
        } catch (IOException e) {
        }
    }

    /**
     * 解压文件
     * @param filePath 需要解压的文件路径
     * @param folderPath 解压输出的文件目录
     * */
    private static void unZipFile(String filePath, String folderPath) {
        try {
            File zipFile = new File(filePath);
            Unzip.upZipFile(zipFile, folderPath, true);
        } catch (Exception e) {
        }
    }

    public static boolean isCalendarDbExist() {
        return FileUtil.isFileExist(CALENDAR_DB_PATH);
    }

}
