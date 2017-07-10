package com.ppzhu.calendar.schedule;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.ppzhu.calendar.bean.Schedule;
import com.ppzhu.calendar.bean.ScheduleListBean;
import com.ppzhu.calendar.constants.ConstData;
import com.ppzhu.calendar.constants.ScheduleConst;
import com.ppzhu.calendar.database.DataBaseCalendarManager;
import com.ppzhu.calendar.utils.ScheduleUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author zzl
 * 日历控制类，一些逻辑操作写在这里
 * Created on 2016/9/21.
 */
public class ScheduleController {
    private Context mContext;
    ScheduleListener mScheduleListener;
    selectScheduleSearchAsyncTask mSelectScheduleSearchAsyncTask;
    AllScheduleSearchAsyncTask mAllScheduleSearchAsyncTask;

    private long mSelectDateMill;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");

    public ScheduleController() {
    }

    public ScheduleController(Context context, ScheduleListener listener) {
        mContext = context;
        mScheduleListener = listener;
    }

    /**
     * 搜索选中日期下的带有重复提醒的日程
     * @param selectDateMill 当前点击选中的日期
     * */
    public void startSelectScheduleSearch(long selectDateMill){
        mSelectDateMill = selectDateMill;
        cancelSelectScheduleSearchAsyncTask();
        mSelectScheduleSearchAsyncTask = new selectScheduleSearchAsyncTask();
        mSelectScheduleSearchAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 获取选中日期设置重复提醒的日程
     * @param selectDateMill 选中的日期
     * */
    private ScheduleListBean getSelectScheduleList(long selectDateMill) {
//        List<Schedule> selectList = DataBaseCalendarManager.getInstance(mContext).getDaySchedule(str);
        ScheduleListBean scheduleListBean = new ScheduleListBean();
        List<Schedule> selectList = new ArrayList<>();

        Calendar selectCalendar = Calendar.getInstance();
        selectCalendar.setTimeInMillis(selectDateMill);
        List<Schedule> allList = DataBaseCalendarManager.getInstance(mContext).getAllScheduleData();

        //添加生日提醒
        List<Schedule> allBirthDayScheduleList = ScheduleUtil.getAllBirthDayScheduleList(mContext);
        if (null != allBirthDayScheduleList && !allBirthDayScheduleList.isEmpty()) {
            allList.addAll(allBirthDayScheduleList);
        }
        Calendar scheduleCalendar = Calendar.getInstance();

        for (int i = 0; i < allList.size(); i++) {
            Schedule schedule = allList.get(i);
            if (null == schedule.getId()) {
                continue;
            }
            String repeatId = schedule.getRepeatId();
            long alertTime = schedule.getAlertTime();
            scheduleCalendar.setTimeInMillis(alertTime);
            int sYear = selectCalendar.get(Calendar.YEAR);
            int sMonth = selectCalendar.get(Calendar.MONTH);
            int sDay = selectCalendar.get(Calendar.DAY_OF_MONTH);
            int year = scheduleCalendar.get(Calendar.YEAR);
            int month = scheduleCalendar.get(Calendar.MONTH);
            int day = scheduleCalendar.get(Calendar.DAY_OF_MONTH);

            //小于日程开始日期的不做处理，只获取日程开始后的重复日程
            if (sYear < year){
                continue;
            } else if (sYear == year){
                if (sMonth < month){
                    continue;
                } else if (sMonth == month){
                    if (sDay < day){
                        continue;
                    }
                }
            }

            Calendar endScheduleCalendar = Calendar.getInstance();
            long endTime = schedule.getEndTimeMill();
            endScheduleCalendar.setTimeInMillis(endTime);
            int endYear = endScheduleCalendar.get(Calendar.YEAR);
            int endMonth = endScheduleCalendar.get(Calendar.MONTH);
            int endDay = endScheduleCalendar.get(Calendar.DAY_OF_MONTH);

            String[] repeatIdArr = repeatId.split(",");
            if (ScheduleConst.SCHEDULE_CUSTOM_REPEAT_MODE == schedule.getRepeatMode()){
                //如果选中日期与创建日期相等说明当天有日程
                boolean isSameDayCalendar = ScheduleUtil.isSameDayCalendar(selectCalendar, scheduleCalendar);
                if (isSameDayCalendar) {
                    selectList.add(schedule);
                }
                //自定义重复列表
                for (int j = 0; j < repeatIdArr.length; j ++){
                    String customRepeatIndex = repeatIdArr[j];
                    switch (customRepeatIndex){
                        case ScheduleConst.SCHEDULE_REPEAT_SUNDAY:
                            if (Calendar.SUNDAY == selectCalendar.get(Calendar.DAY_OF_WEEK) && !isSameDayCalendar){
                                selectList.add(schedule);
                            }
                            break;
                        case ScheduleConst.SCHEDULE_REPEAT_MONDAY:
                            if (Calendar.MONDAY == selectCalendar.get(Calendar.DAY_OF_WEEK) && !isSameDayCalendar){
                                selectList.add(schedule);
                            }
                            break;
                        case ScheduleConst.SCHEDULE_REPEAT_TUESDAY:
                            if (Calendar.TUESDAY == selectCalendar.get(Calendar.DAY_OF_WEEK) && !isSameDayCalendar){
                                selectList.add(schedule);
                            }
                            break;
                        case ScheduleConst.SCHEDULE_REPEAT_WEDNESDAY:
                            if (Calendar.WEDNESDAY == selectCalendar.get(Calendar.DAY_OF_WEEK) && !isSameDayCalendar){
                                selectList.add(schedule);
                            }
                            break;
                        case ScheduleConst.SCHEDULE_REPEAT_THURSDAY:
                            if (Calendar.THURSDAY == selectCalendar.get(Calendar.DAY_OF_WEEK) && !isSameDayCalendar){
                                selectList.add(schedule);
                            }
                            break;
                        case ScheduleConst.SCHEDULE_REPEAT_FRIDAY:
                            if (Calendar.FRIDAY == selectCalendar.get(Calendar.DAY_OF_WEEK) && !isSameDayCalendar){
                                selectList.add(schedule);
                            }
                            break;
                        case ScheduleConst.SCHEDULE_REPEAT_SATURDAY:
                            if (Calendar.SATURDAY == selectCalendar.get(Calendar.DAY_OF_WEEK) && !isSameDayCalendar){
                                selectList.add(schedule);
                            }
                            break;
                        default:
                            break;
                    }
                }
            } else {
                String repeatIndex = repeatIdArr[0];
                switch (repeatIndex) {
                    case ScheduleConst.SCHEDULE_REPEAT_NEVER:
                        //永不
                        if (sYear < endYear || (sYear == endYear && sMonth < endMonth) || (sYear == endYear && sMonth == endMonth && sDay <= endDay)) {
                            selectList.add(schedule);
                        }
                        break;
                    case ScheduleConst.SCHEDULE_REPEAT_EVERY_DAY:
                        //每天
                        selectList.add(schedule);
                        break;
                    case ScheduleConst.SCHEDULE_REPEAT_EVERY_WEEK:
                        //每周
                        int selectDayWeek = selectCalendar.get(Calendar.DAY_OF_WEEK);
                        int startDayWeek = scheduleCalendar.get(Calendar.DAY_OF_WEEK);
                        int endDayWeek = endScheduleCalendar.get(Calendar.DAY_OF_WEEK);

                        List<Integer> weekDayList = getWeekDayList(startDayWeek, endDayWeek);
                        for (int aWeekDay : weekDayList) {
                            if (aWeekDay == selectDayWeek) {
                                selectList.add(schedule);
                            }
                        }
                        break;
                    case ScheduleConst.SCHEDULE_REPEAT_EVERY_MONTH:
                        //每月
                        /*if (selectCalendar.get(Calendar.DAY_OF_MONTH) == scheduleCalendar.get(Calendar.DAY_OF_MONTH)){
                            selectList.add(schedule);
                        }*/
                        if ((endMonth == month && selectCalendar.get(Calendar.DAY_OF_MONTH) >= scheduleCalendar.get(Calendar.DAY_OF_MONTH) && selectCalendar.get(Calendar.DAY_OF_MONTH) <= endScheduleCalendar.get(Calendar.DAY_OF_MONTH)) ||
                                (endMonth > month && sDay >= day) || (endMonth > month && sMonth != month && sDay <= endDay)) {
                            selectList.add(schedule);
                        }
                        break;
                    case ScheduleConst.SCHEDULE_REPEAT_EVERY_YEAR:
                        //每年
                       /* if (sMonth == month && sDay == day){
                            selectList.add(schedule);
                        }*/
                        if (sMonth == month && sDay == day || (endMonth == month && sMonth == month && sDay > day && sDay <= endDay) || ((endMonth > month && sMonth == month && sDay >= day) || (endMonth > month && sMonth > month && sMonth < endMonth) || (endMonth > month && sMonth == endMonth && sDay <= endDay))) {
                            selectList.add(schedule);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        scheduleListBean.setScheduleList(selectList);
        return scheduleListBean;
    }

    private List<Integer> getWeekDayList(int startDayWeek, int endDayWeek) {
        List<Integer> weekArrayList = new ArrayList<>();
        int weekCount;
        if (endDayWeek >= startDayWeek) {
            weekCount = endDayWeek - startDayWeek + 1;
        } else {
            weekCount = 7 + endDayWeek - startDayWeek + 1;
        }

        for (int i = 0; i < weekCount; i++) {
            int weekDay;
            if (startDayWeek + i > 7) {
                weekDay = (startDayWeek + i) % 7;
            } else {
                weekDay = startDayWeek + i;
            }
            weekArrayList.add(weekDay);
        }

        return weekArrayList;
    }


    /**
     * 开启异步获取所有日程
     * */
    public void startAllScheduleSearch() {
        cancelAllScheduleSearchAsyncTask();
        if (null == mAllScheduleSearchAsyncTask) {
            mAllScheduleSearchAsyncTask = new AllScheduleSearchAsyncTask();
        }
        mAllScheduleSearchAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 获取所有日程，包括重复日程
     * */
    private List<Schedule> getAllScheduleList() {
        List<Schedule> rList = new ArrayList<>();
        List<Schedule> allList = DataBaseCalendarManager.getInstance(mContext).getAllScheduleData();
        if (null != allList && !allList.isEmpty()) {
            for (Schedule schedule : allList) {
                Calendar startCalendar = ScheduleUtil.getCalendar(schedule.getAlertTime());
                if (ScheduleConst.SCHEDULE_CUSTOM_REPEAT_MODE == schedule.getRepeatMode()) {
                    //自定义重复日程
                    List<Schedule> customRepeatList = getAllCustomRepeatSchedule(schedule);
                    rList.addAll(customRepeatList);
                } else {
                    List<Schedule> normalRepeatList = getNormalRepeatSchedule(schedule);
                    rList.addAll(normalRepeatList);
                }
            }
        }
        return rList;
    }

    private List<Schedule> getNormalRepeatSchedule(Schedule schedule) {
        List<Schedule> list = new ArrayList<>();
        String repeatId = schedule.getRepeatId();
        String[] repeatIdArr = repeatId.split(",");
        String repeatIndex = repeatIdArr[0];

        Calendar scheduleCalendar = Calendar.getInstance();
        scheduleCalendar.setTimeInMillis(schedule.getAlertTime());

        Calendar initCalendar = scheduleCalendar;

        Calendar maxCalendar = Calendar.getInstance();
        maxCalendar.set(Calendar.YEAR, initCalendar.get(Calendar.YEAR) + 2);
        maxCalendar.set(Calendar.MONTH, ConstData.MAX_MONTH);
        maxCalendar.set(Calendar.DAY_OF_MONTH, ConstData.MAX_DAY);

        Schedule repeatSchedule;
        switch (repeatIndex) {
            case ScheduleConst.SCHEDULE_REPEAT_NEVER:
                //永不

                break;
            case ScheduleConst.SCHEDULE_REPEAT_EVERY_DAY:
                //每天
                for (;scheduleCalendar.get(Calendar.YEAR) <= maxCalendar.get(Calendar.YEAR) &&
                        scheduleCalendar.get(Calendar.MONTH) <= maxCalendar.get(Calendar.MONTH) &&
                        scheduleCalendar.get(Calendar.DAY_OF_MONTH) <= maxCalendar.get(Calendar.DAY_OF_MONTH);) {
                    repeatSchedule = schedule;
                    repeatSchedule.setSortTimeMill(scheduleCalendar.getTimeInMillis());
                    list.add(repeatSchedule);
                    scheduleCalendar.set(Calendar.DAY_OF_MONTH, + 1);
                }
                break;
            case ScheduleConst.SCHEDULE_REPEAT_EVERY_WEEK:
                for (;scheduleCalendar.get(Calendar.YEAR) <= maxCalendar.get(Calendar.YEAR) &&
                        scheduleCalendar.get(Calendar.MONTH) <= maxCalendar.get(Calendar.MONTH) &&
                        scheduleCalendar.get(Calendar.DAY_OF_MONTH) <= maxCalendar.get(Calendar.DAY_OF_MONTH);) {
                    if (scheduleCalendar.get(Calendar.DAY_OF_WEEK) == scheduleCalendar.get(Calendar.DAY_OF_WEEK)) {
                        repeatSchedule = schedule;
                        repeatSchedule.setSortTimeMill(schedule.getAlertTime());
                        list.add(repeatSchedule);
                        scheduleCalendar.set(Calendar.WEEK_OF_YEAR, +1);
                    }
                }
                break;
            case ScheduleConst.SCHEDULE_REPEAT_EVERY_MONTH:
                //每月
                for (;scheduleCalendar.get(Calendar.YEAR) <= maxCalendar.get(Calendar.YEAR) &&
                        scheduleCalendar.get(Calendar.MONTH) <= maxCalendar.get(Calendar.MONTH) &&
                        scheduleCalendar.get(Calendar.DAY_OF_MONTH) <= maxCalendar.get(Calendar.DAY_OF_MONTH);) {
                    if (scheduleCalendar.get(Calendar.DAY_OF_MONTH) == scheduleCalendar.get(Calendar.DAY_OF_MONTH)) {
                        repeatSchedule = schedule;
                        repeatSchedule.setSortTimeMill(schedule.getAlertTime());
                        list.add(repeatSchedule);
                        scheduleCalendar.set(Calendar.WEEK_OF_YEAR, +1);
                    }

                }

                break;
            case ScheduleConst.SCHEDULE_REPEAT_EVERY_YEAR:
                //每年

                break;
            default:
                break;
        }

        return list;
    }

    private List<Schedule> getAllCustomRepeatSchedule(Schedule schedule) {
        List<Schedule> list = new ArrayList<>();
        String repeatId = schedule.getRepeatId();
        String[] repeatIdArr = repeatId.split(",");
        for (int i = 0; i < repeatIdArr.length; i++) {
            String customRepeatIndex = repeatIdArr[i];
            switch (customRepeatIndex) {
                case ScheduleConst.SCHEDULE_REPEAT_SUNDAY:

                    break;
                case ScheduleConst.SCHEDULE_REPEAT_MONDAY:

                    break;
                case ScheduleConst.SCHEDULE_REPEAT_TUESDAY:

                    break;
                case ScheduleConst.SCHEDULE_REPEAT_WEDNESDAY:

                    break;
                case ScheduleConst.SCHEDULE_REPEAT_THURSDAY:

                    break;
                case ScheduleConst.SCHEDULE_REPEAT_FRIDAY:

                    break;
                case ScheduleConst.SCHEDULE_REPEAT_SATURDAY:

                    break;
                default:
                    break;
            }
        }

        return list;
    }

    /**
     * 异步线程获取所有日程
     * */
    private class AllScheduleSearchAsyncTask extends AsyncTask<Void, Void, List<Schedule>> {
        @Override
        protected void onPostExecute(List<Schedule> list) {

        }

        @Override
        protected List<Schedule> doInBackground(Void... params) {
            return getAllScheduleList();
        }
    }

    /**
     * 异步线程获取月视图选中日期下的日程
     * */
    private class selectScheduleSearchAsyncTask extends AsyncTask<Void, Void, ScheduleListBean>{
        @Override
        protected void onPostExecute(ScheduleListBean scheduleListBean) {
            if (null != mScheduleListener){
                mScheduleListener.OnGetSelectScheduleList(scheduleListBean);
            }
        }

        @Override
        protected ScheduleListBean doInBackground(Void... params) {
            return getSelectScheduleList(mSelectDateMill);
        }
    }

    /**
     * 取消搜索异步线程
     * */
    private void cancelSelectScheduleSearchAsyncTask() {
        if (null != mSelectScheduleSearchAsyncTask) {
            mSelectScheduleSearchAsyncTask.cancel(true);
            mSelectScheduleSearchAsyncTask = null;
        }
    }

    private void cancelAllScheduleSearchAsyncTask() {
        if (null != mAllScheduleSearchAsyncTask) {
            mAllScheduleSearchAsyncTask.cancel(true);
            mAllScheduleSearchAsyncTask = null;
        }
    }

    public void close(){
        cancelSelectScheduleSearchAsyncTask();
        cancelAllScheduleSearchAsyncTask();
    }

}
