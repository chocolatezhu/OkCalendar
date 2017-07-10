package com.ppzhu.calendar.utils;

import android.os.Environment;

import java.io.File;


/**
 * Created by jyqqhw on 16-1-6.
 */
public class FileUtil {
    private static long curTime;

    /**文件是否存在*/
    public static boolean isFileExist(String aFilePath) {
        File mFile = null;
        boolean mIsExist = false;
        if(checkFileSystemIsOk() && null != aFilePath) {
            mFile = new File(aFilePath);
            mIsExist = mFile.exists();
        }
        return mIsExist;
    }

    private static boolean checkFileSystemIsOk() {
        boolean mIsOk = false;
        if(Environment.getExternalStorageState().equals("mounted")) {
            mIsOk = true;
        }
        return mIsOk;
    }

    public static boolean checkMultiClick(){
        if(0 != curTime){
            curTime = System.currentTimeMillis() - curTime;
            if(curTime < 1000){
                curTime = System.currentTimeMillis();
                return true;
            }
            curTime = System.currentTimeMillis();
        }else{
            curTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

}
