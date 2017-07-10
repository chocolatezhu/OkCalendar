package com.ppzhu.calendar.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * @author fengjun
 * @created 2015-3-19
 */
public class T {
    private static T mToastEx;
    private Toast mToast;

    protected T(Context context) {
        if (null == mToast) {
            mToast = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_LONG);
        }
    }

    public static T getInstance(Context context) {
        if (null == mToastEx) {
            mToastEx = new T(context.getApplicationContext());
        }
        return mToastEx;
    }

    /**
     * toast show
     *
     * @param text
     */
    public void l(String text) {
        if (null == mToast) {
            return;
        }
        mToast.setText(text);
        showLongBase();
    }

    /**
     * toast show
     */
    public void l(int textID) {
        if (null == mToast) {
            return;
        }
        mToast.setText(textID);
        showLongBase();
    }

    /**
     * toast show
     */
    public void s(String text) {
        if (null == mToast) {
            return;
        }
        mToast.setText(text);
        showShortBase();
    }

    /**
     * toast show
     */
    public void s(int textID) {
        if (null == mToast) {
            return;
        }
        mToast.setText(textID);
        showShortBase();
    }

    /**
     * toast cancel ( Normally, it can be used when we exit the application )
     */
    public void cancel() {
        if (null == mToast) {
            return;
        }
        mToast.cancel();
    }

    /**
     * 短吐司通用属性
     */
    private void showLongBase() {
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
    }

    /**
     * 长吐司通用属性
     */
    private void showShortBase() {
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }


}
