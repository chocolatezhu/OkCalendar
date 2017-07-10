package com.ppzhu.calendar.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author zzl
 * 星期标题
 * Created on 2017/3/7.
 */

public class WeekTitleTextView extends TextView {
    public WeekTitleTextView(Context context) {
        super(context);
        setTypeface();
    }

    public WeekTitleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface();
    }

    public WeekTitleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface();
    }

    private void setTypeface() {
        Typeface typeFace = Typeface.createFromAsset(getResources().getAssets(), "fonts/calendar_font.ttf");
        setTypeface(typeFace);
    }

}
