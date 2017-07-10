package com.ppzhu.calendar.view.slidelayout;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Jimmy on 2016/10/8 0008.
 */
public class ScheduleRecyclerView extends RecyclerView {
    private boolean mIsIdle = true;

    public ScheduleRecyclerView(Context context) {
        this(context, null);
    }

    public ScheduleRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScheduleRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean isScrollTop() {
        return computeVerticalScrollOffset() == 0;
    }

    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        if (getOnFocusChangeListener() != null) {
            getOnFocusChangeListener().onFocusChange(child, false);
            getOnFocusChangeListener().onFocusChange(focused, true);
        }
    }

    public void setIsIdle(boolean isIdle) {
        mIsIdle = isIdle;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!mIsIdle) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }
}