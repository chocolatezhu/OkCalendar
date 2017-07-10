package com.ppzhu.calendar.view.slidelayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.ppzhu.calendar.R;
import com.ppzhu.calendar.constants.ConstData;
import com.ppzhu.calendar.month.MonthCalendarView;
import com.ppzhu.calendar.month.MonthView;
import com.ppzhu.calendar.month.OnCalendarClickListener;
import com.ppzhu.calendar.utils.CalendarUtils;
import com.ppzhu.calendar.week.WeekCalendarView;
import com.ppzhu.calendar.week.WeekView;

import org.joda.time.DateTime;

import java.util.Calendar;

/**
 * 这里控制上下伸缩
 * Created by Jimmy on 2016/10/7 0007.
 */
public class ScheduleLayout extends FrameLayout {
    //布局是否初始化，用于第一次控制日程列表布局的Y轴距离
    private static boolean IS_INIT_LAYOUT = false;
    private static final int NUM_ROWS = 6;
    private MonthCalendarView mcvCalendar;
    private WeekCalendarView wcvCalendar;
    private RelativeLayout rlMonthCalendar;
    private RelativeLayout rlScheduleList;
    private ScheduleRecyclerView rvScheduleList;

    private int mCurrentSelectYear;
    private int mCurrentSelectMonth;
    private int mCurrentSelectDay;
    public int mRowSize;
    private int mMinDistance;
    private int mAutoScrollDistance;
    private float mDownPosition[] = new float[2];
    private boolean mIsScrolling = false;
    private int mCurrentMonthViewHeight;

    private int weekRowCount;

    private CalendarViewManager mCalendarViewManager;
    private OnCalendarClickListener mOnCalendarClickListener;
    private GestureDetector mGestureDetector;

    //是否从年视图切换，也是判断动画是否结束的标志
    private boolean isFromYearAnimRunning = false;

    public ScheduleLayout(Context context) {
        this(context, null);
    }

    public ScheduleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScheduleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs();
        initDate();
        initGestureDetector();
    }

    public void setCalendarViewManager(CalendarViewManager calendarViewManager) {
        mCalendarViewManager = calendarViewManager;
        mCalendarViewManager.setState(CalendarViewManager.ScheduleState.Month);
    }

    private void initAttrs() {
        IS_INIT_LAYOUT = true;
        mRowSize = getResources().getDimensionPixelSize(R.dimen.week_calendar_height);
        mMinDistance = getResources().getDimensionPixelSize(R.dimen.calendar_min_distance);
        mAutoScrollDistance = getResources().getDimensionPixelSize(R.dimen.auto_scroll_distance);
    }

    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(getContext(), new OnScheduleScrollListener(this));
    }

    private void initDate() {
        Calendar calendar = Calendar.getInstance();
        resetCurrentSelectDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mcvCalendar = (MonthCalendarView) findViewById(R.id.mcvCalendar);
        wcvCalendar = (WeekCalendarView) findViewById(R.id.wcvCalendar);
        rlMonthCalendar = (RelativeLayout) findViewById(R.id.rlMonthCalendar);
        rlScheduleList = (RelativeLayout) findViewById(R.id.rlScheduleList);
        rvScheduleList = (ScheduleRecyclerView) findViewById(R.id.rvScheduleList);
        bindingMonthAndWeekCalendar();
    }

    private void bindingMonthAndWeekCalendar() {
        mcvCalendar.setOnCalendarClickListener(mMonthCalendarClickListener);
        wcvCalendar.setOnCalendarClickListener(mWeekCalendarClickListener);
        mcvCalendar.setVisibility(VISIBLE);
        wcvCalendar.setVisibility(GONE);
    }

    public void resetCurrentSelectDate(int year, int month, int day) {
        mCurrentSelectYear = year;
        mCurrentSelectMonth = month;
        mCurrentSelectDay = day;
    }

    private OnCalendarClickListener mMonthCalendarClickListener = new OnCalendarClickListener() {
        @Override
        public void onClickDate(int year, int month, int day) {
            onMonthClick(year, month, day);
        }
    };

    /**
     * 月视图点击选中事件
     * */
    public void onMonthClick(int year, int month, int day) {
        wcvCalendar.setOnCalendarClickListener(null);
        resetCurrentSelectDate(year, month, day);

        DateTime mStartDate = new DateTime(year, month + 1, day, 0, 0, 0);
        mStartDate = mStartDate.plusDays(-mStartDate.getDayOfWeek() % 7);
        int position = CalendarUtils.getWeeksAgo(ConstData.MIN_YEAR, ConstData.MIN_MONTH - 1, ConstData.MIN_DAY, mStartDate.getYear(), mStartDate.getMonthOfYear() - 1, mStartDate.getDayOfMonth());
        wcvCalendar.setCurrentItem(position, false);
        resetWeekView();
        wcvCalendar.setOnCalendarClickListener (mWeekCalendarClickListener);
    }

    private void resetWeekView() {
        WeekView weekView = wcvCalendar.getCurrentWeekView();
        if (weekView != null) {
            weekView.setSelectYearMonth(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay);
            weekView.invalidate();
        } else {
            WeekView newWeekView = wcvCalendar.getWeekAdapter().instanceWeekView(wcvCalendar.getCurrentItem());
            newWeekView.setSelectYearMonth(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay);
            newWeekView.invalidate();
        }
        if (mOnCalendarClickListener != null) {
            mOnCalendarClickListener.onClickDate(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay);
        }
    }

    private OnCalendarClickListener mWeekCalendarClickListener = new OnCalendarClickListener() {
        @Override
        public void onClickDate(int year, int month, int day) {
            onWeekClick(year, month, day);
        }
    };

    /**
     * 周视图点击选中事件
     * */
    public void onWeekClick(int year, int month, int day) {
        mcvCalendar.setOnCalendarClickListener(null);
        int months = CalendarUtils.getMonthsAgo(mCurrentSelectYear, mCurrentSelectMonth, year, month);
        resetCurrentSelectDate(year, month, day);
        if (months != 0) {
            int position = mcvCalendar.getCurrentItem() + months;
            mcvCalendar.setCurrentItem(position, false);
        }
        resetMonthView();
        mcvCalendar.setOnCalendarClickListener(mMonthCalendarClickListener);
    }

    private void resetMonthView() {
        MonthView monthView = mcvCalendar.getCurrentMonthView();
        if (monthView != null) {
            monthView.setSelectYearMonth(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay);
            monthView.invalidate();
        }
        if (mOnCalendarClickListener != null) {
            mOnCalendarClickListener.onClickDate(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        weekRowCount = getWeekRows();
        mCurrentMonthViewHeight = mRowSize * weekRowCount;
        resetViewHeight(rlScheduleList, height - mRowSize);
        resetViewHeight(this, height);
//        resetViewHeight(mcvCalendar, mCurrentMonthViewHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void resetViewHeight(View view, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams.height != height) {
            layoutParams.height = height;
            view.setLayoutParams(layoutParams);
        }
    }

    /**
     * 获取当前月有几行，从星期日算起
     * */
    public int getWeekRows(){
        int numRows;
        int monthDays = CalendarUtils.getMonthDays(mCurrentSelectYear, mCurrentSelectMonth);
        int weekNumber = CalendarUtils.getFirstDayWeek(mCurrentSelectYear, mCurrentSelectMonth);
        //上个月占的天数
        int lastMonthDays = weekNumber - 1;
        //下个月占的天数
        int nextMonthDays = 42 - monthDays - weekNumber + 1;
        numRows = NUM_ROWS - (lastMonthDays / 7 + nextMonthDays / 7);

        return numRows;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (IS_INIT_LAYOUT) {
            //进入onCreate重新计算Y轴距离
            resetRlScheduleListLayout();
            IS_INIT_LAYOUT = false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //监听整个父布局的touch事件
        if (isFromYearAnimRunning) {
            return true;
        }
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownPosition[0] = ev.getRawX();
                mDownPosition[1] = ev.getRawY();
                mGestureDetector.onTouchEvent(ev);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mIsScrolling) {
            return true;
        }
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                float x = ev.getRawX();
                float y = ev.getRawY();
                float distanceX = Math.abs(x - mDownPosition[0]);
                float distanceY = Math.abs(y - mDownPosition[1]);
                if (distanceY > mMinDistance && distanceY > distanceX * 1.2f) {
                    return (y > mDownPosition[1] && isRecyclerViewTouch()) || (y < mDownPosition[1] && mCalendarViewManager.getState() == CalendarViewManager.ScheduleState.Month);
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private boolean isRecyclerViewTouch() {
        return mCalendarViewManager.getState() == CalendarViewManager.ScheduleState.Week && (rvScheduleList.getChildCount() == 0 || rvScheduleList.isScrollTop());
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownPosition[0] = event.getRawX();
                mDownPosition[1] = event.getRawY();
                resetCalendarPosition();
                return true;
            case MotionEvent.ACTION_MOVE:
                transferEvent(event);
                mIsScrolling = true;
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                transferEvent(event);
                changeCalendarState();
                resetScrollingState();
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void transferEvent(MotionEvent event) {
        if (mCalendarViewManager.getState() == CalendarViewManager.ScheduleState.Week) {
            mcvCalendar.setVisibility(VISIBLE);
            wcvCalendar.setVisibility(INVISIBLE);
            mGestureDetector.onTouchEvent(event);
        } else {
            mGestureDetector.onTouchEvent(event);
        }
    }

    /**
     * 重置日程列别布局Y轴坐标
     * */
    private void resetRlScheduleListLayout() {
        if (null != mCalendarViewManager) {
            if (mCalendarViewManager.getState() == CalendarViewManager.ScheduleState.Week) {
                rlScheduleList.setY(mRowSize);
            } else {
                rlScheduleList.setY(getWeekRows() * mRowSize);
            }
        } else {
            rlScheduleList.setY(getWeekRows() * mRowSize);
        }
    }


    private void changeCalendarState() {
        if (rlScheduleList.getY() > mRowSize * 2 &&
                rlScheduleList.getY() < mcvCalendar.getHeight() - mRowSize) { // 位于中间
            ScheduleAnimation animation = new ScheduleAnimation(this, mCalendarViewManager.getState(), mAutoScrollDistance);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    changeState();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            rlScheduleList.startAnimation(animation);
        } else if (rlScheduleList.getY() <= mRowSize * 2) { // 位于顶部
            ScheduleAnimation animation = new ScheduleAnimation(this, CalendarViewManager.ScheduleState.Month, mAutoScrollDistance);
            animation.setDuration(50);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mCalendarViewManager.getState() == CalendarViewManager.ScheduleState.Month) {
                        changeState();
                    } else {
                        resetCalendar();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            rlScheduleList.startAnimation(animation);
        } else {
            ScheduleAnimation animation = new ScheduleAnimation(this, CalendarViewManager.ScheduleState.Week, mAutoScrollDistance);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mCalendarViewManager.getState() == CalendarViewManager.ScheduleState.Week) {
                        mCalendarViewManager.setState(CalendarViewManager.ScheduleState.Month);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            rlScheduleList.startAnimation(animation);
        }
    }

    private void resetCalendarPosition() {
        if (mCalendarViewManager.getState() == CalendarViewManager.ScheduleState.Month) {
            rlMonthCalendar.setY(0);
            rlScheduleList.setY(mCurrentMonthViewHeight);
        } else {
            rlMonthCalendar.setY(-CalendarUtils.getWeekRow(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay) * mRowSize);
            rlScheduleList.setY(mRowSize);
        }
    }

    private void resetCalendar() {
        if (mCalendarViewManager.getState() == CalendarViewManager.ScheduleState.Month) {
            mcvCalendar.setVisibility(VISIBLE);
            wcvCalendar.setVisibility(INVISIBLE);
        } else {
            mcvCalendar.setVisibility(INVISIBLE);
            wcvCalendar.setVisibility(VISIBLE);
        }
    }

    private void changeState() {
        if (mCalendarViewManager.getState() == CalendarViewManager.ScheduleState.Month) {
            mCalendarViewManager.setState(CalendarViewManager.ScheduleState.Week);
            mcvCalendar.setVisibility(INVISIBLE);
            wcvCalendar.setVisibility(VISIBLE);
            rlMonthCalendar.setY((1 - mcvCalendar.getCurrentMonthView().getWeekRow()) * mRowSize);
        } else {
            mCalendarViewManager.setState(CalendarViewManager.ScheduleState.Month);
            mcvCalendar.setVisibility(VISIBLE);
            wcvCalendar.setVisibility(INVISIBLE);
            rlMonthCalendar.setY(0);
        }
    }

    private void resetScrollingState() {
        mDownPosition[0] = 0;
        mDownPosition[1] = 0;
        mIsScrolling = false;
    }

    protected void onCalendarScroll(float distanceY) {
        MonthView monthView = mcvCalendar.getCurrentMonthView();
        distanceY = Math.min(distanceY, mAutoScrollDistance);
        float calendarDistanceY = distanceY / (weekRowCount -1);
//        float calendarDistanceY = distanceY / 5.0f;
        int row = monthView.getWeekRow() - 1;
        int calendarTop = -row * mRowSize;
        int scheduleTop = mRowSize;
        float calendarY = rlMonthCalendar.getY() - calendarDistanceY * row;
        calendarY = Math.min(calendarY, 0);
        calendarY = Math.max(calendarY, calendarTop);
        rlMonthCalendar.setY(calendarY);
        float scheduleY = rlScheduleList.getY() - distanceY;
        scheduleY = Math.min(scheduleY, mCurrentMonthViewHeight);
        scheduleY = Math.max(scheduleY, scheduleTop);
        rlScheduleList.setY(scheduleY);
    }

    public void setOnCalendarClickListener(OnCalendarClickListener onCalendarClickListener) {
        mOnCalendarClickListener = onCalendarClickListener;
    }

    public ScheduleRecyclerView getSchedulerRecyclerView() {
        return rvScheduleList;
    }

    public MonthCalendarView getMonthCalendar() {
        return mcvCalendar;
    }

    public WeekCalendarView getWeekCalendar() {
        return wcvCalendar;
    }

    public int getCurrentSelectYear() {
        return mCurrentSelectYear;
    }

    public int getCurrentSelectMonth() {
        return mCurrentSelectMonth;
    }

    public int getCurrentSelectDay() {
        return mCurrentSelectDay;
    }

    public void setYearAnimRunningFlag(boolean isFromYearAnimRunning) {
        this.isFromYearAnimRunning = isFromYearAnimRunning;
    }
}
