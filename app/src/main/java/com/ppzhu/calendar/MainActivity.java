package com.ppzhu.calendar;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ppzhu.app.CalendarController;
import com.ppzhu.calendar.anim.MonEnterAnim;
import com.ppzhu.calendar.anim.MonthExitAnim;
import com.ppzhu.calendar.anim.ScheduleListSwitchAnim;
import com.ppzhu.calendar.anim.SlideInUpAnim;
import com.ppzhu.calendar.anim.YearEnterAnim;
import com.ppzhu.calendar.schedule.ScheduleController;
import com.ppzhu.calendar.schedule.ScheduleListener;
import com.ppzhu.calendar.schedule.add.AddScheduleActivity;
import com.ppzhu.calendar.schedule.add.ScheduleAdapter;
import com.ppzhu.calendar.schedule.detail.ScheduleShowActivity;
import com.ppzhu.calendar.utils.DataRecover;
import com.ppzhu.calendar.utils.StatusBarUtil;
import com.ppzhu.calendar.year.YearPagerAdapter;
import com.ppzhu.calendar.base.BaseFragmentActivity;
import com.ppzhu.calendar.constants.AppConst;
import com.ppzhu.calendar.constants.ConstData;
import com.ppzhu.calendar.constants.ScheduleConst;
import com.ppzhu.calendar.database.DataBaseCalendarManager;
import com.ppzhu.calendar.month.MonthView;
import com.ppzhu.calendar.utils.CalendarUtils;
import com.ppzhu.calendar.month.OnCalendarClickListener;
import com.ppzhu.calendar.month.MonthCalendarView;
import com.ppzhu.calendar.view.slidelayout.CalendarViewManager;
import com.ppzhu.calendar.view.slidelayout.ScheduleLayout;
import com.ppzhu.calendar.view.slidelayout.ScheduleRecyclerView;
import com.ppzhu.calendar.bean.AlmanacPojo;
import com.ppzhu.calendar.bean.Schedule;
import com.ppzhu.calendar.bean.ScheduleListBean;
import com.ppzhu.calendar.utils.DateFormatter;
import com.ppzhu.calendar.week.WeekCalendarView;

import org.joda.time.DateTime;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends BaseFragmentActivity implements View.OnClickListener, OnCalendarClickListener {
    private static final String TAG = "MainActivity";
    private Context mContext;
    private TextView mBackTitle;
    private TextView mYear, tv_luna_year;
    private ImageView mAdd;
    private Button tv_today;
    private TextView mTvScheduleNone;
    private ScheduleRecyclerView mScheduleRecyclerView;
    private LinearLayout mYearTitleLayout;
    private LinearLayout mMonthViewLayout;
    private RelativeLayout mMonthView;
    private RelativeLayout mScheduleListContentLayout;

    private FrameLayout mContentLayout;

    private ViewPager mYearPager;
    private static final int HANDLER_KEY = 0;

    private MonthCalendarView mMonthCalendarView;
    private WeekCalendarView mWeekCalendarView;
    private MonthView mCurrentMonthView;
    private ScheduleLayout slSchedule;
    private LinearLayout mWeekTitleLayout;
    private ScheduleAdapter scheduleAdapter;
    private List<Schedule> scheduleList = new ArrayList<>();
    private AlmanacPojo almanacPojo;

    private MainHandle mHandle = new MainHandle(MainActivity.this);

    private ScheduleController mScheduleController;
    private CalendarController mCalendarController;

    private Time mSelectedYearTime = new Time();
    private String mYearFormat = "yyyy年M月";
    private long selectTimeMill;
    //视图的状态，年，月，周
    private CalendarViewManager.ScheduleState mState;
    private CalendarViewManager mCalendarViewManager;

    private YoYo.YoYoString yearEnterYo;
    private YoYo.YoYoString monthExitYo;
    private YoYo.YoYoString yearExitYo;
    private YoYo.YoYoString monthEnterYo;

    //是否从年视图切换，也是判断动画是否结束的标志
    private boolean isFromYearAnimRunning = false;

    private int mSelectYear;
    private int mSelectMonth;
    private int mSelectDay;

    /**
     * 处理数据的单线程
     */
    private ExecutorService singleExecutor = Executors.newSingleThreadExecutor();

    @Override
    public void onClickDate(int year, int month, int day) {
        setClickDate(year, month, day);
        setMonthTitle(year + "年" + (month + 1) + "月");
        selectTimeMill = DateFormatter.getTimeMill(year, month, day);
        mScheduleController.startSelectScheduleSearch(selectTimeMill);
    }

    private void setClickDate(int year, int month, int day) {
        mSelectYear = year;
        mSelectMonth = month;
        mSelectDay = day;
    }

    private static class MainHandle extends Handler {
        private final WeakReference<MainActivity> weakReferenceActivity;

        public MainHandle(MainActivity activity) {
            weakReferenceActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = weakReferenceActivity.get();
            int what = msg.what;
            switch (what) {
                case ConstData.SCHEDULE_REFRESH:
                    activity.initScheduleListData();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawable(null);
        initView();
        bindEvents();
        initController();
        initManager();
        //校验和恢复数据
        checkAndRecoverAsync();
    }

    /**
     * 日程列表数据初始化
     */
    private void initScheduleListData() {
        mScheduleController.startSelectScheduleSearch(selectTimeMill);
    }

    private void initManager() {
        if (null == mCalendarViewManager) {
            mCalendarViewManager = new CalendarViewManager();
        }
        slSchedule.setCalendarViewManager(mCalendarViewManager);
    }

    private void initController() {
        initScheduleController();
        initYearCalendarController();
    }

    private void checkAndRecoverAsync() {
        if (null == singleExecutor) {
            singleExecutor = Executors.newSingleThreadExecutor();
        }
        singleExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DataRecover.checkCalendarDB(mContext.getApplicationContext());
                Message msg = new Message();
                msg.what = ConstData.SCHEDULE_REFRESH;
                mHandle.sendMessage(msg);
            }
        });
    }

    /**
     * 初始化年视图管理Controller类
     */
    private void initYearCalendarController() {
        mCalendarController = CalendarController.getInstance(this);
        mCalendarController.registerFirstEventHandler(HANDLER_KEY, new CalendarController.EventHandler() {
            @Override
            public long getSupportedEventTypes() {
                return CalendarController.EventType.GO_TO | CalendarController.EventType.VIEW_EVENT | CalendarController.EventType.UPDATE_TITLE;
            }

            @Override
            public void handleEvent(CalendarController.EventInfo event) {
                long start = System.currentTimeMillis();
                if (event.eventType == CalendarController.EventType.GO_TO) {
                    selectTimeMill = event.startTime.toMillis(false);
                    startToMonthView(selectTimeMill);
                } else if (event.eventType == CalendarController.EventType.UPDATE_TITLE) {
                    setYearTitle(event);
                }
            }

            @Override
            public void eventsChanged() {

            }
        });
        mCalendarController.sendEvent(this, CalendarController.EventType.UPDATE_TITLE, mSelectedYearTime, mSelectedYearTime, mSelectedYearTime, -1, CalendarController.ViewType.CURRENT,
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR, null, null);
    }

    /**
     * 初始化日程管理Controller类
     */
    private void initScheduleController() {
        mScheduleController = new ScheduleController(getApplicationContext(), new ScheduleListener() {
            @Override
            public void OnGetSelectScheduleList(ScheduleListBean listBean) {
                if (null != listBean) {
                    scheduleList.clear();
                    scheduleList.addAll(listBean.getScheduleList());
                    almanacPojo = listBean.getAlmanacPojo();
                    scheduleAdd();
                }
            }

            @Override
            public void OnGetAllScheduleList(List<Schedule> allScheduleList) {

            }
        });
    }


    private void scheduleAdd() {
        if ((null != scheduleList && !scheduleList.isEmpty()) || (null != almanacPojo && !TextUtils.isEmpty(almanacPojo.getDate()))) {
            sortScheduleList(scheduleList);
            mTvScheduleNone.setVisibility(View.GONE);
            scheduleAdapter.refresh(scheduleList);
        } else {
            scheduleList = new ArrayList<>();
            almanacPojo = new AlmanacPojo();
            scheduleAdapter.refresh(scheduleList);
            mTvScheduleNone.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 对日程按照时间排序，全天日程排在最前
     */
    private void sortScheduleList(List<Schedule> scheduleList) {
        if (null == scheduleList || scheduleList.isEmpty()) {
            return;
        }
        Collections.sort(scheduleList, new Comparator<Schedule>() {
            @Override
            public int compare(Schedule lhs, Schedule rhs) {
                Calendar currentCalendar = Calendar.getInstance();
                Calendar nextCalendar = Calendar.getInstance();
                currentCalendar.setTimeInMillis(lhs.getAlertTime());
                nextCalendar.setTimeInMillis(rhs.getAlertTime());
                //为了避免跨天日程排序有问题，这里年月日设置为0，只比较时分秒
                currentCalendar.set(0, 0, 0);
                nextCalendar.set(0, 0, 0);

                long current = currentCalendar.getTimeInMillis();
                long next = nextCalendar.getTimeInMillis();
                if (current < next) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }

    private void initView() {
        mContext = this;

        slSchedule = (ScheduleLayout) findViewById(R.id.slSchedule);
        mWeekTitleLayout = (LinearLayout) findViewById(R.id.week_title);

        mBackTitle = (TextView) findViewById(R.id.back);
        mYear = (TextView) findViewById(R.id.year);
        tv_luna_year = (TextView) findViewById(R.id.tv_luna_year);
        mYearTitleLayout = (LinearLayout) findViewById(R.id.year_title_layout);
        mMonthView = (RelativeLayout) findViewById(R.id.month_view);
        mMonthViewLayout = (LinearLayout) findViewById(R.id.month_view_layout);
        mScheduleListContentLayout = (RelativeLayout) findViewById(R.id.rlScheduleList);
        mAdd = (ImageView) findViewById(R.id.iv_add);
        mTvScheduleNone = (TextView) findViewById(R.id.tv_no_schedule);
        tv_today = (Button) findViewById(R.id.tv_today);
        mScheduleRecyclerView = (ScheduleRecyclerView) findViewById(R.id.rvScheduleList);

        mContentLayout = (FrameLayout) findViewById(R.id.main_frame_layout);

        mMonthCalendarView = (MonthCalendarView) findViewById(R.id.mcvCalendar);
        mWeekCalendarView = (WeekCalendarView) findViewById(R.id.wcvCalendar);

        initMonthTitle();
        initMonthViewPager();
        initYearViewPager();
        initScheduleList();
    }

    /**
     * 初始化月视图标题
     */
    private void initMonthTitle() {
        selectTimeMill = System.currentTimeMillis();
        setMonthTitle(DateFormatter.timeToFormat(selectTimeMill, mYearFormat));
    }

    /**
     * 初始化月视图页面
     */
    private void initMonthViewPager() {
        Calendar currentCalendar = Calendar.getInstance();
        int cYear = currentCalendar.get(Calendar.YEAR);
        int cMonth = currentCalendar.get(Calendar.MONTH);
        int position = (cYear - ConstData.MIN_YEAR) * 12 + cMonth;
        mMonthCalendarView.setCurrentItem(position, false);
    }

    /**
     * 初始化年视图页面
     */
    private void initYearViewPager() {
        mYearPager = (ViewPager) findViewById(R.id.year_pager);
        mYearPager.setOffscreenPageLimit(3);

        mSelectedYearTime.set(Calendar.getInstance().getTimeInMillis());
        YearPagerAdapter mYearPagerAdapter = new YearPagerAdapter(getSupportFragmentManager(), mSelectedYearTime);
        mYearPager.setAdapter(mYearPagerAdapter);
        int currentYear = mSelectedYearTime.year;
        int currentPosition = currentYear - ConstData.MIN_YEAR;
        mYearPager.setCurrentItem(currentPosition, false);

        mYearPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                onYearPagerSelect(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 初始化日程列表
     */
    private void initScheduleList() {
        mScheduleRecyclerView = slSchedule.getSchedulerRecyclerView();
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mScheduleRecyclerView.setLayoutManager(manager);
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);
        mScheduleRecyclerView.setItemAnimator(itemAnimator);
        scheduleAdapter = new ScheduleAdapter(mContext, scheduleList);
        mScheduleRecyclerView.setAdapter(scheduleAdapter);

        slSchedule.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    private void bindEvents() {
        slSchedule.setOnCalendarClickListener(this);
        mBackTitle.setOnClickListener(this);
        mAdd.setOnClickListener(this);
        tv_today.setOnClickListener(this);

        initScheduleAdapterListener();
        initMonthViewPagerListener();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //监听整个父布局的touch事件
//        if (isFromYearAnimRunning) {
//            return true;
//        }
        if (null != mMonthCalendarView) {
            mMonthCalendarView.setYearAnimRunningFlag(isFromYearAnimRunning);
        }
        if (null != slSchedule) {
            slSchedule.setYearAnimRunningFlag(isFromYearAnimRunning);
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 初始化MonthViewPager滑动事件监听
     * */

    private void initMonthViewPagerListener() {
        mMonthCalendarView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentMonthView = mMonthCalendarView.mMonthAdapter.getViews().get(position);
                if (CalendarViewManager.ScheduleState.Week == mCalendarViewManager.getState()) {
                    return;
                }
                if (isFromYearAnimRunning) {
                    return;
                }
                if (mCurrentMonthView == null) {
                    return;
                }

                float from = mScheduleListContentLayout.getY();
                float end = CalendarUtils.getWeekRows(mCurrentMonthView.getSelectYear(), mCurrentMonthView.getSelectMonth()) * slSchedule.mRowSize;
                ScheduleListSwitchAnim scheduleListSwitchAnim = new ScheduleListSwitchAnim(from, end);
                YoYo.with(scheduleListSwitchAnim).interpolate(new DecelerateInterpolator()).duration(300).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //设置标题
                        setMonthTitle((mCurrentMonthView.getSelectYear() + "年" + (mCurrentMonthView.getSelectMonth() + 1) + "月"));

                        //刷新日程列表
                        selectTimeMill = DateFormatter.getTimeMill(mCurrentMonthView.getSelectYear(), mCurrentMonthView.getSelectMonth(), mCurrentMonthView.getSelectDay());
                        mScheduleController.startSelectScheduleSearch(selectTimeMill);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).playOn(mScheduleListContentLayout);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        mScheduleRecyclerView.setIsIdle(false);
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:
                        mScheduleRecyclerView.setIsIdle(true);
                        //月视图点击选中事件
                        if (null == mCurrentMonthView) {
                            break;
                        }
                        try {
                            slSchedule.onMonthClick(mCurrentMonthView.getSelectYear(), mCurrentMonthView.getSelectMonth(), mCurrentMonthView.getSelectDay());
                        } catch (Exception e) {
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 初始化日程列表点击事件
     * */
    private void initScheduleAdapterListener() {
        scheduleAdapter.setOnItemClickListener(new ScheduleAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startToScheduleDetail(position);
            }
        });
    }

    /**
     * 跳转到日程详情
     * <p> 如果是联系人生日就跳转到联系人详情界面 </p>
     *
     * @param position 当前点击位置
     */
    private void startToScheduleDetail(int position) {
        if (null == scheduleList || scheduleList.isEmpty()) {
            return;
        }
        Intent intent = new Intent();
        if (TextUtils.isEmpty(scheduleList.get(position).getLookUpUri())) {
            intent.putExtra(ConstData.INTENT_SCHEDULE_SELECT_DATE_KEY, selectTimeMill);
            intent.setClass(mContext, ScheduleShowActivity.class);
            intent.putExtra("id", scheduleList.get(position).getId());
            startActivityForResult(intent, ConstData.INTENT_SCHEDULE_REFRESH);
        } else {
            //跳转联系人详情
            Uri lookupUri = Uri.parse(scheduleList.get(position).getLookUpUri());
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setData(lookupUri);
            intent.setAction(ScheduleConst.ACTION_CONTACT_DETAIL);
            startActivityForResult(intent, ConstData.INTENT_SCHEDULE_BIRTHDAY_REFRESH);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.back:
                onClickBack();
                break;
            case R.id.iv_add:
                scheduleAdd(intent);
                break;
            case R.id.tv_today:
                positionToToday();
                break;
            default:
                break;
        }
    }

    private void onClickBack() {
        if (mState != CalendarViewManager.ScheduleState.Year && !isMonthYearAnimRunning()) {
            startToYearView();
        }
    }

    private void onYearPagerSelect(int position) {
        //当前选中的年份
        mSelectedYearTime.year = position + ConstData.MIN_YEAR;
        mCalendarController.sendEvent(this, CalendarController.EventType.UPDATE_TITLE, mSelectedYearTime, mSelectedYearTime, mSelectedYearTime, -1, CalendarController.ViewType.CURRENT,
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR, null, null);
    }

    /**
     * 设置月视图时的标题
     *
     * @param date 字符串格式的时间
     */
    private void setMonthTitle(String date) {
        String dateStr = date.replaceAll(" ", "");
        Calendar calendar = DateFormatter.formatToCalendar(dateStr, mYearFormat);
        mSelectedYearTime.set(calendar.getTimeInMillis());
        mBackTitle.setText(dateStr);
    }

    /**
     * 设置年视图时的标题
     *
     * @param event
     */
    private void setYearTitle(CalendarController.EventInfo event) {
        if (event.eventType != CalendarController.EventType.UPDATE_TITLE) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(event.startTime.toMillis(true));
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        int year = calendar.get(Calendar.YEAR);
        StringBuilder sb = new StringBuilder();
        sb.append(getArrayString(R.array.year_gan, year % 10));
        sb.append(getArrayString(R.array.year_zhi, year % 12));
        sb.append(getArrayString(R.array.year_animal, year % 12));
        sb.append("年");
        mYear.setText(String.valueOf(year));
        tv_luna_year.setText(sb.toString());
    }

    private String getArrayString(int resId, int index) {
        return getResources().getStringArray(resId)[index];
    }

    /**
     * 定位到今天
     */
    private void positionToToday() {
        if (mState == CalendarViewManager.ScheduleState.Year) {
            selectTimeMill = System.currentTimeMillis();
            mSelectedYearTime.set(selectTimeMill);
            int currentPosition = mSelectedYearTime.year - ConstData.MIN_YEAR;
            if (mYearPager.getCurrentItem() == currentPosition) {
                if (!isMonthYearAnimRunning()) {
                    showMonthView(selectTimeMill);
                }
            } else {
                mYearPager.setCurrentItem(currentPosition, false);
            }
        } else {
            if (null != mMonthCalendarView) {
                mMonthCalendarView.setTodayToView();
            }
        }
    }

    /**
     * 跳转到日程添加界面
     */
    private void scheduleAdd(Intent intent) {
        //当前选中日期的时间
        intent.putExtra(ConstData.INTENT_SCHEDULE_SELECT_DATE_KEY, selectTimeMill);
        intent.setClass(MainActivity.this, AddScheduleActivity.class);
        startActivityForResult(intent, ConstData.INTENT_SCHEDULE_REFRESH);
        overridePendingTransition(R.anim.bottom_enter, R.anim.anim_fade_out);
    }

    /**
     * 切换到月视图
     */
    private void startToMonthView(long timeMill) {
        if (mState == CalendarViewManager.ScheduleState.Year && !isMonthYearAnimRunning()) {
            showMonthView(timeMill);
        }
    }

    /**
     * 切换到年视图
     */
    private void startToYearView() {
        Calendar calendar = DateFormatter.formatToCalendar(mBackTitle.getText().toString(), mYearFormat);
        ConstData.CURRENT_SELECT_MONTH = calendar.get(Calendar.MONTH) + 1;

        mSelectedYearTime.set(calendar.getTimeInMillis());
        int currentPosition = mSelectedYearTime.year - ConstData.MIN_YEAR;
        mYearPager.setCurrentItem(currentPosition, false);

        RectF monthViewRectF = getMonthRect();
        float mPivotX = monthViewRectF.centerX();
        float mPivotY = monthViewRectF.centerY();

        float[] pivot = getYearToMontPivot(calendar.get(Calendar.MONTH));
        float pivotX = pivot[0];
        float pivotY = pivot[1];
        MonthExitAnim monthExitAnim = new MonthExitAnim(mPivotX, mPivotY, pivotX, pivotY);
        monthExitYo = YoYo.with(monthExitAnim).interpolate(new DecelerateInterpolator()).duration(0).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mScheduleListContentLayout.setVisibility(View.GONE);
                mBackTitle.setVisibility(View.GONE);
                mWeekTitleLayout.setVisibility(View.INVISIBLE);
                mYearTitleLayout.setVisibility(View.VISIBLE);
                mYearPager.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                    mManager.setState(CalendarManager.State.YEAR);
                mMonthViewLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(mMonthView);

        //LAYER_TYPE_HARDWARE相关知识参考http://www.jianshu.com/p/f1feafffc365或参考官方文档
        mYearPager.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        YearEnterAnim yearEnterAnim = new YearEnterAnim(mPivotX, mPivotY, pivotX, pivotY);
        yearEnterYo = YoYo.with(yearEnterAnim).interpolate(new DecelerateInterpolator()).duration(350).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
//                mYearTitleLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mState = CalendarViewManager.ScheduleState.Year;
                //这里必须要释放
                mYearPager.setLayerType(View.LAYER_TYPE_NONE, null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(mYearPager);
    }

    /**
     * 计算月视图矩形
     */
    private RectF getMonthRect() {
        int left = mMonthView.getLeft();
        int top = mMonthView.getTop();
        int width = mMonthView.getWidth();
        int height = mMonthView.getHeight();
        return new RectF(left, top, left + width, top + height);
    }

    /**
     * 计算年视图每个月的矩形
     *
     * @param month 月份
     */
    private RectF getYearMonthRect(int month) {
        int width = mContentLayout.getWidth();
        int height = mContentLayout.getHeight();
        int left = mContentLayout.getLeft();
        int top = mContentLayout.getTop();
        int perWidth = width / 3;
        int perHeight = height / 4;
        int row = (month) / 3 + 1;
        int column;
        if ((month + 1) % 3 != 0) {
            column = (month + 1) % 3;
        } else {
            column = 3;
        }
        float startX = left + (column - 1) * perWidth;
        float startY = top + (row - 1) * perHeight;
        float stopX = left + column * perWidth;
        float stopY = top + row * perHeight;
        return new RectF(startX, startY, stopX, stopY);
    }

    private void showMonthView(final long timeMill) {
        isFromYearAnimRunning = true;

        RectF monthViewRectF = getMonthRect();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMill);
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        RectF rectF = getYearMonthRect(month);

        float pivot[] = getYearToMontPivot(month);
        float pivotX = pivot[0];
        float pivotY = pivot[1];
        float scale = rectF.width() / monthViewRectF.width();

        MonEnterAnim monEnterAnim = new MonEnterAnim(pivotX, pivotY, scale);
        monthEnterYo = YoYo.with(monEnterAnim).interpolate(new DecelerateInterpolator()).duration(350).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mYearPager.setVisibility(View.GONE);
                mYearTitleLayout.setVisibility(View.GONE);
                mBackTitle.setVisibility(View.VISIBLE);

                mScheduleListContentLayout.setVisibility(View.GONE);
                mYearTitleLayout.setVisibility(View.GONE);
                mBackTitle.setVisibility(View.VISIBLE);
                mMonthViewLayout.setVisibility(View.VISIBLE);
                mMonthCalendarView.changeDateToView(timeMill);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mState = CalendarViewManager.ScheduleState.Month;
                mScheduleListContentLayout.setVisibility(View.VISIBLE);
                mWeekTitleLayout.setVisibility(View.VISIBLE);

                YoYo.with(Techniques.FadeInDown).interpolate(new DecelerateInterpolator()).duration(250).playOn(mWeekTitleLayout);
                showContentListAnim(year, month);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(mMonthView);
    }

    /**
     * 日程列表动画
     * @param year
     * @param month
     * */
    private void showContentListAnim(int year, int month) {
        float end, from;
        if (mCalendarViewManager.getState() != CalendarViewManager.ScheduleState.Week) {
            //如果返回的是是月视图
            end = CalendarUtils.getWeekRows(year, month) * slSchedule.mRowSize;
            from = end + slSchedule.mRowSize;
        } else {
            //如果返回的是周视图
            end = slSchedule.mRowSize;
            from = slSchedule.mRowSize * 2;
        }
        SlideInUpAnim slideInUpAnim = new SlideInUpAnim(from, end);
        YoYo.with(slideInUpAnim).interpolate(new DecelerateInterpolator()).duration(250).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isFromYearAnimRunning = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(mScheduleListContentLayout);
    }

    /**
     * 获取年视图切换月视图的缩放位置
     */
    private float[] getYearToMontPivot(int month) {
        float[] pivot = new float[2];
        RectF rectF = getYearMonthRect(month);
        switch (month) {
            case Calendar.JANUARY:
                pivot[0] = rectF.left;
                pivot[1] = rectF.top;
                break;
            case Calendar.FEBRUARY:
                pivot[0] = rectF.centerX();
                pivot[1] = rectF.top;
                break;
            case Calendar.MARCH:
                pivot[0] = rectF.right;
                pivot[1] = rectF.top;
                break;
            case Calendar.APRIL:
                pivot[0] = rectF.left;
                pivot[1] = rectF.centerY();
                break;
            case Calendar.MAY:
                pivot[0] = rectF.centerX();
                pivot[1] = rectF.top;
                break;
            case Calendar.JUNE:
                pivot[0] = rectF.right;
                pivot[1] = rectF.centerY();
                break;
            case Calendar.JULY:
                pivot[0] = rectF.left;
                pivot[1] = rectF.bottom;
                break;
            case Calendar.AUGUST:
                pivot[0] = rectF.centerX();
                pivot[1] = rectF.bottom;
                break;
            case Calendar.SEPTEMBER:
                pivot[0] = rectF.right;
                pivot[1] = rectF.bottom;
                break;
            case Calendar.OCTOBER:
                pivot[0] = rectF.left;
                pivot[1] = rectF.bottom;
                break;
            case Calendar.NOVEMBER:
                pivot[0] = rectF.centerX();
                pivot[1] = rectF.bottom;
                break;
            case Calendar.DECEMBER:
                pivot[0] = rectF.right;
                pivot[1] = rectF.bottom;
                break;
            default:
                break;
        }
        return pivot;
    }

    private boolean isMonthYearAnimRunning() {
        return (yearEnterYo != null && yearEnterYo.isRunning()) ||
                (monthExitYo != null && monthExitYo.isRunning()) ||
                (yearExitYo != null && yearExitYo.isRunning()) ||
                (monthEnterYo != null && monthEnterYo.isRunning());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mHandle) {
            mHandle.removeCallbacksAndMessages(null);
            mHandle = null;
        }
        if (null != mScheduleController) {
            mScheduleController.close();
        }
        clearAnimYoYo();

        DataBaseCalendarManager.releaseInstance();
    }

    private void clearAnimYoYo() {

        if (null != yearEnterYo) {
            yearEnterYo.stop(true);
            yearEnterYo = null;
        }
        if (null != yearExitYo) {
            yearExitYo.stop(true);
            yearExitYo = null;
        }
        if (null != monthEnterYo) {
            monthEnterYo.stop(true);
            monthEnterYo = null;
        }
        if (null != monthExitYo) {
            monthExitYo.stop(true);
            monthExitYo = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setStatusBar();
        onDateUpdate();
        onRefreshScheduleList();
    }

    /**
     * 设置状态栏颜色状态
     * */
    private void setStatusBar() {
        StatusBarUtil.setStateBar(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    /**
     * 如果日程数据被删除了就刷新
     */
    private void onRefreshScheduleList() {
        if (AppConst.IsNeedRefreshSchedule) {
            if (null != mScheduleController) {
                mScheduleController.startSelectScheduleSearch(selectTimeMill);
                refreshCalendarView(selectTimeMill);
            }
        }
        AppConst.IsNeedRefreshSchedule = false;
    }

    /**
     * 更新日期，当时间到第二天或者更改系统时间时刷新
     */
    private void onDateUpdate() {
        if (null == slSchedule.getMonthCalendar().getCurrentMonthView()) {
            return;
        }
        int currentYear = slSchedule.getMonthCalendar().getCurrentMonthView().getCurrentYear();
        int currentMonth = slSchedule.getMonthCalendar().getCurrentMonthView().getCurrentMonth();
        int currentDay = slSchedule.getMonthCalendar().getCurrentMonthView().getCurrentDay();

        Calendar now = Calendar.getInstance();
        if (currentYear != now.get(Calendar.YEAR) || currentMonth != now.get(Calendar.MONTH) || currentDay != now.get(Calendar.DAY_OF_MONTH)) {
            //重新刷新整个日历视图
            refreshCalendarView(Calendar.getInstance().getTimeInMillis());
        }
    }

    /**
     * 重新刷新整个日历视图
     */
    private void refreshCalendarView(long selectTimeMill) {
        if (null == mMonthCalendarView || null == mWeekCalendarView) {
            return;
        }

        //刷新月视图
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(selectTimeMill);
        int cYear = currentCalendar.get(Calendar.YEAR);
        int cMonth = currentCalendar.get(Calendar.MONTH);
        int cDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        int monthViewPosition = (cYear - ConstData.MIN_YEAR) * 12 + cMonth;
        mMonthCalendarView.setCurrentItem(monthViewPosition, false);
        mMonthCalendarView.refresh();
        if (null != mMonthCalendarView.getCurrentMonthView()) {
            mMonthCalendarView.getCurrentMonthView().clickThisMonth(cYear, cMonth, cDay);
        }

        //刷新周视图
        DateTime mStartDate = new DateTime(currentCalendar);
        mStartDate = mStartDate.plusDays(-mStartDate.getDayOfWeek() % 7);
        int weekViewPosition = CalendarUtils.getWeeksAgo(ConstData.MIN_YEAR, ConstData.MIN_MONTH - 1, ConstData.MIN_DAY, mStartDate.getYear(), mStartDate.getMonthOfYear() - 1, mStartDate.getDayOfMonth());
        mWeekCalendarView.setCurrentItem(weekViewPosition, false);
        mWeekCalendarView.refresh();
        if (null != mWeekCalendarView.getCurrentWeekView()) {
            mWeekCalendarView.getCurrentWeekView().clickThisWeek(cYear, cMonth, cDay);
        }
    }


    @Override
    public void onBackPressed() {
        if (mState == CalendarViewManager.ScheduleState.Year && !isMonthYearAnimRunning()) {
            showMonthView(selectTimeMill);
        } else if (isMonthYearAnimRunning()) {
            //预留
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //刷新联系人生日
        if (requestCode == ConstData.INTENT_SCHEDULE_BIRTHDAY_REFRESH) {
            onActivityBackScheduleRefresh();
            refreshCalendarView(selectTimeMill);
        }
        if (resultCode == ConstData.INTENT_SCHEDULE_REFRESH) {
            onActivityBackScheduleRefresh();
        }

        //刷新日程圆点
        if (RESULT_CANCELED != resultCode) {
            //不为0，则表示日程修改过，直接进行更新
            //重新刷新视图
            refreshCalendarView(selectTimeMill);
        }
    }

    /**
     * 刷新日程数据
     */
    private void onActivityBackScheduleRefresh() {
        //更新主页日历列表
        if (null != mScheduleController) {
            mScheduleController.startSelectScheduleSearch(selectTimeMill);
        }
    }
}

