package com.ppzhu.calendar.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ppzhu.calendar.constants.ConstData;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FestivalDatabaseUtil {
    //数据库存储路径
    static final String localDatabasePath = "/data/data/com.eebbk.calendar/databases/calendar.db";
    //数据库存放的文件夹 data/data/com.main.jh 下面
    static final String databasesFileDir = "/data/data/com.eebbk.calendar/databases";

    /**
     * 打开日历数据库
     * <p> asset目录预置了日历的数据库，初衷是为了方便为其他模块（例如防沉迷模块）提供节假日信息数据 </p>
     * */
    public SQLiteDatabase openLocalDatabase(final Context context) {
        File localDatabaseFile = new File(localDatabasePath);
        if (localDatabaseFile.exists()) {
            //存在则直接返回打开的数据库
            return SQLiteDatabase.openOrCreateDatabase(localDatabaseFile, null);
        } else {
            //不存在先创建文件夹
            File path = new File(databasesFileDir);
            if (path.mkdir()) {
                Log.i(ConstData.TAG, "创建成功");
            } else {
                Log.i(ConstData.TAG, "创建失败");
            }
            try {
                AssetManager am = context.getAssets();
                //得到数据库的输入流
                InputStream is = am.open("calendar.db");
                //用输出流写到SDcard上面
                FileOutputStream fos = new FileOutputStream(localDatabaseFile);
                //创建byte数组  用于1KB写一次
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.flush();
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            //如果没有这个数据库  我们已经把他写到SD卡上了，然后在执行一次这个方法 就可以返回数据库了
            return openLocalDatabase(context);
        }
    }
}