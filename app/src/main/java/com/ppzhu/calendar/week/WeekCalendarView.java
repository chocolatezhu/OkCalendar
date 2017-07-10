package com.ppzhu.calendar.week;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;


import com.ppzhu.calendar.R;
import com.ppzhu.calendar.constants.ConstData;
import com.ppzhu.calendar.month.OnCalendarClickListener;
import com.ppzhu.calendar.utils.CalendarUtils;

import org.joda.time.DateTime;

import java.util.Calendar;


/**
 * Created by Jimmy on 2016/10/7 0007.
 */
public class WeekCalendarView extends ViewPager implements OnWeekClickListener {

    private OnCalendarClickListener mOnCalendarClickListener;
    private WeekAdapter mWeekAdapter;

    private DateTime mStartDate;

    public WeekCalendarView(Context context) {
        this(context, null);
    }

    public WeekCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        addOnPageChangeListener(mOnPageChangeListener);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        initWeekAdapter(context, context.obtainStyledAttributes(attrs, R.styleable.WeekCalendarView));
    }

    private void initWeekAdapter(Context context, TypedArray array) {
        mWeekAdapter = new WeekAdapter(context, array, this);
        setAdapter(mWeekAdapter);
        mStartDate = mWeekAdapter.mStartDate;
        int position = CalendarUtils.getWeeksAgo(ConstData.MIN_YEAR, ConstData.MIN_MONTH - 1, ConstData.MIN_DAY, mStartDate.getYear(), mStartDate.getMonthOfYear() - 1, mStartDate.getDayOfMonth());
        setCurrentItem(position, false);
    }

    @Override
    public void onClickDate(int year, int month, int day) {
        if (mOnCalendarClickListener != null) {
            mOnCalendarClickListener.onClickDate(year, month, day);
        }
    }

    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            WeekView weekView = mWeekAdapter.getViews().get(position);
            if (weekView != null) {
                weekView.clickThisWeek(weekView.getSelectYear(), weekView.getSelectMonth(), weekView.getSelectDay());
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public void refresh() {
        if (null != mWeekAdapter) {
            mWeekAdapter.notifyDataSetChanged();
        }
    }

    /**　
     * 跳转到今天
     */
    public void setTodayToView() {
        Calendar currentCalendar = Calendar.getInstance();
        DateTime mStartDate = new DateTime(currentCalendar);
        mStartDate = mStartDate.plusDays(-mStartDate.getDayOfWeek() % 7);
        int weekViewPosition = CalendarUtils.getWeeksAgo(ConstData.MIN_YEAR, ConstData.MIN_MONTH - 1, ConstData.MIN_DAY, mStartDate.getYear(), mStartDate.getMonthOfYear() - 1, mStartDate.getDayOfMonth());
        setCurrentItem(weekViewPosition, false);

        WeekView weekView = mWeekAdapter.getViews().get(weekViewPosition);
        if (weekView != null) {
            Calendar calendar = Calendar.getInstance();
            weekView.clickThisWeek(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        }
    }

    /**
     * 设置点击日期监听
     * @param onCalendarClickListener
     */
    public void setOnCalendarClickListener(OnCalendarClickListener onCalendarClickListener) {
        mOnCalendarClickListener = onCalendarClickListener;
    }

    public SparseArray<WeekView> getWeekViews() {
        return mWeekAdapter.getViews();
    }

    public WeekAdapter getWeekAdapter() {
        return mWeekAdapter;
    }

    public WeekView getCurrentWeekView() {
        return getWeekViews().get(getCurrentItem());
    }

}
