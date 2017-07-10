package com.ppzhu.calendar.utils;

/**
 * Created by Administrator on 2015/12/10.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Method;

public class StatusBarUtil {
    private static final int INVALID_VAL = -1;
    private static final int COLOR_DEFAULT = Color.parseColor("#00000000");

    public static void setStateBar(Activity activity) {
        try {
            switchTransSystemUI(activity);
        } catch (Exception e) {
        } catch (Error e) {
        }
    }

    public static void setStatusBarColor(Activity activity, int statusColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (statusColor != INVALID_VAL) {
                activity.getWindow().setStatusBarColor(statusColor);
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setStatusBarTransparent(activity.getWindow());
            int color = COLOR_DEFAULT;
            ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
            if (statusColor != INVALID_VAL) {
                color = statusColor;
            }
            View statusBarView = new View(activity);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getStatusBarHeight(activity));
            statusBarView.setBackgroundColor(color);
            contentView.addView(statusBarView, lp);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }


    public static void setStatusBarColor(Window window, int statusColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (statusColor != INVALID_VAL) {
                window.setStatusBarColor(statusColor);
            }
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setStatusBarTransparent(window);
            int color = COLOR_DEFAULT;
            ViewGroup contentView = (ViewGroup) window.findViewById(android.R.id.content);
            if (statusColor != INVALID_VAL) {
                color = statusColor;
            }
            View statusBarView = new View(window.getContext());
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getStatusBarHeight(window.getContext()));
            statusBarView.setBackgroundColor(color);
            contentView.addView(statusBarView, lp);
        }
    }

    public static void setStatusBarTransparent(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static void setStatusBarTransparent(Window window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public static void setStatusBarColor(Activity activity) {
        setStatusBarColor(activity, INVALID_VAL);
    }


    protected static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static final int SYSTEM_UI_FLAG_CUSTOM_BAR_COLOR = 0x00002000;
    public static final int SYSTEM_UI_FLAG_TRANSLUCENT_BAR = 0x00000800;

    //	private static final int SYSTEM_UI_FLAG_TRANSLUCENT_BAR = View.SYSTEM_UI_FLAG_TRANSLUCENT_BAR;
    public static void setStatusBarGray(Activity activity) {
        View view = activity.getWindow().getDecorView();
        int systemUIVis = view.getSystemUiVisibility();
        systemUIVis |= SYSTEM_UI_FLAG_CUSTOM_BAR_COLOR;
        view.setSystemUiVisibility(systemUIVis);
        invokeMethod(View.class, view, "setCustomBarGray",
                SET_CUSTOM_BAR_COLOR, activity.getPackageName());
    }

    private final static Class[] SET_CUSTOM_BAR_COLOR = new Class[]{String.class};

    /**
     * Invoke specified class method.
     */
    public static Object invokeMethod(Class<?> objClass, Object object, String methodName, Class<?>[] paramTypes,
                                      Object... args) {

        if (null == objClass || null == object || null == methodName) {
            return null;
        }

        Method method = null;

        try {
            // method = objClass.getDeclaredMethod(methodName, paramTypes);
            method = objClass.getMethod(methodName, paramTypes);
            method.setAccessible(true);
            return method.invoke(object, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 设置全屏，并且状态栏透明悬浮在应用界面之上
     */
    public static void setFullScreenWithTranslate(Activity activity) {
        Window window = activity.getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    public static void switchTransSystemUI(Activity activity) {
        Window window = activity.getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }


}