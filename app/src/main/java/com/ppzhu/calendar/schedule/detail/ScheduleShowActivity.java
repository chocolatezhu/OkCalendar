package com.ppzhu.calendar.schedule.detail;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ppzhu.calendar.R;
import com.ppzhu.calendar.bean.Schedule;
import com.ppzhu.calendar.constants.ConstData;
import com.ppzhu.calendar.constants.ScheduleConst;
import com.ppzhu.calendar.database.DataBaseCalendarManager;
import com.ppzhu.calendar.schedule.ScheduleController;
import com.ppzhu.calendar.schedule.add.AddScheduleActivity;
import com.ppzhu.calendar.schedule.add.customreminder.ReminderActivity;
import com.ppzhu.calendar.utils.DateFormatter;
import com.ppzhu.calendar.utils.DialogUtils;
import com.ppzhu.calendar.utils.StatusBarUtil;
import com.ppzhu.calendar.view.dialog.CustomAlertDialog;
import com.ppzhu.calendar.view.dialog.ImplementCustomAlertDialogListener;

import java.util.Calendar;
import java.util.Date;

public class ScheduleShowActivity extends FragmentActivity implements View.OnClickListener {

    private ImageView mBack;
    private TextView mTitle;
    private TextView mTvEdit;
    private TextView mShowTitle;
    private TextView mShowLocation;
    private TextView mShowStartTime;
    private TextView mShowEndTime;
    private TextView mShowRepeat;
    private TextView mShowReminder;
    private TextView mShowRemark;
    private RelativeLayout rl_reminder_show;
    private LinearLayout ll_remark;
    private Schedule schedule;
    private int reminderId = 0;
    private Dialog mDialog;
    private CustomAlertDialog mScheduleDeleteDialog;

    private boolean isScheduleFresh = false;
    private Calendar mSelectCalendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_show);
        getWindow().setBackgroundDrawable(null);
        getIntentInfo();
        initView();
        bindEvent();
    }

    private void getIntentInfo() {
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        long selectTime = intent.getLongExtra(ConstData.INTENT_SCHEDULE_SELECT_DATE_KEY, -1);
        if (-1 != selectTime) {
            mSelectCalendar = Calendar.getInstance();
            mSelectCalendar.setTimeInMillis(selectTime);
        }

        if (id == null || id.equals(" ")) {
            //这里finish了为什么还会执行下面的initView()
            finish();
        }
        schedule = DataBaseCalendarManager.getInstance(this).getSchedule(id);
        if (schedule == null) {
            //这里finish了为什么还会执行下面的initView()
            finish();
        }
    }

    private void bindEvent() {
        mBack.setOnClickListener(this);
        mTvEdit.setOnClickListener(this);
        rl_reminder_show.setOnClickListener(this);
    }

    private void initView() {
        mBack = (ImageView) findViewById(R.id.iv_title_bar);
        mTitle = (TextView) findViewById(R.id.tv_title_bar);
        mTitle.setText(getResources().getString(R.string.schedule_detail_title));
        mTvEdit = (TextView) findViewById(R.id.tv_title_edit);
        mTvEdit.setText(getResources().getString(R.string.schedule_detail_edit));
        mShowTitle = (TextView) findViewById(R.id.show_title);
        mShowLocation = (TextView) findViewById(R.id.show_location);
        mShowStartTime = (TextView) findViewById(R.id.show_start_time);
        mShowEndTime = (TextView) findViewById(R.id.show_end_time);
        mShowRepeat = (TextView) findViewById(R.id.show_repeat);
        mShowReminder = (TextView) findViewById(R.id.show_reminder);
        mShowRemark = (TextView) findViewById(R.id.show_remark);
        Button mScheduleDeleteBtn = (Button) findViewById(R.id.schedule_show_bottom_delete_btn);
        mScheduleDeleteBtn.setOnClickListener(this);
        rl_reminder_show = (RelativeLayout) findViewById(R.id.rl_reminder_show);
        ll_remark = (LinearLayout) findViewById(R.id.ll_remark);

        refreshView();
    }

    private void refreshView() {
        if (null == schedule){
            return;
        }
        mShowTitle.setText(schedule.getTitle());
        if (TextUtils.isEmpty(schedule.getLocation())){
            mShowLocation.setVisibility(View.GONE);
        } else {
            mShowLocation.setVisibility(View.VISIBLE);
            mShowLocation.setText(schedule.getLocation() + "");
        }
        String repeatId = schedule.getRepeatId();

        //初始化开始、结束日历格式
        initDate();

        String[] str = repeatId.split(",");
        String repeat = "";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length; i++) {
            int temp = Integer.parseInt(str[i]);
            if (isCustomRepeat()){
                //自定义
                if (str.length==7){
                    builder.append(ConstData.REPEATSTR[1]);
                    break;
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
        mShowRepeat.setText(builder.toString());
        mShowReminder.setText(ConstData.REMINDERSTR[schedule.getRemindId()]);
        if (TextUtils.isEmpty(schedule.getRemark())){
            ll_remark.setVisibility(View.GONE);
        } else {
            ll_remark.setVisibility(View.VISIBLE);
            mShowRemark.setText(schedule.getRemark() + "");
        }
    }

    /**
     * 判断是否重复日程
     * */
    private boolean isRepeatSchedule() {
        if (null == schedule) {
            return false;
        }
        return !(ScheduleConst.SCHEDULE_NORMAL_REPEAT_MODE == schedule.getRepeatMode() && ScheduleConst.SCHEDULE_REPEAT_NEVER.equals(schedule.getRepeatId()));

    }

    /**
     * 判断日程是否每天重复
     * */
    private boolean isRepeatEveryDay() {
        boolean isRepeatEveryDay = false;
        String[] str = schedule.getRepeatId().split(",");
        if (ScheduleConst.SCHEDULE_CUSTOM_REPEAT_MODE == schedule.getRepeatMode()) {
            if (7 == str.length) {
                isRepeatEveryDay = true;
            }
        } else {
            if (ScheduleConst.SCHEDULE_REPEAT_EVERY_DAY.equals(schedule.getRepeatId())) {
                isRepeatEveryDay = true;
            }
        }
        return isRepeatEveryDay;
    }

    /**
     * 判断是否打开全天
     * */
    private boolean isAllDay(){
        return 1 == schedule.getAllDay();
    }

    private boolean isCustomRepeat(){
        if (null == schedule){
            return false;
        }
        return 1 == schedule.getRepeatMode();
    }

    /**
     * 初始化日程开始、结束日期显示
     * */
    private void initDate() {
        Calendar startCalendar = getStartCalendar();
        Calendar endCalendar = getEndCalendar();
        boolean isSameDay = isSameDay(startCalendar, endCalendar);

        if (isSameDay && isRepeatSchedule()) {
            //如果是重复日程，开始的日期要跟随选中的日期变化
            if (null != mSelectCalendar) {
                startCalendar.set(Calendar.YEAR, mSelectCalendar.get(Calendar.YEAR));
                startCalendar.set(Calendar.MONTH , mSelectCalendar.get(Calendar.MONTH));
                startCalendar.set(Calendar.DAY_OF_MONTH, mSelectCalendar.get(Calendar.DAY_OF_MONTH));
            }
        }

        initStartDate(startCalendar, endCalendar, isSameDay);
        initEndDate(startCalendar, endCalendar, isSameDay);
    }

    /**
     * 判断是否同一天
     * */
    private boolean isSameDay (Calendar startCalendar, Calendar endCalendar) {
        String startDay = (String) DateFormat.format("yyyy年M月d日", startCalendar);
        String endDay = (String) DateFormat.format("yyyy年M月d日", endCalendar);
        boolean isSameDay;
        if (startDay != null && endDay != null && startDay.equals(endDay)) {
            isSameDay = true;
        } else {
            isSameDay = false;
        }
        return isSameDay;
    }

    /**
     * 获取开始提醒日期
     * */
    private Calendar getStartCalendar() {
        return formatDate(schedule.getAlertTime());
    }

    /**
     * 获取结束提醒日期
     * */
    private Calendar getEndCalendar() {
        return formatDate(schedule.getEndTimeMill());
    }

    /**
     * 初始化日程结束日程格式
     * */
    private void initEndDate(Calendar startCalendar, Calendar endCalendar,boolean isSameDay) {
        String endDates = "";
        int weekDay = endCalendar.get(Calendar.DAY_OF_WEEK);
        if (isSameDay){
            if (isAllDay()) {
                endDates = "全天";
            } else {
                endDates = DateFormat.format("HH:mm", startCalendar) + " - " + DateFormat.format("HH:mm", endCalendar);
            }
        }else {
            if (isAllDay()) {
                endDates = "结束：" + DateFormat.format("yyyy年M月d日", endCalendar) + "   " + ConstData.CUSTOMREPEATSTR[weekDay - 1] +
                        "\n" + "全天";
            } else {
                endDates = "结束：" + DateFormat.format("yyyy年M月d日 HH:mm", endCalendar) + "   " + ConstData.CUSTOMREPEATSTR[weekDay - 1];
            }
        }

        mShowEndTime.setText(endDates);
    }

    /**
     * 初始化日程开始日期格式
     * */
    private void initStartDate(Calendar startCalendar, Calendar endCalendar, boolean isSameDay) {
        String startDates = "";
        int weekDay = startCalendar.get(Calendar.DAY_OF_WEEK);
        if (isSameDay) {
            startDates = DateFormat.format("yyyy年M月d日", startCalendar) + "   " + ConstData.CUSTOMREPEATSTR[weekDay - 1];
        } else {
           if (isAllDay()){
                startDates = "开始：" + DateFormat.format("yyyy年M月d日", startCalendar) + "   " + ConstData.CUSTOMREPEATSTR[weekDay - 1];
            } else {
                startDates = "开始：" + DateFormat.format("yyyy年M月d日 HH:mm", startCalendar) + "   " + ConstData.CUSTOMREPEATSTR[weekDay - 1];
            }
        }

        mShowStartTime.setText(startDates);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_reminder_show:
                scheduleRemindEdit();
                break;
            case R.id.iv_title_bar:
                goBack();
                break;
            case R.id.tv_title_edit:
                scheduleEdit();
                break;
            case R.id.schedule_show_bottom_delete_btn:
                showDeleteScheduleDialog();
                break;
            default:
                break;
        }
    }

    private void goBack() {
        if (isScheduleFresh){
            setResult(ConstData.INTENT_SCHEDULE_REFRESH);
        }
        finish();
    }



    private void scheduleRemindEdit() {
        if (null == schedule) {
            return;
        }
        Intent rlReminder = new Intent();
        reminderId = schedule.getRemindId();
        rlReminder.putExtra(ConstData.INTENT_SCEDULE_REMIND_EDIT_KEY, reminderId);
        rlReminder.setClass(this, ReminderActivity.class);
        startActivityForResult(rlReminder, ConstData.reminderCode);
    }

    private void scheduleEdit() {
        if (null == schedule){
            return;
        }
        Intent it = new Intent(ScheduleShowActivity.this, AddScheduleActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(ConstData.INTENT_SCHEDULE_EDIT_KEY, schedule);
        it.putExtras(bundle);
        startActivityForResult(it, ConstData.INTENT_SCHEDULE_REFRESH);
//        overridePendingTransition(R.anim.bottom_enter, R.anim.anim_fade_out);
    }

    /**
     * 日程删除弹框
     */
    private void showDeleteScheduleDialog() {
        mScheduleDeleteDialog = DialogUtils.scheduleDeleteDialog(this, new ImplementCustomAlertDialogListener() {
            @Override
            public void onPositiveButton() {
                super.onPositiveButton();
                deleteSchedule();
            }

            @Override
            public void onNegativeButton() {
                super.onNegativeButton();
                mScheduleDeleteDialog.dismiss();
            }
        });
        mScheduleDeleteDialog.show();
    }

    private boolean isBeforeToday(){
        if (null == schedule) {
            return false;
        }
        Calendar scheduleCalendar = DateFormatter.formatToCalendar(schedule.getDate(), "yyyy年MM月dd日");
        Calendar todayCalendar = Calendar.getInstance();
        Date scheduleDate = scheduleCalendar.getTime();
        Date todayDate = todayCalendar.getTime();
        return scheduleDate.before(todayDate);
    }

    private void deleteSchedule() {
        int result = DataBaseCalendarManager.getInstance(this).deleteSchedule(schedule.getId());
        if (-1 != result) {
            Toast.makeText(this, "删除日程成功", Toast.LENGTH_SHORT).show();
            setResult(ConstData.INTENT_SCHEDULE_REFRESH);
        } else {
            Toast.makeText(this, "删除日程失败", Toast.LENGTH_SHORT).show();
        }
        mScheduleDeleteDialog.dismiss();
        finish();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
//        getWindow().getDecorView().setBackground(null);
//        StatusBarUtil.setStateBar(this);
//        StatusBarUtil.setStatusBarGray(this);
        StatusBarUtil.setStatusBarColor(this, getResources().getColor(R.color.menu_tab_red));
    }

    @Override
    public void onBackPressed() {
        if (isScheduleFresh){
            setResult(ConstData.INTENT_SCHEDULE_REFRESH);
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case ConstData.reminderCode:
                Bundle bReminder = data.getExtras(); //data为B中回传的Intent
                reminderId = bReminder.getInt("reminder");//str即为回传的值
                if (reminderId != schedule.getRemindId()) {
                    mShowReminder.setText(ConstData.REMINDERSTR[reminderId]);
                    schedule.setRemindId(reminderId);
                    int result = DataBaseCalendarManager.getInstance(this).updateSchedule(schedule);
                    if (result != -1)
                        Toast.makeText(this, "更新日程成功", Toast.LENGTH_SHORT).show();
                    else{
                        Toast.makeText(this, "更新日程失败", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case ConstData.INTENT_SCHEDULE_REFRESH:
                isScheduleFresh = true;
                String id = schedule.getId();
                schedule = DataBaseCalendarManager.getInstance(this).getSchedule(id);
                refreshView();
                break;
            default:
                break;
        }
    }

    private Calendar formatDate(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return cal;
    }

    public String format(int value) {
        String tmpStr = String.valueOf(value);
        if (value < 10) {
            tmpStr = "0" + tmpStr;
        }
        return tmpStr;
    }

}
