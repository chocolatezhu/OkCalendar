package com.ppzhu.calendar.year;

import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.ppzhu.calendar.R;
import com.ppzhu.calendar.constants.ConstData;

import java.util.Calendar;

public class YearViewFragment extends Fragment {

    private View mContentView;
    long timeInMillis;
    Time mSelected = new Time();
    private YearView mYearView;
    private Handler mHandler;

    private RectF mYearRectF;

    // 在午夜更新界面。
    protected Runnable mTodayUpdater = new Runnable() {
        @Override
        public void run() {
            Time midnight = new Time(mSelected.timezone);
            midnight.setToNow();
            long currentMillis = midnight.toMillis(true);
            midnight.hour = 0;
            midnight.minute = 0;
            midnight.second = 0;
            midnight.monthDay++;
            setSelectTime(midnight, this, currentMillis);
        }
    };

    private void setSelectTime(Time midnight, Runnable runna, long currentMillis) {
        long millisToMidnight = midnight.normalize(true) - currentMillis;
        mHandler.postDelayed(runna, millisToMidnight);
        mSelected.setToNow();
        mYearView.setselectedtime(timeInMillis, mSelected);
    }


    public YearViewFragment() {
        super();
        mHandler = new Handler();
    }

    public static YearViewFragment newInstance(long timeInMillis, Time mSelectedDay) {
        YearViewFragment mYearViewFragment = new YearViewFragment();
        Bundle args = new Bundle();
        args.putLong(ConstData.SELECT_DAY, mSelectedDay.toMillis(true));
        args.putLong(ConstData.TIME_MILLIS, timeInMillis);
        mYearViewFragment.setArguments(args);
        return mYearViewFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fram_year_view_layout, container, false);
        Bundle args = getArguments();
        mSelected.set(args.getLong(ConstData.SELECT_DAY));
        timeInMillis = args.getLong(ConstData.TIME_MILLIS);
        initView();
        return mContentView;
    }

    private void initView() {
        mYearView = (YearView) mContentView.findViewById(R.id.yearview);
        mYearView.setselectedtime(timeInMillis, mSelected);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mTodayUpdater);
    }

    @Override
    public void onResume() {
        super.onResume();
        doResumeUpdates();
    }


    protected void doResumeUpdates() {
        mSelected.setToNow();
        mYearView.setselectedtime(timeInMillis, mSelected);
        mTodayUpdater.run();
    }

    public static Fragment create(int position, Time mSelectedDay) {
        //这里设置默认选中的日期时间，从年视图返回月视图默认选中的日期时间
        Calendar calendar = Calendar.getInstance();
        calendar.set(ConstData.MIN_YEAR, 0, 1);
        calendar.add(Calendar.YEAR, position - 1);
        long timeInMillis = calendar.getTimeInMillis();
        return YearViewFragment.newInstance(timeInMillis, mSelectedDay);
    }
}
