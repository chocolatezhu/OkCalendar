package com.ppzhu.calendar.week;

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
import com.ppzhu.calendar.constants.ConstData;
import com.ppzhu.calendar.decorator.TaskHint;
import com.ppzhu.calendar.festival.ChinaDate;
import com.ppzhu.calendar.utils.CalendarUtils;
import com.ppzhu.calendar.utils.LunarCalendar;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Jimmy on 2016/10/7 0007.
 */
public class WeekView extends View {

    private static final int NUM_COLUMNS = 7;
    private Paint mPaint;
    private Paint mChinaDayPaint;
    private Paint mDayBgPaint;
    private Paint mLinePaint;
    private Paint mFestivalPaint;
    private Paint mHintCirclePaint;
    private int mLastOrNextMonthTextColor;
    private int mNormalDayColor;
    private int mNormalChinaDayColor;
    private int mSelectDayColor;
    private int mSelectBGColor;
    private int mSelectBGTodayColor;
    private int mCurrentDayColor;
    private int mLineColor;
    private int mHintCircleColor;
    private int mFestivalWorkTextColor;
    private int mFestivalRestTextColor;
    private int mWeekSelectTextColor;
    private int mCurrYear, mCurrMonth, mCurrDay;
    private int mSelYear, mSelMonth, mSelDay;
    private int mColumnSize, mRowSize;
    private float mSelectCircleSize;
    private float mRadiusRate;
    private float mCircleRadius;
    private boolean mIsShowHint;
    private boolean mIsShowLunar;
    private DateTime mStartDate;
    private DisplayMetrics mDisplayMetrics;
    private OnWeekClickListener mOnWeekClickListener;
    private GestureDetector mGestureDetector;
    private boolean[] mTaskHintCircle;
    private int[] festivalHint;

    private Typeface mTypeFace;

    private ExecutorService singleExecutor;

    public WeekView(Context context, DateTime dateTime) {
        this(context, null, dateTime);
    }

    public WeekView(Context context, TypedArray array, DateTime dateTime) {
        this(context, array, null, dateTime);
    }

    public WeekView(Context context, TypedArray array, AttributeSet attrs, DateTime dateTime) {
        this(context, array, attrs, 0, dateTime);
    }

    public WeekView(Context context, TypedArray array, AttributeSet attrs, int defStyleAttr, DateTime dateTime) {
        super(context, attrs, defStyleAttr);
        initAttrs(array, dateTime);
        initPaint();
        initWeek();
        initGestureDetector();
    }

    private void initTaskHint() {
        if (mIsShowHint) {
            if (null == singleExecutor) {
                singleExecutor = Executors.newSingleThreadExecutor();
            }
            singleExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    TaskHint taskHint = TaskHint.getInstance(getContext().getApplicationContext());
                    mTaskHintCircle = taskHint.getTaskHintByWeek(mStartDate);
                    festivalHint = taskHint.getFestivalHintByWeek(mStartDate);

                    if ((null != mTaskHintCircle && mTaskHintCircle.length > 0) || (null != festivalHint && festivalHint.length > 0)) {
                        postInvalidate();
                    }
                }
            });
        }
    }

    private void initAttrs(TypedArray array, DateTime dateTime) {
        if (array != null) {
            mSelectDayColor = array.getColor(R.styleable.WeekCalendarView_week_selected_text_color, Color.parseColor("#FFFFFF"));
            mSelectBGColor = array.getColor(R.styleable.WeekCalendarView_week_selected_circle_color, Color.parseColor("#f8cfbe"));
            mSelectBGTodayColor = array.getColor(R.styleable.WeekCalendarView_week_selected_circle_today_color, Color.parseColor("#e75757"));
            mNormalDayColor = array.getColor(R.styleable.WeekCalendarView_week_normal_text_color, Color.parseColor("#363636"));
            mNormalChinaDayColor = array.getColor(R.styleable.WeekCalendarView_week_normal_china_day_color, Color.parseColor("#545454"));
            mCurrentDayColor = array.getColor(R.styleable.WeekCalendarView_week_today_text_color, Color.parseColor("#ff763f"));
            mLineColor = array.getColor(R.styleable.WeekCalendarView_week_line_color, Color.parseColor("#ff763f"));
            mHintCircleColor = array.getColor(R.styleable.WeekCalendarView_week_hint_circle_color, Color.parseColor("#aaaaaa"));
            mLastOrNextMonthTextColor = array.getColor(R.styleable.MonthCalendarView_month_last_or_next_month_text_color, Color.parseColor("#ACA9BC"));
            mFestivalWorkTextColor = array.getColor(R.styleable.WeekCalendarView_week_festival_work_text_color, Color.parseColor("#13be67"));
            mFestivalRestTextColor = array.getColor(R.styleable.WeekCalendarView_week_festival_rest_text_color, Color.parseColor("#f55a5a"));
            mWeekSelectTextColor = Color.parseColor("#777777");
            mIsShowHint = array.getBoolean(R.styleable.WeekCalendarView_week_show_task_hint, true);
            mIsShowLunar = array.getBoolean(R.styleable.WeekCalendarView_week_show_lunar, true);
        } else {
            mSelectDayColor = Color.parseColor("#FFFFFF");
            mSelectBGColor = Color.parseColor("#f8cfbe");
            mSelectBGTodayColor = Color.parseColor("#e75757");
            mNormalDayColor = Color.parseColor("#363636");
            mNormalChinaDayColor = Color.parseColor("#545454");
            mCurrentDayColor = Color.parseColor("#ff763f");
            mLineColor = Color.parseColor("#ff763f");
            mHintCircleColor = Color.parseColor("#aaaaaa");
            mLastOrNextMonthTextColor = Color.parseColor("#ACA9BC");
            mFestivalWorkTextColor = Color.parseColor("#13be67");
            mFestivalRestTextColor = Color.parseColor("#f55a5a");
            mWeekSelectTextColor = Color.parseColor("#777777");
            mIsShowHint = true;
            mIsShowLunar = true;
        }
        mStartDate = dateTime;
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

    private void initWeek() {
        Calendar calendar = Calendar.getInstance();
        mCurrYear = calendar.get(Calendar.YEAR);
        mCurrMonth = calendar.get(Calendar.MONTH);
        mCurrDay = calendar.get(Calendar.DATE);
        DateTime endDate = mStartDate.plusDays(7);
        if (mStartDate.getMillis() <= System.currentTimeMillis() && endDate.getMillis() > System.currentTimeMillis()) {
            setSelectYearMonth(mStartDate.getYear(), mStartDate.getMonthOfYear() - 1, mCurrDay);
        } else {
            setSelectYearMonth(mStartDate.getYear(), mStartDate.getMonthOfYear() - 1, mStartDate.getDayOfMonth());
        }
        initTaskHint();
    }

    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                doClickAction((int) e.getX(), (int) e.getY());
                return true;
            }
        });
    }

    public void setSelectYearMonth(int year, int month, int day) {
        mSelYear = year;
        mSelMonth = month;
        mSelDay = day;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = mDisplayMetrics.densityDpi * 200;
        }
        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = mDisplayMetrics.densityDpi * 300;
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long start = System.currentTimeMillis();
        initSize();
        drawThisWeek(canvas);
        drawLunarText(canvas);
    }

    private void initSize() {
        mColumnSize = getWidth() / NUM_COLUMNS;
        mRowSize = getResources().getDimensionPixelSize(R.dimen.week_calendar_height);
//        mRowSize = getHeight();
//        mSelectCircleSize =  mColumnSize / 2.45f;
        mSelectCircleSize =  mRowSize / 2.45f;
    }

    private void drawThisWeek(Canvas canvas) {
        for (int i = 0; i < 7; i++) {
            DateTime date = mStartDate.plusDays(i);
            int day = date.getDayOfMonth();
            String dayString = String.valueOf(day);
            int startX = (int) (mColumnSize * i + (mColumnSize - mPaint.measureText(dayString)) / 2);
            int startY = (int) (mRowSize / 2 - (mPaint.ascent() + mPaint.descent()) * 0.1f);

            //绘制选中日期时的文字颜色
            if (dayString.equals(String.valueOf(mSelDay))) {
                int startRecX = mColumnSize * i;
                int endRecX = startRecX + mColumnSize;
                if (date.getYear() == mCurrYear && date.getMonthOfYear() - 1 == mCurrMonth && day == mCurrDay) {
                    mDayBgPaint.setColor(mSelectBGTodayColor);
                } else {
                    mDayBgPaint.setColor(mSelectBGColor);
                }
                //绘制选中日期圆圈
                canvas.drawCircle((startRecX + endRecX) / 2, mRowSize / 2 - mRowSize * 0.06f, mSelectCircleSize * mRadiusRate, mDayBgPaint);
            }



            if (dayString.equals(String.valueOf(mSelDay)) && (date.getYear() == mCurrYear && date.getMonthOfYear() - 1 == mCurrMonth && day == mCurrDay)) {
                mPaint.setColor(mSelectDayColor);
                mFestivalPaint.setColor(mSelectDayColor);
            } else {
                //如果是今天
                if (date.getYear() == mCurrYear && date.getMonthOfYear() - 1 == mCurrMonth && day == mCurrDay) {
                    mPaint.setColor(mSelectBGTodayColor);
                } else {
                    //如果是周末
                    if (CalendarUtils.isWeekend(date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth())){
                        if (date.getDayOfMonth() == mSelDay) {
                            //周末选中颜色
                            mPaint.setColor(mWeekSelectTextColor);
                        } else {
                            //周末灰显
                            mPaint.setColor(getResources().getColor(R.color.weekend_txt_color));
                            mFestivalPaint.setColor(mNormalDayColor);
                        }
                    } else {
                        //一般情况
                        mPaint.setColor(mNormalDayColor);
                    }
                }
            }
            canvas.drawText(dayString, startX, startY, mPaint);

            //绘制日程点
            drawHintCircle(i, day, canvas);
            //绘制放假信息
            drawHoliday(date,i, day, startY, canvas);
        }
    }

    /**
     * 绘制放假信息
     * @param date 日期
     * @param i 下标
     * @param day 当前是几号
     * @param startY Y轴距离
     * @param canvas 画布
     * */
    private void drawHoliday(DateTime date, int i, int day, int startY, Canvas canvas) {
        //绘制“班”和“休”
        String festivalStr;
        if (festivalHint != null && festivalHint.length > 0) {
            int festivalStartX;
            int festivalStartY = (int) (startY - mPaint.getTextSize());
            if (0 == festivalHint[i]) {
                festivalStr = "班";
                if (!(date.getYear() == mCurrYear && date.getMonthOfYear() - 1 == mCurrMonth && day == mCurrDay)) {
                    mFestivalPaint.setColor(mFestivalWorkTextColor);
                }
                festivalStartX = (int) (mColumnSize * i + (mColumnSize - mFestivalPaint.measureText(festivalStr)) / 2);
                canvas.drawText(festivalStr, festivalStartX, festivalStartY, mFestivalPaint);
            } else if (1 == festivalHint[i]) {
                festivalStr = "休";
                if (!(date.getYear() == mCurrYear && date.getMonthOfYear() - 1 == mCurrMonth && day == mCurrDay)) {
                    mFestivalPaint.setColor(mFestivalRestTextColor);
                }
                festivalStartX = (int) (mColumnSize * i + (mColumnSize - mFestivalPaint.measureText(festivalStr)) / 2);
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
        if (mIsShowLunar) {
            for (int i = 0; i < 7; i++) {
                DateTime date = mStartDate.plusDays(i);
                int year = date.getYear();
                int month = date.getMonthOfYear();
                int day = date.getDayOfMonth();

                long[] l = LunarCalendar.computeLunarLimitMaxDate(year, month - 1, day);
                String dayString = ChinaDate.getFestivalAndSolar(year, month, day, l);

                float startX = (int) (mColumnSize * i + (mColumnSize - mChinaDayPaint.measureText(dayString)) / 2);
                float startY = (int) (mRowSize * 0.65 - (mChinaDayPaint.ascent() + mChinaDayPaint.descent()) / 2);

                float lineStartX = (mColumnSize * i + (mColumnSize - mChinaDayPaint.measureText(dayString)) / 2);
                float lineStartY = startY + getContext().getResources().getDimension(R.dimen.DIMEN_10PX);
                float lineStopX = (mColumnSize * i + (mColumnSize - mChinaDayPaint.measureText(dayString)) / 2) + mChinaDayPaint.measureText(dayString);
                float lineStopY = startY + getContext().getResources().getDimension(R.dimen.DIMEN_10PX);

                if (day == mSelDay && (date.getYear() == mCurrYear && date.getMonthOfYear() - 1 == mCurrMonth && day == mCurrDay)) {
                    mChinaDayPaint.setColor(mSelectDayColor);
                }  else {
                    if (date.getYear() == mCurrYear && date.getMonthOfYear() - 1 == mCurrMonth && day == mCurrDay) {
                        //如果是今天
                        mChinaDayPaint.setColor(mSelectBGTodayColor);
//                        mChinaDayPaint.setColor(mNormalChinaDayColor);
                    } else {
                        if (CalendarUtils.isWeekend(date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth())){
                            if (date.getDayOfMonth() == mSelDay) {
                                //周末选中颜色
                                mChinaDayPaint.setColor(mWeekSelectTextColor);
                            } else {
                                //周末灰显
                                mChinaDayPaint.setColor(getResources().getColor(R.color.weekend_txt_color));
                            }
                        } else {
                            mChinaDayPaint.setColor(mNormalChinaDayColor);
                        }
                    }

                    //如果今天是初一就不用画初一的横线了
                    if (day != mCurrDay && 1 == l[2] || dayString.contains("月")) {
                        mLinePaint.setColor(mLineColor);
                        canvas.drawLine(lineStartX, lineStartY , lineStopX, lineStopY, mLinePaint);
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
    private void drawHintCircle(int column, int day, Canvas canvas) {
        if (mTaskHintCircle != null && mTaskHintCircle.length > 0) {
            if (mTaskHintCircle[column]) {
                mHintCirclePaint.setColor(mHintCircleColor);
                float circleX = (float) (mColumnSize * column + mColumnSize * 0.5);
                float circleY = (float) (mRowSize * 0.76 + mChinaDayPaint.getTextSize());
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
                doClickAction(x, y);
                break;
            default:
                break;
        }
        return true;
//        return mGestureDetector.onTouchEvent(event);
    }

    /**
     * 处理点击事件
     * */
    private void doClickAction(int x, int y) {
        if (y > getHeight())
            return;
        int column = x / mColumnSize;
        column = Math.min(column, 6);
        DateTime date = mStartDate.plusDays(column);
        if (date.getYear() < ConstData.MIN_YEAR || date.getYear() > ConstData.MAX_YEAR) {
            return;
        }
        clickThisWeek(date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth());
        initAnim();
    }

    public void clickThisWeek(int year, int month, int day) {
        if (mOnWeekClickListener != null) {
            mOnWeekClickListener.onClickDate(year, month, day);
        }
        setSelectYearMonth(year, month, day);
        invalidate();
    }

    public void setOnWeekClickListener(OnWeekClickListener onWeekClickListener) {
        mOnWeekClickListener = onWeekClickListener;
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

    public int getCurrentYear() {
        return mCurrYear;
    }

    /**
     * 获取当前选择日
     * @return
     */
    public int getSelectDay() {
        return this.mSelDay;
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
    public void addTaskHint(Integer day) {
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
    public void removeTaskHint(Integer day) {
        if (null != mTaskHintCircle) {
            mTaskHintCircle[day] = false;
            invalidate();
        }
    }
}
