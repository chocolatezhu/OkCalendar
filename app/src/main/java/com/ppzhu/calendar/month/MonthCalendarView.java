package com.ppzhu.calendar.month;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;


import com.ppzhu.calendar.R;
import com.ppzhu.calendar.constants.ConstData;

import java.util.Calendar;

/**
 * Created by Jimmy on 2016/10/6 0006.
 */
public class MonthCalendarView extends ViewPager implements OnMonthClickListener {
    //是否从年视图切换，也是判断动画是否结束的标志
    private boolean isFromYearAnimRunning = false;
    public MonthAdapter mMonthAdapter;
    private OnCalendarClickListener mOnCalendarClickListener;

    public MonthCalendarView(Context context) {
        this(context, null);
    }

    public MonthCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
//        addOnPageChangeListener(mOnPageChangeListener);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        initMonthAdapter(context, context.obtainStyledAttributes(attrs, R.styleable.MonthCalendarView));
    }

    public void initMonthAdapter(Context context, TypedArray array) {
        mMonthAdapter = new MonthAdapter(context, array, this);
        setAdapter(mMonthAdapter);
    }

    @Override
    public void onClickThisMonth(int year, int month, int day) {
        if (mOnCalendarClickListener != null) {
            mOnCalendarClickListener.onClickDate(year, month, day);
        }
    }

    @Override
    public void onClickLastMonth(int year, int month, int day) {
        MonthView monthDateView = mMonthAdapter.getViews().get(getCurrentItem() - 1);
        if (monthDateView != null) {
            monthDateView.setSelectYearMonth(year, month, day);
        }
        setCurrentItem(getCurrentItem() - 1, true);
    }

    @Override
    public void onClickNextMonth(int year, int month, int day) {
        MonthView monthDateView = mMonthAdapter.getViews().get(getCurrentItem() + 1);
        if (monthDateView != null) {
            monthDateView.setSelectYearMonth(year, month, day);
            monthDateView.invalidate();
        }
        onClickThisMonth(year, month, day);
        setCurrentItem(getCurrentItem() + 1, true);
    }

    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            MonthView monthView = mMonthAdapter.getViews().get(getCurrentItem());
            if (monthView != null) {
                monthView.clickThisMonth(monthView.getSelectYear(), monthView.getSelectMonth(), monthView.getSelectDay());
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    public void refresh() {
        if (null != mMonthAdapter) {
            mMonthAdapter.notifyDataSetChanged();
        }
    }

    /**　
     * 跳转到今天
     */
    public void setTodayToView() {
        Calendar currentCalendar = Calendar.getInstance();
        int cYear = currentCalendar.get(Calendar.YEAR);
        int cMonth = currentCalendar.get(Calendar.MONTH);
        int position = (cYear - ConstData.MIN_YEAR) * 12 + cMonth;
        setCurrentItem(position, false);

        MonthView monthView = mMonthAdapter.getViews().get(position);
        if (monthView != null) {
            Calendar calendar = Calendar.getInstance();
            monthView.clickThisMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        }
    }

    /**
     * 跳转到指定月份
     * @param timeMill
     * */
    public void changeDateToView(long timeMill) {
        Calendar positionCalendar = Calendar.getInstance();
        positionCalendar.setTimeInMillis(timeMill);
        int cYear = positionCalendar.get(Calendar.YEAR);
        int cMonth = positionCalendar.get(Calendar.MONTH);
        int position = (cYear - ConstData.MIN_YEAR) * 12 + cMonth;
        setCurrentItem(position, false);

        MonthView monthView = mMonthAdapter.getViews().get(position);
        if (monthView != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeMill);
            monthView.clickThisMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //监听整个父布局的touch事件
        if (isFromYearAnimRunning) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 设置点击日期监听
     * @param onCalendarClickListener
     */
    public void setOnCalendarClickListener(OnCalendarClickListener onCalendarClickListener) {
        mOnCalendarClickListener = onCalendarClickListener;
    }

    public SparseArray<MonthView> getMonthViews() {
        return mMonthAdapter.getViews();
    }

    public MonthView getCurrentMonthView() {
        return getMonthViews().get(getCurrentItem());
    }

    public void setYearAnimRunningFlag(boolean isFromYearAnimRunning) {
        this.isFromYearAnimRunning = isFromYearAnimRunning;
    }

}
