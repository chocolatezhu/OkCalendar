package com.ppzhu.calendar.year;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.ppzhu.app.CalendarController;
import com.ppzhu.calendar.R;
import com.ppzhu.calendar.festival.ChinaDate;
import com.ppzhu.calendar.utils.LunarCalendar;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 对于参数的调整最好是设置成屏幕的参数进行换算后得到值，不要使用确定的值，即使使用也应该是针对不同的屏幕提供不同的值，这样才能做到适配
 * 这里每个月的高度确定为控件高度的4分之一
 */
public class YearView extends View {

    private Calendar mCalendar;
    private Time mBaseDate = new Time();
    private Time mToday;
    private int mFirstJulianDay;
    private int mLastJulianDay;
    private Time mSelectedTime;
    private CalendarController mController;
    private GestureDetector mGestureDetector;

    private static float MONTH_TEXT_SIZE = 28;
    private boolean mIsSelected;
    private int mColumn;
    private int mRow;
    private int mMonth;
    public static RectF mRectF;

    private boolean mHasToday = false;
    private int mMonthTodayColor;
    private int mMonthNormalColor;
    private int mDayTextTodayColor;
    private int mDayTextNormalColor;

    private String mMonthStringArray[];

    private float mDayInterval;
    private int mMonthHeight;
    private float mMonthWidth;
    private float weekwidth;
    // 空格间距
    private int mSpacewidth;
    // 与整个屏幕的左右margin距离
    private int mMarginwidth;
    // 每月头部margin距离
    private int mMonthMarginTop;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private Typeface mTypeFace;
    private Paint mMonthPaint;
    private Paint mLinePaint;
    private Paint mToDayPaint;
    private Paint mDayPaint;
    private Paint mChinaDayPaint;

    public YearView(Context context) {
        this(context, null);
    }

    public YearView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YearView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs();
        initPaint();
        initView(context);
    }

    private void initAttrs() {
        mMonthStringArray = getResources().getStringArray(R.array.month_name);
        mMonthTodayColor = getResources().getColor(R.color.month_text_color);
        mMonthNormalColor= getResources().getColor(R.color.schedule_menu_text);
        mDayTextTodayColor = getResources().getColor(R.color.today_text_color);
        mDayTextNormalColor = getResources().getColor(R.color.day_text_color);
    }

    /**
     * 初始化画笔参数
     * */
    private void initPaint() {
        mTypeFace = Typeface.createFromAsset(getResources().getAssets(), "fonts/calendar_font.ttf");
        initMonthPaint();
        initLinePaint();
        initToDayPaint();
        initDayPaint();
        initChinaDayPaint();
    }

    private void initView(Context context) {
        Resources mResources = context.getResources();
        mSpacewidth = mResources.getDimensionPixelSize(R.dimen.DIMEN_101PX);
        mMarginwidth = mResources.getDimensionPixelSize(R.dimen.DIMEN_32PX);
        mMonthMarginTop = mResources.getDimensionPixelSize(R.dimen.DIMEN_81PX);
        mToday = new Time();
        mGestureDetector = new GestureDetector(context, new CalendarGestureListener());
        mController = CalendarController.getInstance(context);
    }

    /**
     * 从年视图进入月份界面
     * */
    private void setSelectionFromPosition(int x, int y) {
        for (int row = 1; row <= 4; row++) {
            for (int column = 1; column <= 3; column++) {
                int month = column + (row - 1) * 3;
                float startX = (column - 1) * (mMonthWidth + mSpacewidth);
                float startY = (row - 1) * mMonthHeight;
                float stopX = column * (mMonthWidth) + (column - 1) * (mSpacewidth);
                float stopY = row * mMonthHeight;
                RectF rect = new RectF(startX, startY, stopX, stopY);
                if (rect.contains(x, y)) {
                    clickEnterMonth(column, row, month);
                    break;
                }
            }
        }
    }

    private void clickEnterMonth(int column, int row, int month) {
        mColumn = column;
        mRow = row;
        mMonth = month;
        mIsSelected = true;
        mBaseDate.month = month - 1;
        mController.sendEvent(this, CalendarController.EventType.GO_TO, mBaseDate, mBaseDate, -1, CalendarController.ViewType.MONTH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        doDraw(canvas);
    }

    private void doDraw(Canvas canvas) {
        // 每个月的宽度和高度,将这些数据全部在这里初始化
        setElementSize();
        // 绘制月份
        drawMonth(canvas);
        // 绘制下划线
        drawLine(canvas);
        // 绘制今天
        if (mHasToday) {
            drawToDay(canvas);
        }
        long start = System.currentTimeMillis();
        // 绘制天
        drawDay(canvas);
    }

    private void setElementSize() {
        mMonthWidth = (getMeasuredWidth() - mSpacewidth * 2 - mMarginwidth * 2) / 3;
        mMonthHeight = (getMeasuredHeight() / 4);
        mDayInterval = mMonthHeight / 8.0f;
        weekwidth = mMonthWidth / 7.0f;
    }

    private void drawToDay(Canvas canvas) {
        int day = mToday.monthDay;
        int month = mToday.month + 1;
        int row, column;
        row = (mToday.month) / 3 + 1;
        if (month % 3 != 0) {
            column = month % 3;
        } else {
            column = 3;
        }
        int dayOfWeek = mToday.weekDay;
        float startX = mMarginwidth + (column - 1) * (mMonthWidth + mSpacewidth) + weekwidth * dayOfWeek;
        float startMonthY = mDayInterval * 2.65f;
        float startY = startMonthY + (row - 1) * mMonthHeight;
        drawCircle(startX, startY, canvas);
    }


    private void drawCircle(float startX, float startY, Canvas canvas) {
        Calendar instance = Calendar.getInstance();
        int week = instance.get(Calendar.WEEK_OF_MONTH) - 1;
        startY += mDayInterval * week;
        float r = Math.max(mDayInterval, weekwidth) / 2.0f - getResources().getDimension(R.dimen.DIMEN_4PX);
        canvas.drawCircle(startX + weekwidth / 2.0f, startY - r / 2.0f, r, mToDayPaint);
    }

    /**
     * 设置绘制今天日期画笔
     * */
    private void initToDayPaint() {
        mToDayPaint = new Paint();
        mToDayPaint.setAntiAlias(true);
        mToDayPaint.setColor(getResources().getColor(R.color.month_text_color));
    }

    public void updateToday() {
        mToday.setToNow();
        mToday.normalize(true);

        int julianToday = Time.getJulianDay(mToday.toMillis(false), mToday.gmtoff);
        mHasToday = julianToday >= mFirstJulianDay && julianToday < mLastJulianDay;
        mRow = (mSelectedTime.month) / 3 + 1;
        int month = mSelectedTime.month + 1;
        if (month % 3 != 0) {
            mColumn = month % 3;
        } else {
            mColumn = 3;
        }
        mIsSelected = true;
    }

    private void drawDay(Canvas canvas) {
        mCalendar = Calendar.getInstance();
        int month;
        StringBuilder builder = new StringBuilder();

        try {
            for (int row = 1; row <= 4; row++) {
                for (int column = 1; column <= 3; column++) {
                    month = column + (row - 1) * 3;
                    Date mDate = format.parse(getDateFormatString(builder, month));
                    mCalendar.setTime(mDate);
                    int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
                    int dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
                    mCalendar.add(Calendar.DATE, 1 - dayOfMonth);
                    mCalendar.add(Calendar.MONTH, 1);
                    mCalendar.add(Calendar.DATE, -1);

                    float startMonthY = mDayInterval * 2.6f;
                    float startY = startMonthY + (row - 1) * mMonthHeight;
                    drawDayText(mCalendar, dayOfWeek, column, month, canvas, startY);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawDayText(Calendar calendar, int dayOfWeek, int column, int month, Canvas canvas, float startY) {
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        for (int day = 1; day <= dayOfMonth; day++) {
            float startX = mMarginwidth + (column - 1) * (mMonthWidth + mSpacewidth) + weekwidth * (dayOfWeek - 1);
            dayOfWeek++;
            float mDateNumStrWidth = mDayPaint.measureText(day + "");

            if (mHasToday && day == mToday.monthDay && (month - 1) == mToday.month) {
                mDayPaint.setColor(mDayTextTodayColor);
            } else {
                mDayPaint.setColor(mDayTextNormalColor);
            }
            canvas.drawText(day + "", (mDateNumStrWidth / 2 + startX + (weekwidth - mDateNumStrWidth) / 2), startY, mDayPaint);

//            //计算农历
//            long[] dates = LunarCalendar.computeLunarLimitMaxDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), day);
//            String china_date = ChinaDate.getChinaDate((int) (dates[2]));
//
//            if (china_date.equals("初一")) {
//                float x = startX + (weekwidth - getResources().getDimension(R.dimen.DIMEN_24PX)) / 2;
//                float y = startY + getResources().getDimension(R.dimen.DIMEN_9PX);
//                if (mHasToday && day == mToday.monthDay && (month - 1) == mToday.month) {
//                    y = startY + getResources().getDimension(R.dimen.DIMEN_19PX);
//                }
//
//                if (ChinaDate.nStr1[(int) dates[1]].equals("正")) {
//                    mChinaDayPaint.setStrokeWidth(6);
//                } else {
//                    mChinaDayPaint.setStrokeWidth(3);
//                }
//                canvas.drawLine(x, y, x + getResources().getDimension(R.dimen.DIMEN_24PX), y, mChinaDayPaint);
//            }

            if (dayOfWeek > 7) {
                dayOfWeek = 1;
                startY += mDayInterval;
            }
        }
    }

    private void initChinaDayPaint() {
        mChinaDayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mChinaDayPaint.setColor(getResources().getColor(R.color.month_text_color));
        mChinaDayPaint.setTypeface(mTypeFace);
        mChinaDayPaint.setStyle(Style.FILL);
        mChinaDayPaint.setTextAlign(Align.CENTER);
        mChinaDayPaint.setFakeBoldText(true);
        mChinaDayPaint.setAntiAlias(true);
    }

    private String getDateFormatString(StringBuilder stringBuilder, int month) {
        stringBuilder.setLength(0);
        stringBuilder.append("").append(mBaseDate.year).append("-");
        stringBuilder.append("").append(month).append("-1");
        return stringBuilder.toString();
    }

    private void initDayPaint() {
        mDayPaint = new Paint();
        mDayPaint.setAntiAlias(true);
        mDayPaint.setTextAlign(Align.CENTER);
        mDayPaint.setStyle(Style.FILL);
        mDayPaint.setTypeface(mTypeFace);
        mDayPaint.setTextSize(getResources().getDimension(R.dimen.DIMEN_21PX));
    }

    private void drawLine(Canvas canvas) {
        for (int row = 1; row <= 4; row++) {
            for (int column = 1; column <= 3; column++) {
                float startMonthY = mDayInterval * 1.9f;
                float startX = mMarginwidth + (column - 1) * (mMonthWidth + mSpacewidth);
                float startY = startMonthY + (row - 1) * mMonthHeight;
                float stopX = mMarginwidth + column * mMonthWidth + (column - 1) * mSpacewidth;
                canvas.drawLine(startX, startY, stopX, startY, mLinePaint);
            }
        }
    }

    private void initLinePaint() {
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(getResources().getColor(R.color.line_color));
        mLinePaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.DIMEN_1PX));
        mLinePaint.setStyle(Style.FILL);
        mLinePaint.setTextAlign(Align.CENTER);
        mLinePaint.setFakeBoldText(true);
        mLinePaint.setAntiAlias(true);
    }

    private void drawMonth(Canvas canvas) {
        for (int row = 1; row <= 4; row++) {
            for (int column = 1; column <= 3; column++) {
                int month = column + (row - 1) * 3;
                float startX = mMarginwidth + (column - 1) * (mMonthWidth + mSpacewidth);
                float startY = mMonthMarginTop + (row - 1) * mMonthHeight;
                Calendar calendar = Calendar.getInstance();
                if(month == (calendar.get(Calendar.MONTH) + 1) && mHasToday){
                    mMonthPaint.setColor(mMonthTodayColor);
                } else {
                    mMonthPaint.setColor(mMonthNormalColor);
                }

                canvas.drawText(mMonthStringArray[month - 1], startX, startY, mMonthPaint);
            }
        }
    }

    private void initMonthPaint() {
        mMonthPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMonthPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.DIMEN_39PX));
        mMonthPaint.setStyle(Style.STROKE);
        mMonthPaint.setTypeface(mTypeFace);
        mMonthPaint.setAntiAlias(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mGestureDetector.onTouchEvent(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                mGestureDetector.onTouchEvent(event);
                return true;
            case MotionEvent.ACTION_UP:
                mGestureDetector.onTouchEvent(event);
                return true;
            case MotionEvent.ACTION_CANCEL:
                mGestureDetector.onTouchEvent(event);
                return true;
            default:
                return mGestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
        }
    }

    class CalendarGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent ev) {
            //点击年视图，跳转到对应用的月份
            YearView.this.doSingleTapUp(ev);
            return true;
        }

        @Override
        public boolean onDown(MotionEvent ev) {
            return true;
        }
    }

    /**
     * 处理年视图点击事件
     * @param event
     * */
    public void doSingleTapUp(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        setSelectionFromPosition(x, y);
    }

    public void setselectedtime(long timeInMillis, Time mSelectedTime) {
        this.mSelectedTime = mSelectedTime;
        mBaseDate.set(timeInMillis);
        mFirstJulianDay = Time.getJulianDay(mBaseDate.toMillis(false), mBaseDate.gmtoff);
        Time mNextDate = new Time();
        mNextDate.set(mBaseDate);
        mNextDate.year += 1;
        mLastJulianDay = Time.getJulianDay(mNextDate.toMillis(false), mNextDate.gmtoff);
        updateToday();
        invalidate();
    }

}
