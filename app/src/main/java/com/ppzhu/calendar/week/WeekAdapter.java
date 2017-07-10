package com.ppzhu.calendar.week;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;


import com.ppzhu.calendar.constants.ConstData;
import com.ppzhu.calendar.utils.CalendarUtils;

import org.joda.time.DateTime;

/**
 * Created by Jimmy on 2016/10/7 0007.
 */
public class WeekAdapter extends PagerAdapter {

    private SparseArray<WeekView> mViews;
    private Context mContext;
    private TypedArray mArray;
    private WeekCalendarView mWeekCalendarView;
    public DateTime mStartDate;
    public static final int WEEK_COUNT = ConstData.MAX_WEEK_COUNT;

    public WeekAdapter(Context context, TypedArray array, WeekCalendarView weekCalendarView) {
        mContext = context;
        mArray = array;
        mWeekCalendarView = weekCalendarView;
        mViews = new SparseArray<>();
        initStartDate();
    }

    private void initStartDate() {
        mStartDate = new DateTime();
        mStartDate = mStartDate.plusDays(-mStartDate.getDayOfWeek() % 7);
    }

    @Override
    public int getCount() {
        return WEEK_COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mViews.get(position) == null) {
            instanceWeekView(position);
        }
        container.addView(mViews.get(position));
        return mViews.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.remove(position);
    }

    @Override
    public int getItemPosition(Object object) {
        //复写该方法，调用notifyDataSetChanged()时会清除缓存重新加载
        return POSITION_NONE;
    }

    public SparseArray<WeekView> getViews() {
        return mViews;
    }

    public int getWeekCount() {
        return WEEK_COUNT;
    }

    public WeekView instanceWeekView(int position) {
        WeekView weekView = new WeekView(mContext, mArray, mStartDate.plusWeeks(getPlusWeek(position)));
        weekView.setId(position);
        weekView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        weekView.setOnWeekClickListener(mWeekCalendarView);
        weekView.invalidate();
        mViews.put(position, weekView);
        return weekView;
    }

    private int getPlusWeek(int position) {
        int weekCount = CalendarUtils.getWeeksAgo(ConstData.MIN_YEAR, ConstData.MIN_MONTH - 1, ConstData.MIN_DAY, mStartDate.getYear(), mStartDate.getMonthOfYear() - 1, mStartDate.getDayOfMonth());
        return position - weekCount;
    }

}
