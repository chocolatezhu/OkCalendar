package com.ppzhu.calendar.schedule.add;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.eebbk.common.utils.datatimewheel.DateChooseWheelViewDialog;
import com.eebbk.common.utils.datatimewheel.util.DatechooseConfig;
import com.eebbk.common.utils.datatimewheel.util.ModeConst;
import com.ppzhu.calendar.R;
import com.ppzhu.calendar.bean.Schedule;
import com.ppzhu.calendar.constants.ConstData;
import com.ppzhu.calendar.constants.ScheduleConst;
import com.ppzhu.calendar.database.DataBaseCalendarManager;
import com.ppzhu.calendar.schedule.ScheduleController;
import com.ppzhu.calendar.schedule.add.customreminder.ReminderActivity;
import com.ppzhu.calendar.schedule.add.customrepeat.RepeatActivity;
import com.ppzhu.calendar.utils.CalendarUtils;
import com.ppzhu.calendar.utils.DateFormatter;
import com.ppzhu.calendar.utils.InputManagerUtil;
import com.ppzhu.calendar.utils.MaxLengthWatcher;
import com.ppzhu.calendar.utils.StatusBarUtil;
import com.ppzhu.calendar.utils.T;
import com.ppzhu.calendar.view.dialog.CustomAlertDialog;
import com.ppzhu.calendar.view.dialog.ImplementCustomAlertDialogListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddScheduleActivity extends FragmentActivity implements View.OnClickListener, NumberPicker.Formatter {

    private Context mContext;
    private EditText mEtTitle;
    private EditText mEtLocation;
    private EditText mEtRemark;
    private TextView mTitle;
    private TextView mTvStart;
    private TextView mTvEnd;
    private TextView mTvRepeat;
    private TextView mTvReminder;
    private TextView mTvCancel;
    private TextView mTvSave;
    private Switch mSwitchView;
    private RelativeLayout mAllDaySwitchLayout;
    private RelativeLayout rl_start;
    private RelativeLayout rl_end;
    private RelativeLayout rl_repeat;
    private RelativeLayout rl_reminder;

    private AlertDialog mDatePickerAlertDialog;
    private DateChooseWheelViewDialog mDateChooseWheelViewDialog;

    //记录时间选择器的日期
    private Calendar mTimePickCalendar;

    //日程保存的日期时间，查找所有日程的关键字，保存格式为yyyy年MM月dd日
    private String mStartDate;

    private Schedule mInitSchedule;
    //是否更新，从日程详情编辑跳转过来
    private Boolean isUpdateScheduleMode = false;

    private ArrayList<Integer> repeatList = new ArrayList<>();
    //判断是否自定义
    private boolean isCustomRepeat = false;
    private String mRepeatId = "0";
    private int mReminderId = 3;
    private int mRepeatMode = 0;
    //0全天关闭，1全天打开
    private int isAllDay = 0;
    private String mUpdateScheduleId;
    private long mAlertTime;
    private long mEndTimeMill;
    //全天打开的时间格式
    private long mAllDayStartTime;
    private long mAllDayEndTime;

    //当前编辑的是否结束日期
    private boolean isEndDate = false;
    private String str = "开始";

    private CharSequence timeFormat = "yyyy年M月d日  HH:mm";
    private CharSequence allDayTimeFormat = "yyyy年M月d日";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_schedule_activity);
        getWindow().setBackgroundDrawable(null);
        initView();
        getEditIntentInfo();
        bindEvents();
    }

    /**
     * 获取编辑日程的Intent信息
     * */
    private void getEditIntentInfo() {
        Intent it = getIntent();
        Bundle bundle = it.getExtras();
        if (null == bundle){
            initSchedule();
            return;
        }
        mInitSchedule = bundle.getParcelable(ConstData.INTENT_SCHEDULE_EDIT_KEY);
        if (null == mInitSchedule){
            initSchedule();
            return;
        }
        isUpdateScheduleMode = true;
        mUpdateScheduleId = mInitSchedule.getId();
        String mScheduleTitle = mInitSchedule.getTitle() + "";
        String mScheduleLocatoin = mInitSchedule.getLocation() + "";
        isAllDay = mInitSchedule.getAllDay();
        String mStartTime = mInitSchedule.getStartTime() + "";
        String mEndTime = mInitSchedule.getEndTime() + "";
        mReminderId = mInitSchedule.getRemindId();
        String mRemark = mInitSchedule.getRemark() + "";
        mRepeatId = mInitSchedule.getRepeatId();
        mRepeatMode = mInitSchedule.getRepeatMode();
        mAlertTime = mInitSchedule.getAlertTime();
        mEndTimeMill = mInitSchedule.getEndTimeMill();
        mAllDayStartTime = mAlertTime;
        mAllDayEndTime = mEndTimeMill;
        mStartDate = mInitSchedule.getDate();

        mEtTitle.setText(mScheduleTitle);
        //设置光标位置在末尾
        if (!TextUtils.isEmpty(mEtTitle.getText())){
            mEtTitle.setSelection(mEtTitle.length());
            mEtTitle.requestFocus();
        }
        mEtLocation.setText(mScheduleLocatoin);

        isCustomRepeat = 1 == mRepeatMode;

        if (1 == isAllDay) {
            mSwitchView.setChecked(true);
        } else {
            mSwitchView.setChecked(false);
        }
        mTitle.setText("编辑日程");
        mTvStart.setText(getStartTimeStr());
        mTvEnd.setText(getEndTimeStr());
        mTvRepeat.setText(getRepeatStr() + "");
        mTvReminder.setText(ConstData.REMINDERSTR[mInitSchedule.getRemindId()] + "");
        mEtRemark.setText(mRemark);
    }

    private String getStartTimeStr(){
        String startTime;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mInitSchedule.getAlertTime());
        if (1 == isAllDay){
            startTime = DateFormat.format(allDayTimeFormat, calendar) + "  " + DateFormatter.getWeekDay(calendar);
        } else {
            startTime = DateFormat.format(timeFormat, calendar) + "";
        }

        return startTime;
    }

    private String getEndTimeStr(){
        String endTime;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mInitSchedule.getEndTimeMill());
        if (1 == isAllDay){
            endTime = DateFormat.format(allDayTimeFormat, calendar) + "  " + DateFormatter.getWeekDay(calendar);
        } else {
            endTime = DateFormat.format(timeFormat, calendar) + "";
        }

        return endTime;
    }

    private String getRepeatStr(){
        String[] str = mRepeatId.split(",");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length; i++) {
            int temp = Integer.parseInt(str[i]);
            repeatList.add(temp);
            if (isCustomRepeat){
                //自定义
                if (repeatList.size() == 7){
                    builder.append(ConstData.REPEATSTR[1]) ;
                }else {
                    builder.append(ConstData.CUSTOMREPEATSTR[temp]);
                    if (i != str.length - 1) {
                        builder.append(",");
                    }
                }
            } else {
                builder.append(ConstData.REPEATSTR[temp]);
            }
        }
        return builder.toString();
    }

    private void initView() {
        mContext = this;
        mEtTitle = (EditText) findViewById(R.id.et_title);
        mEtLocation = (EditText) findViewById(R.id.et_location);
        mEtRemark = (EditText) findViewById(R.id.et_remark);

        mEtTitle.addTextChangedListener(new MaxLengthWatcher(AddScheduleActivity.this, ScheduleConst.MAX_SCHEDULE_TITLE_INPUT_LENGTH));
        mEtLocation.addTextChangedListener(new MaxLengthWatcher(AddScheduleActivity.this, ScheduleConst.MAX_SCHEDULE_TITLE_INPUT_LENGTH));
        mEtRemark.addTextChangedListener(new MaxLengthWatcher(AddScheduleActivity.this, ScheduleConst.MAX_SCHEDULE_REMARK_INPUT_LENGTH));
//        mEtTitle.setFilters(TextFilter.mInputFiltersSymbol);
//        mEtLocation.setFilters(TextFilter.mInputFiltersSymbol);
//        mEtRemark.setFilters(TextFilter.mInputFiltersSymbol);

        mTitle = (TextView) findViewById(R.id.tv_new_schedule);
        mTvStart = (TextView) findViewById(R.id.tv_start);
        mTvEnd = (TextView) findViewById(R.id.tv_end);
        mTvRepeat = (TextView) findViewById(R.id.tv_repeat);
        mTvReminder = (TextView) findViewById(R.id.tv_reminder);
        mTvCancel = (TextView) findViewById(R.id.tv_cancle);
        mTvSave = (TextView) findViewById(R.id.tv_save);
        mSwitchView = (Switch) findViewById(R.id.tv_switch);
        mAllDaySwitchLayout= (RelativeLayout) findViewById(R.id.rl_all_day_switch);
        rl_start = (RelativeLayout) findViewById(R.id.rl_start);
        rl_end = (RelativeLayout) findViewById(R.id.rl_end);
        rl_repeat = (RelativeLayout) findViewById(R.id.rl_repeat);
        rl_reminder = (RelativeLayout) findViewById(R.id.rl_reminder);
        iniDate();
    }

    private void iniDate() {
        //获取选中日期的时间
        Intent it = getIntent();
        long selectTime = it.getLongExtra(ConstData.INTENT_SCHEDULE_SELECT_DATE_KEY, System.currentTimeMillis());
        Calendar selectCalendar = Calendar.getInstance();
        selectCalendar.setTimeInMillis(selectTime);
        int year = selectCalendar.get(Calendar.YEAR);
        int month = selectCalendar.get(Calendar.MONTH);
        int day = selectCalendar.get(Calendar.DAY_OF_MONTH);

        mTimePickCalendar = Calendar.getInstance();
        mTimePickCalendar.set(year, month, day);
        mStartDate = (String) DateFormat.format("yyyy年MM月dd日", mTimePickCalendar);
        mAlertTime = mTimePickCalendar.getTimeInMillis();
        mTvStart.setText(DateFormat.format(timeFormat, mTimePickCalendar));

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(year, month, day);
        endCalendar.add(Calendar.HOUR_OF_DAY, +1);
        mEndTimeMill = endCalendar.getTimeInMillis();
        mTvEnd.setText(DateFormat.format(timeFormat, endCalendar));
    }

    /**
     * 初始化初始的Schedule
     * <P> 用于判断退出新建日程界面是否有更改，提示保存 </P>
     * */
    private void initSchedule() {
        mInitSchedule = new Schedule();
        mInitSchedule.setTitle(mEtTitle.getText().toString());
        mInitSchedule.setLocation(mEtLocation.getText().toString());
        mInitSchedule.setAllDay(isAllDay);
        mInitSchedule.setStartTime(mTvStart.getText().toString());
        mInitSchedule.setEndTime(mTvEnd.getText().toString());
        mInitSchedule.setRepeatMode(mRepeatMode);
        mInitSchedule.setRepeatId(mRepeatId);
        mInitSchedule.setRemindId(mReminderId);
        mInitSchedule.setRemark(mEtRemark.getText().toString());
    }

    private void bindEvents() {
        mTvSave.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);
        rl_start.setOnClickListener(this);
        rl_end.setOnClickListener(this);
        rl_repeat.setOnClickListener(this);
        rl_reminder.setOnClickListener(this);
        mAllDaySwitchLayout.setOnClickListener(this);
        mSwitchView.setClickable(false);
//        mSwitchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                //是否打开全天
//                setAllDayOpen(isChecked);
//            }
//        });
    }

    /**
     * 是否打开全天
     * @param isChecked switch开关状态
     * */
    private void setAllDayOpen(boolean isChecked) {
        if (isChecked) {
            isAllDay = 1;
            //开始日期
            Calendar startCalendar = getAllDayStartTime();
            mAllDayStartTime = startCalendar.getTimeInMillis();
            mTvStart.setText(DateFormat.format(allDayTimeFormat, startCalendar) + "  " + DateFormatter.getWeekDay(startCalendar));

            //结束日期
            Calendar endCalendar = getAllDayEndTime();
            mAllDayEndTime = endCalendar.getTimeInMillis();
            mTvEnd.setText(DateFormat.format(allDayTimeFormat, endCalendar) + "  " + DateFormatter.getWeekDay(endCalendar));
        } else {
            isAllDay = 0;
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTimeInMillis(mAlertTime);
            mTvStart.setText(DateFormat.format(timeFormat, startCalendar));
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTimeInMillis(mEndTimeMill);
            mTvEnd.setText(DateFormat.format(timeFormat, endCalendar));
        }
    }

    /**
     * 获取全天状态时的结束日期
     * <p> 时分秒设置为0，这里只需要整天时间，比如结束日期是：2016年10月3日 23:59 </p>
     * */
    private Calendar getAllDayEndTime() {
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTimeInMillis(mEndTimeMill);
        endCalendar.set(
                endCalendar.get(Calendar.YEAR),
                endCalendar.get(Calendar.MONTH),
                endCalendar.get(Calendar.DAY_OF_MONTH),
                ScheduleConst.MAX_HOUR,
                ScheduleConst.MAX_MINUTE,
                ScheduleConst.MAX_SECOND);
        return endCalendar;
    }

    /**
     * 获取全天状态时的开始日期
     * <p> 这里整天时间，提醒时间设置为上午9点，比如开始日期是：2016年10月5日 9:00 </p>
     * */
    private Calendar getAllDayStartTime() {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTimeInMillis(mAlertTime);
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        return startCalendar;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.showSoftInput(mEtTitle, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_all_day_switch:
                //是否打开全天
                setAllDayOpen(!mSwitchView.isChecked());
                mSwitchView.setChecked(!mSwitchView.isChecked());
                break;
            case R.id.rl_start:
                if (isDatePickerDialogShowing()) {
                    return;
                }
                str = "开始";
                isEndDate = false;
                showDatePickerDialog(isAllDay());
                InputManagerUtil.closeSoftInput(AddScheduleActivity.this);
                break;
            case R.id.rl_end:
                if (isDatePickerDialogShowing()) {
                    return;
                }
                str = "结束";
                isEndDate = true;
                showDatePickerDialog(isAllDay());
                InputManagerUtil.closeSoftInput(AddScheduleActivity.this);
                break;
            case R.id.rl_repeat:
                scheduleRepeat();
                break;
            case R.id.rl_reminder:
                scheduleRemindEdit();
                break;
            case R.id.tv_cancle:
                InputManagerUtil.closeSoftInput(mContext);
                finish();
                break;
            case R.id.tv_save:
                InputManagerUtil.closeSoftInput(mContext);
                saveSchedule();
                break;
            default:
                break;
        }
    }


    private boolean isAllDay(){
        return 1 == isAllDay;
    }

    private void saveTimePick() {
        if (!isEndDate){
            //开始日期
            saveStartTime();
        } else {
            //结束日期
            saveEndTime();
        }
    }

    /**
     * 保存选择的结束日期
     * <p> 结束日期是否小于开始日期，是否选择全天状态，需要针对不同情况设定最终的日期 </p>
     * */
    private void saveEndTime() {
        Calendar endCalendar= Calendar.getInstance();
        endCalendar.set(mTimePickCalendar.get(Calendar.YEAR),
                mTimePickCalendar.get(Calendar.MONTH),
                mTimePickCalendar.get(Calendar.DAY_OF_MONTH),
                mTimePickCalendar.get(Calendar.HOUR_OF_DAY),
                mTimePickCalendar.get(Calendar.MINUTE),
                0);
        mEndTimeMill = endCalendar.getTimeInMillis();
        if (mAlertTime > mEndTimeMill) {
            //当结束时间小于开始时间
            mEndTimeMill = mAlertTime;
        }

        if (1 == isAllDay){
            //全天状态
            endCalendar = getAllDayEndTime();
            mAllDayEndTime = endCalendar.getTimeInMillis();
            mTvEnd.setText(DateFormat.format(allDayTimeFormat, endCalendar) + "  " + DateFormatter.getWeekDay(endCalendar));
        } else {
            endCalendar.setTimeInMillis(mEndTimeMill);
            mTvEnd.setText(DateFormat.format(timeFormat, endCalendar));
        }
    }

    /**
     * 保存选择的开始日期
     * <p> 结束日期是否小于开始日期，是否选择全天状态，需要针对不同情况设定最终的日期 </p>
     * */
    private void saveStartTime() {
        Calendar startCalendar= Calendar.getInstance();
        startCalendar.set(mTimePickCalendar.get(Calendar.YEAR),
                mTimePickCalendar.get(Calendar.MONTH),
                mTimePickCalendar.get(Calendar.DAY_OF_MONTH),
                mTimePickCalendar.get(Calendar.HOUR_OF_DAY),
                mTimePickCalendar.get(Calendar.MINUTE),
                0);
        mAlertTime = startCalendar.getTimeInMillis();

        if (mAlertTime > mEndTimeMill) {
            //当结束时间小于开始时间
            mEndTimeMill = mAlertTime;
        }

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTimeInMillis(mEndTimeMill);
        if (1 == isAllDay){
            //全天状态
            startCalendar = getAllDayStartTime();
            mAllDayStartTime = startCalendar.getTimeInMillis();

            endCalendar = getAllDayEndTime();
            mAllDayEndTime = endCalendar.getTimeInMillis();

            mTvStart.setText(DateFormat.format(allDayTimeFormat, startCalendar) + "  " + DateFormatter.getWeekDay(startCalendar));
            mTvEnd.setText(DateFormat.format(allDayTimeFormat, endCalendar) + "  " + DateFormatter.getWeekDay(endCalendar));
        } else {
            mTvStart.setText(DateFormat.format(timeFormat, startCalendar));
            mTvEnd.setText(DateFormat.format(timeFormat, endCalendar));
        }
    }

    private void scheduleRemindEdit() {
        Intent inReminder = new Intent();
        inReminder.putExtra(ConstData.INTENT_SCEDULE_REMIND_EDIT_KEY, mReminderId);
        inReminder.setClass(this, ReminderActivity.class);
        startActivityForResult(inReminder, ConstData.reminderCode);
    }

    private void scheduleRepeat() {
        Intent rlRepeat = new Intent();
        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList(ConstData.INTENT_SCHEDULE_REPEAT_EDIT_KEY, repeatList);
//        bundle.putString(ConstData.INTENT_SCHEDULE_REPEAT_EDIT_KEY, mRepeatId);
        bundle.putBoolean(ConstData.INTENT_SCHEDULE_REPEAT_IS_CUSTOM_REPEAT, isCustomRepeat);
        rlRepeat.putExtras(bundle);
        rlRepeat.setClass(this, RepeatActivity.class);
        startActivityForResult(rlRepeat, ConstData.repeatCode);
    }

    private void showDatePickerDialog(boolean isAllDay) {
        int mode;
        initPickerTime();
        AlertDialog.Builder builder = new AlertDialog.Builder(AddScheduleActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveTimePick();
                mDateChooseWheelViewDialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                mDateChooseWheelViewDialog.dismiss();
            }
        });
        mDatePickerAlertDialog = builder.create();
        mDatePickerAlertDialog.setTitle("");
        DatechooseConfig mDateChooseConfig = new DatechooseConfig();

        //是否打开了全天
        if (isAllDay) {
            mode = ModeConst.YEAR_MONTH_DAY;
        } else {
            mode = ModeConst.DAYMONTH_HOUR_MINUTE;
        }

        mDateChooseConfig.setmMonthLeadYearAdd(true);
        mDateChooseWheelViewDialog = new DateChooseWheelViewDialog(AddScheduleActivity.this, new DateChooseWheelViewDialog.DateChooseInterface(){
            @Override
            public void getDateTime(Date date, View view){
                Calendar calendar = getTimePickCalendar(date);
                setDatePickerTitle(calendar);
                mTimePickCalendar = calendar;
            }

            @Override
            public void getSetContenView(View view){

            }
        }, mode, mDatePickerAlertDialog, builder, mDateChooseConfig);

        mDateChooseWheelViewDialog.showDateChooseDialog();
    }

    /**
     * 获取时间选择器日期
     * <p> 不能超出最大或者最小日期</p>
     * @param date
     * */
    private Calendar getTimePickCalendar(Date date) {
        Calendar minCalendar = CalendarUtils.getMinCalendar();
        Calendar maxCalendar = CalendarUtils.getMaxCalendar();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if (calendar.compareTo(maxCalendar) == 1) {
            calendar =  maxCalendar;
        }
        if (calendar.compareTo(minCalendar) == -1) {
            calendar =  minCalendar;
        }
        return calendar;
    }

    /**
     * 初始化时间选择器的时间
     * */
    private void initPickerTime() {
        mTimePickCalendar = Calendar.getInstance();
        if (isEndDate) {
            mTimePickCalendar.setTimeInMillis(mEndTimeMill);
        } else {
            mTimePickCalendar.setTimeInMillis(mAlertTime);
        }
        DateChooseWheelViewDialog.setmDate(mTimePickCalendar);
    }

    /**
     * 设置时间选择器标题的时间
     * */
    private void setDatePickerTitle(Calendar calendar) {
        if (1 == isAllDay){
            mDatePickerAlertDialog.setTitle(str + "   " + DateFormat.format("yyyy/M/d", calendar));
        } else {
            mDatePickerAlertDialog.setTitle(str + "   " + DateFormat.format("yyyy/M/d  HH:mm", calendar));
        }
    }

    /**
     * 获取时间选择弹框开关状态
     * */
    private boolean isDatePickerDialogShowing() {
        return null != mDatePickerAlertDialog && mDatePickerAlertDialog.isShowing();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) { //resultCode为回传的标记，回传的是RESULT_OK
            case ConstData.repeatCode:
                Bundle b = data.getExtras(); //data为B中回传的Intent
                // mRepeatId = b.getInt("repeat");//str即为回传的值
                repeatList = b.getIntegerArrayList("repeat");
                isCustomRepeat = b.getBoolean(ConstData.INTENT_SCHEDULE_REPEAT_IS_CUSTOM_REPEAT);
                if (null == repeatList){
                    return;
                }

                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < repeatList.size(); i++) {
                    if (isCustomRepeat) {
                        //自定义
                        mRepeatMode = 1;
                        builder.append(ConstData.CUSTOMREPEATSTR[repeatList.get(i)]);
                        if (i != repeatList.size() - 1) {
                            builder.append(",");
                        }
                        mRepeatId = repeatList.get(i) +",";
                    } else {
                        mRepeatMode = 0;
                        builder.append(ConstData.REPEATSTR[repeatList.get(i)]);
                        mRepeatId = repeatList.get(i) + "";
                    }
                }
                if (isCustomRepeat && repeatList.size()==7){
                    builder.append(ConstData.REPEATSTR[1]);
                }
                mTvRepeat.setText(builder.toString());
                break;
            case ConstData.reminderCode:
                Bundle bReminder = data.getExtras(); //data为B中回传的Intent
                mReminderId = bReminder.getInt("reminder");//str即为回传的值
                mTvReminder.setText(ConstData.REMINDERSTR[mReminderId]);
                break;
        }
    }

    /**
     * 保存日程信息到数据库里
     * */
    private void saveSchedule() {
        String title = mEtTitle.getText().toString();
        if (title.isEmpty() || (title.replaceAll(" ", "")).equals("")){
            T.getInstance(AddScheduleActivity.this).s("日程标题不能为空");
            return;
        }
        //获取要保存的Schedule
        Schedule schedule = getData();

        //这里需要区分新建、更新日程两种情况
        if (isUpdateScheduleMode){
            //更新日程
            int updateResult = DataBaseCalendarManager.getInstance(this).updateSchedule(schedule);
            if (updateResult != -1)
                Toast.makeText(this, "日程更新成功", Toast.LENGTH_SHORT).show();
            else{
                Toast.makeText(this, "日程更新失败", Toast.LENGTH_SHORT).show();
            }
        } else {
            //新建日程
            DataBaseCalendarManager dataBaseCalendarManager = DataBaseCalendarManager.getInstance(this);
            long saveResult =  dataBaseCalendarManager.saveSchedule(schedule);
            if (saveResult != -1) {
                Toast.makeText(this, "日程保存成功", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(this, "日程保存失败", Toast.LENGTH_SHORT).show();
            }
        }

        setResult(ConstData.INTENT_SCHEDULE_REFRESH);
        finish();
    }

    /**
     * 获取要保存的Schedule
     * */
    public Schedule getData() {
        Schedule schedule = new Schedule();
        String str = "";
        if (repeatList.size() < 1) {
            repeatList.add(0);
        }

        for (int i = 0; i < repeatList.size(); i++) {
            if (1 == repeatList.size()) {
                str += repeatList.get(i);
            } else {
                str += repeatList.get(i) + ",";
            }
        }

        //这里需要区分是否全天状态，保存的开始、结束日期会不同
        if (1 == isAllDay){
            //全天状态
            schedule.setAlertTime(mAllDayStartTime);
            schedule.setEndTimeMill(mAllDayEndTime);
        } else {
            schedule.setAlertTime(mAlertTime);
            schedule.setEndTimeMill(mEndTimeMill);
        }
        schedule.setRepeatId(str);
        schedule.setRepeatMode(mRepeatMode);
        schedule.setRemindId(mReminderId);
        schedule.setLocation(mEtLocation.getText().toString());
        schedule.setAllDay(isAllDay);
        schedule.setTitle(mEtTitle.getText().toString());
        schedule.setRemark(mEtRemark.getText().toString());
        schedule.setStartTime(mTvStart.getText().toString());
        schedule.setEndTime(mTvEnd.getText().toString());

        //开始的时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mAlertTime);
        mStartDate = (String) DateFormat.format("yyyy年MM月dd日", calendar);
        schedule.setDate(mStartDate);
        schedule.setId(String.valueOf(System.currentTimeMillis()));

        //这里需要区分新建、更新日程两种情况
        if (isUpdateScheduleMode){
            //更新日程
            schedule.setId(mUpdateScheduleId);
        } else {
            //新建日程
            String id  = String.valueOf(System.currentTimeMillis())+ "";
            schedule.setId(id);
        }

        return schedule;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        //这里导致了ScrollView不能滚动
//        StatusBarUtil.setStateBar(this);
        StatusBarUtil.setStatusBarColor(AddScheduleActivity.this, getResources().getColor(R.color.menu_tab_red));
    }

    @Override
    public String format(int value) {
        String tmpStr = String.valueOf(value);
        if (value < 10) {
            tmpStr = "0" + tmpStr;
        }
        return tmpStr;
    }


    @Override
    public void finish() {
        super.finish();
        if (!isUpdateScheduleMode) {
            overridePendingTransition(android.R.anim.fade_in, R.anim.bottom_exit);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mDatePickerAlertDialog) {
            mDatePickerAlertDialog.cancel();
            mDatePickerAlertDialog = null;
        }
        if (null != mDateChooseWheelViewDialog) {
            mDateChooseWheelViewDialog.cancel();
            mDateChooseWheelViewDialog = null;
        }
    }
}
