package com.ppzhu.calendar.month;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


import com.ppzhu.calendar.R;
import com.ppzhu.calendar.decorator.TaskHint;
import com.ppzhu.calendar.festival.ChinaDate;
import com.ppzhu.calendar.utils.CalendarUtils;
import com.ppzhu.calendar.utils.LunarCalendar;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Jimmy on 2016/10/6 0006.
 */
public class MonthView extends View {

    private static final int NUM_COLUMNS = 7;
    private static final int NUM_ROWS = 6;
    private Paint mPaint;
    private Paint mChinaDayPaint;
    private Paint mDayBgPaint;
    private Paint mLinePaint;
    private Paint mHintCirclePaint;
    private Paint mFestivalPaint;
    private int mNormalDayColor;
    private int mSelectDayColor;
    private int mSelectBGColor;
    private int mSelectBGTodayColor;
    private int mCurrentDayColor;
    private int mLineColor;
    private int mHintCircleColor;
    private int mLastOrNextMonthTextColor;
    private int mNormalChinaDayColor;
    private int mFestivalWorkTextColor;
    private int mFestivalRestTextColor;
    private int mWeekNormalColor;
    private int mWeekSelectTextColor;
    private int mCurrYear, mCurrMonth, mCurrDay;
    private int mSelYear, mSelMonth, mSelDay;
    private int mColumnSize, mRowSize;
    private float mSelectCircleSize;
    private float mRadiusRate;
    private int mWeekRow; // 当前月份第几周
    private float mCircleRadius;
    private int[][] mDaysText;
    private boolean mIsShowHint;
    private boolean mIsShowHunar;
    private DisplayMetrics mDisplayMetrics;
    private OnMonthClickListener mDateClickListener;
    private GestureDetector mGestureDetector;
    private boolean[] mTaskHintCircle;
    private int[] festivalHint;

    private Typeface mTypeFace;

    private ExecutorService singleExecutor;

    public MonthView(Context context, int year, int month) {
        this(context, null, year, month);
    }

    public MonthView(Context context, TypedArray array, int year, int month) {
        this(context, array, null, year, month);
    }

    public MonthView(Context context, TypedArray array, AttributeSet attrs, int year, int month) {
        this(context, array, attrs, 0, year, month);
    }

    public MonthView(Context context, TypedArray array, AttributeSet attrs, int defStyleAttr, int year, int month) {
        super(context, attrs, defStyleAttr);
        initAttrs(array, year, month);
        initPaint();
        initMonth();
        initGestureDetector();
        //这里可以异步获取数据
        initTaskHint();
    }

    private void initTaskHint() {
        if (mIsShowHint) {
            // 从数据库中获取圆点提示数据
            if (null == singleExecutor) {
                singleExecutor = Executors.newSingleThreadExecutor();
            }
            singleExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    TaskHint taskHint = TaskHint.getInstance(getContext().getApplicationContext());
                    mTaskHintCircle = taskHint.geTaskHintByMonth(mSelYear, mSelMonth);
                    festivalHint = taskHint.getFestivalHintByMonth(mSelYear, mSelMonth);
                    if ((null != mTaskHintCircle && mTaskHintCircle.length > 0) || (null != festivalHint && festivalHint.length > 0) ) {
                        postInvalidate();
                    }
                }
            });
        }
    }

    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                //这里处理点击事件
                try {
                    doClickAction((int) e.getX(), (int) e.getY());
                } catch (Exception exception) {

                }

                return true;
            }
        });
    }

    private void initAttrs(TypedArray array, int year, int month) {
        if (array != null) {
            mSelectDayColor = array.getColor(R.styleable.MonthCalendarView_month_selected_text_color, Color.parseColor("#FFFFFF"));
            mSelectBGColor = array.getColor(R.styleable.MonthCalendarView_month_selected_circle_color, Color.parseColor("#f8cfbe"));
            mSelectBGTodayColor = array.getColor(R.styleable.MonthCalendarView_month_selected_circle_today_color, Color.parseColor("#e75757"));
            mNormalDayColor = array.getColor(R.styleable.MonthCalendarView_month_normal_text_color, Color.parseColor("#363636"));
            mNormalChinaDayColor = array.getColor(R.styleable.MonthCalendarView_month_normal_china_day_color, Color.parseColor("#545454"));
            mCurrentDayColor = array.getColor(R.styleable.MonthCalendarView_month_today_text_color, Color.parseColor("#ff763f"));
            mLineColor = array.getColor(R.styleable.MonthCalendarView_month_line_color, Color.parseColor("#e75757"));
            mHintCircleColor = array.getColor(R.styleable.MonthCalendarView_month_hint_circle_color, Color.parseColor("#aaaaaa"));
            mLastOrNextMonthTextColor = array.getColor(R.styleable.MonthCalendarView_month_last_or_next_month_text_color, Color.parseColor("#ACA9BC"));
            mFestivalWorkTextColor = array.getColor(R.styleable.MonthCalendarView_month_festival_work_text_color, Color.parseColor("#13be67"));
            mFestivalRestTextColor = array.getColor(R.styleable.MonthCalendarView_month_festival_rest_text_color, Color.parseColor("#f55a5a"));
            mWeekSelectTextColor = Color.parseColor("#777777");
            mWeekNormalColor = Color.parseColor("#c2c2c2");
            mIsShowHint = array.getBoolean(R.styleable.MonthCalendarView_month_show_task_hint, true);
            mIsShowHunar = array.getBoolean(R.styleable.MonthCalendarView_month_show_lunar, true);
        } else {
            mSelectDayColor = Color.parseColor("#FFFFFF");
            mSelectBGColor = Color.parseColor("#f8cfbe");
            mSelectBGTodayColor = Color.parseColor("#e75757");
            mNormalDayColor = Color.parseColor("#363636");
            mNormalChinaDayColor = Color.parseColor("#545454");
            mCurrentDayColor = Color.parseColor("#ff763f");
            mLineColor = Color.parseColor("#e75757");
            mHintCircleColor = Color.parseColor("#aaaaaa");
            mLastOrNextMonthTextColor = Color.parseColor("#ACA9BC");
            mFestivalWorkTextColor = Color.parseColor("#13be67");
            mFestivalRestTextColor = Color.parseColor("#f55a5a");
            mWeekSelectTextColor = Color.parseColor("#777777");
            mWeekNormalColor = Color.parseColor("#c2c2c2");
            mIsShowHint = true;
            mIsShowHunar = true;
        }
        mSelYear = year;
        mSelMonth = month;
        mRadiusRate = 1.0f;
    }

    private void initPaint() {
        mDisplayMetrics = getResources().getDisplayMetrics();
        mTypeFace = Typeface.createFromAsset(getResources().getAssets(), "fonts/calendar_font.ttf");
        initDayPaint();
        initChinaDayPaint();
        initDayBgPaint();
        initLinePaint();
        initFestivalPaint();
        initHintCirclePaint();
    }

    private void initDayPaint() {
        mPaint = new Paint();
        mPaint.setTypeface(mTypeFace);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(getContext().getResources().getDimension(R.dimen.DIMEN_48PX));
    }

    private void initChinaDayPaint() {
        mChinaDayPaint = new Paint();
        mChinaDayPaint.setTypeface(mTypeFace);
        mChinaDayPaint.setAntiAlias(true);
        mChinaDayPaint.setTextSize(getContext().getResources().getDimension(R.dimen.DIMEN_22PX));
    }

    private void initDayBgPaint() {
        mDayBgPaint = new Paint();
        mDayBgPaint.setAntiAlias(true);
    }

    private void initLinePaint() {
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(getContext().getResources().getDimension(R.dimen.DIMEN_3PX));
    }

    private void initFestivalPaint() {
        mFestivalPaint = new Paint();
        mFestivalPaint.setTypeface(mTypeFace);
        mFestivalPaint.setAntiAlias(true);
        mFestivalPaint.setTextSize(getContext().getResources().getDimension(R.dimen.DIMEN_20PX));
    }

    private void initHintCirclePaint() {
        mHintCirclePaint = new Paint();
        mHintCirclePaint.setAntiAlias(true);
        mCircleRadius = getContext().getResources().getDimension(R.dimen.DIMEN_5PX);
    }

    private void initMonth() {
        Calendar calendar = Calendar.getInstance();
        mCurrYear = calendar.get(Calendar.YEAR);
        mCurrMonth = calendar.get(Calendar.MONTH);
        mCurrDay = calendar.get(Calendar.DATE);
        if (mSelYear == mCurrYear && mSelMonth == mCurrMonth) {
            setSelectYearMonth(mSelYear, mSelMonth, mCurrDay);
        } else {
            setSelectYearMonth(mSelYear, mSelMonth, 1);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0;
        int height = 0;

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            height = mDisplayMetrics.densityDpi * 200;
        } else {
            height = heightSize;
        }
        if (widthMode == MeasureSpec.AT_MOST) {
            width = mDisplayMetrics.densityDpi * 300;
        } else {
            width = widthSize;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long start = System.currentTimeMillis();
        initSize();
        clearData();
        drawThisMonth(canvas);
        drawLunarText(canvas);
    }

    private void initSize() {
        mColumnSize = getWidth() / NUM_COLUMNS;
        mRowSize = getResources().getDimensionPixelSize(R.dimen.week_calendar_height);
//        mSelectCircleSize = mColumnSize / 2.45f;
        mSelectCircleSize = mRowSize / 2.45f;
    }

    private void clearData() {
        mDaysText = new int[6][7];
    }

    private void drawThisMonth(Canvas canvas) {
        String dayString;
        int monthDays = CalendarUtils.getMonthDays(mSelYear, mSelMonth);
        int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
        for (int day = 0; day < monthDays; day++) {
            dayString = String.valueOf(day + 1);
            int column = (day + weekNumber - 1) % 7;
            int row = (day + weekNumber - 1) / 7;
            mDaysText[row][column] = day + 1;
            //mPaint.measureText(dayString)测量文字的宽度
            //mPaint.ascent() + mPaint.descent()测量文字的高度
            int startX = (int) (mColumnSize * column + (mColumnSize - mPaint.measureText(dayString)) / 2);
            int startY = (int) (mRowSize * row + mRowSize / 2 - (mPaint.ascent() + mPaint.descent()) * 0.1f);

            //绘制选中日期时的文字颜色
            if (dayString.equals(String.valueOf(mSelDay))) {
                int startRecX = mColumnSize * column;
                int startRecY = mRowSize * row;
                int endRecX = startRecX + mColumnSize;
                int endRecY = startRecY + mRowSize;
                if (mSelYear == mCurrYear && mCurrMonth == mSelMonth && day + 1 == mCurrDay) {
                    mDayBgPaint.setColor(mSelectBGTodayColor);
                } else {
                    mDayBgPaint.setColor(mSelectBGColor);
                }

                //绘制选中日期圆圈
                canvas.drawCircle((startRecX + endRecX) / 2, ((startRecY + endRecY) / 2) - mRowSize * 0.06f, mSelectCircleSize * mRadiusRate, mDayBgPaint);
                mWeekRow = row + 1;
            }

            //如果选中日期是今天
            if (dayString.equals(String.valueOf(mSelDay)) && (mSelYear == mCurrYear && mCurrMonth == mSelMonth && day + 1 == mCurrDay)) {
                mPaint.setColor(mSelectDayColor);
                mFestivalPaint.setColor(mSelectDayColor);
            } else {
                //如果是今天
                if (mSelYear == mCurrYear && mCurrMonth == mSelMonth && day + 1 == mCurrDay) {
                    mPaint.setColor(mSelectBGTodayColor);
                } else {
                    //如果是周末
                    if (CalendarUtils.isWeekend(mSelYear, mSelMonth, day + 1)) {
                        if (CalendarUtils.isWeekend(mSelYear, mSelMonth, day + 1)) {
                            if (day + 1 == mSelDay) {
                                //周末选中颜色
                                mPaint.setColor(mWeekSelectTextColor);
                            } else {
                                //周末灰显
                                mPaint.setColor(mWeekNormalColor);
                                mFestivalPaint.setColor(mNormalDayColor);
                            }
                        }
                    }else {
                        //一般情况
                        mPaint.setColor(mNormalDayColor);
                    }
                }
            }
            canvas.drawText(dayString, startX, startY, mPaint);

            //绘制日程点
            drawHintCircle(row, column, day, canvas);
            //绘制放假信息
            drawHoliday(startY, column, day, canvas);
        }
    }

    /**
     * 绘制节假日信息
     * */
    private void drawHoliday(int startY, int column, int day, Canvas canvas) {
        //绘制“班”和“休”
        String festivalStr;
        if (festivalHint != null && festivalHint.length > 0) {
            int festivalStartX;
            int festivalStartY = (int) (startY - mPaint.getTextSize());
            if (0 == festivalHint[day]) {
                festivalStr = "班";
                if (!(mSelYear == mCurrYear && mCurrMonth == mSelMonth && day + 1 == mCurrDay)) {
                    mFestivalPaint.setColor(mFestivalWorkTextColor);
                }
                festivalStartX = (int) (mColumnSize * column + (mColumnSize - mFestivalPaint.measureText(festivalStr)) / 2);
                canvas.drawText(festivalStr, festivalStartX, festivalStartY, mFestivalPaint);
            } else if (1 == festivalHint[day]) {
                festivalStr = "休";
                if (!(mSelYear == mCurrYear && mCurrMonth == mSelMonth && day + 1 == mCurrDay)) {
                    mFestivalPaint.setColor(mFestivalRestTextColor);
                }
                festivalStartX = (int) (mColumnSize * column + (mColumnSize - mFestivalPaint.measureText(festivalStr)) / 2);
                canvas.drawText(festivalStr, festivalStartX, festivalStartY, mFestivalPaint);
            }
        }
    }

    /**
     * 绘制农历
     *
     * @param canvas
     */
    private void drawLunarText(Canvas canvas) {
        if (mIsShowHunar) {
            int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
            int monthDays = CalendarUtils.getMonthDays(mSelYear, mSelMonth);

            for (int i = 0; i < monthDays; i++) {
                int column = (i + weekNumber - 1) % 7;
                int row = (i + weekNumber - 1) / 7;

                long[] l = LunarCalendar.computeLunarLimitMaxDate(mSelYear, mSelMonth, i + 1);
                String dayString = ChinaDate.getFestivalAndSolar(mSelYear, mSelMonth + 1, i + 1, l);

                float startX = (int) (mColumnSize * column + (mColumnSize - mChinaDayPaint.measureText(dayString)) / 2);
                float startY = (int) (mRowSize * row + mRowSize * 0.65 - (mChinaDayPaint.ascent() + mChinaDayPaint.descent()) / 2);

                //线的长度和农历日期文字长度一样
                float lineStartX = (mColumnSize * column + (mColumnSize - mChinaDayPaint.measureText(dayString)) / 2);
                float lineStartY = startY + getContext().getResources().getDimension(R.dimen.DIMEN_10PX);
                float lineStopX = (mColumnSize * column + (mColumnSize - mChinaDayPaint.measureText(dayString)) / 2) + mChinaDayPaint.measureText(dayString);
                float lineStopY = startY + getContext().getResources().getDimension(R.dimen.DIMEN_10PX);

                if (i + 1 == mSelDay && (mSelYear == mCurrYear && mCurrMonth == mSelMonth && i + 1 == mCurrDay)) {
                    mChinaDayPaint.setColor(mSelectDayColor);
                } else {
                    if (mSelYear == mCurrYear && mCurrMonth == mSelMonth && i + 1 == mCurrDay) {
                        //如果是今天
                        mChinaDayPaint.setColor(mSelectBGTodayColor);
                    } else {
                        //如果是周末
                        if (CalendarUtils.isWeekend(mSelYear, mSelMonth, i + 1)){
                            if (i + 1 == mSelDay) {
                                //周末选中颜色
                                mChinaDayPaint.setColor(mWeekSelectTextColor);
                            } else {
                                //周末灰显
                                mChinaDayPaint.setColor(getResources().getColor(R.color.weekend_txt_color));
                            }
                        } else {
                            //一般情况
                            mChinaDayPaint.setColor(mNormalChinaDayColor);
                        }
                    }

                    //如果今天是初一就不用画初一的横线了
                    if (i + 1 != mCurrDay && 1 == l[2] || dayString.contains("月")) {
                        mLinePaint.setColor(mLineColor);
                        //绘农历初一下划线
                        canvas.drawLine(lineStartX, lineStartY, lineStopX, lineStopY, mLinePaint);
                    }
                }

                canvas.drawText(dayString, startX, startY, mChinaDayPaint);
            }
        }
    }

    /**
     * 绘制圆点提示
     * @param column
     * @param day
     * @param canvas
     */
    private void drawHintCircle(int row, int column, int day, Canvas canvas) {
        if (null != mTaskHintCircle && mTaskHintCircle.length > 0) {
            if (mTaskHintCircle[day]) {
                mHintCirclePaint.setColor(mHintCircleColor);
                float circleX = (float) (mColumnSize * column + mColumnSize * 0.5);
                float circleY = (float) (mRowSize * row + mRowSize * 0.76 + mChinaDayPaint.getTextSize());
                canvas.drawCircle(circleX, circleY, mCircleRadius, mHintCirclePaint);
            }
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_UP:
                try {
                    doClickAction(x, y);
                } catch (Exception e) {
                }
                break;
            default:
                break;
        }
        return true;
//        return mGestureDetector.onTouchEvent(event);
    }

    public void setSelectYearMonth(int year, int month, int day) {
        mSelYear = year;
        mSelMonth = month;
        mSelDay = day;
    }

    /**
     * 处理点击事件
     * */
    private void doClickAction(int x, int y) {
        if (y > getHeight()) {
            return;
        }
        int row = 0;
        int column = 0;
        if (0 != mRowSize) {
            row = y / mRowSize;
        }
        if (0 != mColumnSize) {
            column = x / mColumnSize;
        }
        column = Math.min(column, 6);
        int clickYear = mSelYear, clickMonth = mSelMonth;
        if (row == 0) {
            if (mDaysText[row][column] >= 23 || mDaysText[row][column] == 0) {
                return;
            } else {
                clickThisMonth(clickYear, clickMonth, mDaysText[row][column]);
                initAnim();
            }
        } else {
            int monthDays = CalendarUtils.getMonthDays(mSelYear, mSelMonth);
            int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
            int nextMonthDays = 42 - monthDays - weekNumber + 1;
            if (mDaysText[row][column] <= nextMonthDays && row >= 4) {
                return;
            } else {
                clickThisMonth(clickYear, clickMonth, mDaysText[row][column]);
                initAnim();
            }
        }
    }

    /**
     * 跳转到某日期
     * @param year
     * @param month
     * @param day
     */
    public void clickThisMonth(int year, int month, int day) {
        if (mDateClickListener != null) {
            mDateClickListener.onClickThisMonth(year, month, day);
        }
        setSelectYearMonth(year, month, day);
        invalidate();
    }

    /**
     * 初始化点击动画
     * */
    private void initAnim() {
        ValueAnimator animator = ValueAnimator.ofFloat(1, 1.1f, 1);
        animator.setDuration(300);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRadiusRate = (float)animation.getAnimatedValue();
                invalidate();
            }
        });
    }

    /**
     * 获取当前选择年
     * @return
     */
    public int getSelectYear() {
        return mSelYear;
    }

    /**
     * 获取当前选择月
     * @return
     */
    public int getSelectMonth() {
        return mSelMonth;
    }

    /**
     * 获取当前选择日
     * @return
     */
    public int getSelectDay() {
        return this.mSelDay;
    }



    public int getCurrentYear() {
        return mCurrYear;
    }

    public void setCurrentYear(int mCurrYear) {
        this.mCurrYear = mCurrYear;
    }

    public int getCurrentMonth() {
        return mCurrMonth;
    }

    public void setCurrentMonth(int mCurrMonth) {
        this.mCurrMonth = mCurrMonth;
    }

    public int getCurrentDay() {
        return mCurrDay;
    }

    public void setCurrentDay(int mCurrDay) {
        this.mCurrDay = mCurrDay;
    }

    public int getRowSize() {
        return mRowSize;
    }

    public int getWeekRow() {
        return mWeekRow;
    }

    /**
     * 设置圆点提示的集合
     * <p> 刷新日程圆点调用改方法 </p>
     * @param taskHintCircle
     */
    public void setTaskHintCircle(boolean[] taskHintCircle) {
        mTaskHintCircle = taskHintCircle;
        invalidate();
    }

    /**
     * 添加一个圆点提示
     * @param day
     * @deprecated
     */
    public void addTaskHintCircle(Integer day) {
        if (null != mTaskHintCircle) {
            if (!mTaskHintCircle[day]) {
                mTaskHintCircle[day] = true;
                invalidate();
            }
        }
    }

    /**
     * 删除一个圆点提示
     * @param day
     * @deprecated
     * <P> 同一个日期下可能有多个日程，此时该方法并不适用 </P>
     */
    public void removeTaskHintCircle(Integer day) {
        if (null != mTaskHintCircle) {
            mTaskHintCircle[day] = false;
            invalidate();
        }
    }

    /**
     * 设置点击日期监听
     * @param dateClickListener
     */
    public void setOnDateClickListener(OnMonthClickListener dateClickListener) {
        this.mDateClickListener = dateClickListener;
    }

}

