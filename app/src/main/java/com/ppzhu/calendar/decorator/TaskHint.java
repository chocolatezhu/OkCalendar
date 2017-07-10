package com.ppzhu.calendar.decorator;

import android.content.Context;
import android.text.format.DateFormat;


import com.ppzhu.calendar.bean.Festival;
import com.ppzhu.calendar.bean.Schedule;
import com.ppzhu.calendar.constants.ScheduleConst;
import com.ppzhu.calendar.database.DataBaseCalendarManager;
import com.ppzhu.calendar.utils.CalendarUtils;
import com.ppzhu.calendar.utils.ScheduleUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author zzl
 * 管理日程圆点
 * Created on 2017/1/11.
 */
public class TaskHint {
    private Context mContext;

    public static final int WEEK_MAX_DAY = 7;//每周7天
    public static final int MONTH_MAX_DAY = 31 + WEEK_MAX_DAY * 2;//前后各一个星期

    public TaskHint(Context context) {
        mContext = context;
    }

    public static TaskHint getInstance(Context context) {
        return new TaskHint(context);
    }

    /**
     * 获取月的日程显示点
     * */
    public boolean[] geTaskHintByMonth(int year, int month) {
        return getScheduleListByMonth(year, month);
    }

    /**
     * 获取周的日程显示点
     *
     * @param startDate*/
    public boolean[] getTaskHintByWeek(DateTime startDate) {
        return getScheduleListByWeek(startDate);
    }


    public int[] getFestivalHintByMonth(int year, int month) {
        return getFestivalListByMonth(year, month);
    }

    public int[] getFestivalHintByWeek(DateTime startDate) {
        return getFestivalListByWeek(startDate);
    }

    private int[] getFestivalListByMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        String dateStr = (String) DateFormat.format("yyyyMM", calendar);
        List<Festival> list = DataBaseCalendarManager.getInstance(mContext).getFestivalObjListByMonth(dateStr);
        return getFestivalStateArrayByMonth(list, year, month);
    }

    private int[] getFestivalListByWeek(DateTime startDate) {
        List<Festival> list = DataBaseCalendarManager.getInstance(mContext).getAllFestivalObjList();
        return getFestivalStateArrayByWeek(list, startDate);
    }

    /**
     * 获取指定月份的节假日
     * @param list
     * @param startDate
     * */
    private int[] getFestivalStateArrayByWeek(List<Festival> list, DateTime startDate) {
        int[] festivalState = new int[WEEK_MAX_DAY];
        for (int i = 0; i < WEEK_MAX_DAY; i++) {
            DateTime date = startDate.plusDays(i);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date.toDate());

            String dateStr = (String) DateFormat.format("yyyyMMdd", calendar);
            int stateIndex = getStateIndex(dateStr, list);
            festivalState[i] = stateIndex;
        }
        return festivalState;
    }

    /**
     * 获取指定月份的节假日
     * @param list
     * @param year
     * @param month 指定的月份 */
    private int[] getFestivalStateArrayByMonth(List<Festival> list, int year, int month) {
        int[] festivalState = new int[MONTH_MAX_DAY];
        int monthDays = CalendarUtils.getMonthDays(year, month);
        for (int day = 0; day < monthDays; day++) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day + 1);

            String dateStr = (String) DateFormat.format("yyyyMMdd", calendar);
            int stateIndex = getStateIndex(dateStr, list);
            festivalState[day] = stateIndex;
        }
        return festivalState;
    }

    /**
     * 获取某一天节假日标记0，1，默认为-1
     * @param dateStr 判断这一天是否有放假标记
     * @param list
     * */
    private int getStateIndex(String dateStr, List<Festival> list) {
        int festivalState = -1;
        for (int i = 0; i < list.size(); i ++) {
            Festival festival = list.get(i);
            if (dateStr.equals(festival.getDay())) {
                festivalState = festival.getIsFestival();
            }
        }
        return festivalState;
    }

    private boolean[] getScheduleListByWeek(DateTime startDate) {
        boolean[] scheduleCircle = new boolean[WEEK_MAX_DAY];
        List<Schedule> allList = DataBaseCalendarManager.getInstance(mContext).getAllScheduleData();
        //获取联系人生日提醒
        List<Schedule> birthdayList = ScheduleUtil.getAllBirthDayScheduleList(mContext);
        if (null != birthdayList && !birthdayList.isEmpty()) {
            allList.addAll(birthdayList);
        }

        for (int i = 0; i < WEEK_MAX_DAY; i++) {
            DateTime date = startDate.plusDays(i);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date.toDate());
            List<Schedule> list  = getSelectScheduleList(calendar, allList);
            scheduleCircle[i] = list != null && !list.isEmpty();
        }

        return scheduleCircle;
    }

    /**
     * 获取月的日程列表
     *
     * @param year
     * @param month
     */
    private boolean[] getScheduleListByMonth(int year, int month) {
        boolean[] scheduleCircle = new boolean[MONTH_MAX_DAY];
        List<Schedule> allList = DataBaseCalendarManager.getInstance(mContext).getAllScheduleData();
        //获取联系人生日提醒
        List<Schedule> birthdayList = ScheduleUtil.getAllBirthDayScheduleList(mContext);
        if (null != birthdayList && !birthdayList.isEmpty()) {
            allList.addAll(birthdayList);
        }
        int monthDays = CalendarUtils.getMonthDays(year, month);
        for (int day = 0; day < monthDays; day++) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day + 1);
            Date time = new Date();
            List<Schedule> list  = getSelectScheduleList(calendar, allList);
            scheduleCircle[day] = list != null && !list.isEmpty();
        }
        return scheduleCircle;
    }

    /**
     * 获取今天以后的某一天是否有日程提醒
     *
     * @param selectCalendar
     * @param allList
     */
    private List<Schedule> getSelectScheduleList(Calendar selectCalendar, List<Schedule> allList) {
        List<Schedule> selectList = new ArrayList<>();
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
            if (sYear < year) {
                continue;
            } else if (sYear == year) {
                if (sMonth < month) {
                    continue;
                } else if (sMonth == month) {
                    if (sDay < day) {
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
            if (ScheduleConst.SCHEDULE_CUSTOM_REPEAT_MODE == schedule.getRepeatMode()) {
                //自定义重复列表
                for (int j = 0; j < repeatIdArr.length; j++) {
                    //如果选中日期与创建日期相等说明当天有日程
                    boolean isSameDayCalendar = ScheduleUtil.isSameDayCalendar(selectCalendar, scheduleCalendar);
                    if (isSameDayCalendar) {
                        selectList.add(schedule);
                    }
                    String customRepeatIndex = repeatIdArr[j];
                    switch (customRepeatIndex) {
                        case ScheduleConst.SCHEDULE_REPEAT_SUNDAY:
                            if (Calendar.SUNDAY == selectCalendar.get(Calendar.DAY_OF_WEEK)) {
                                selectList.add(schedule);
                                return selectList;
                            }
                            break;
                        case ScheduleConst.SCHEDULE_REPEAT_MONDAY:
                            if (Calendar.MONDAY == selectCalendar.get(Calendar.DAY_OF_WEEK)) {
                                selectList.add(schedule);
                                return selectList;
                            }
                            break;
                        case ScheduleConst.SCHEDULE_REPEAT_TUESDAY:
                            if (Calendar.TUESDAY == selectCalendar.get(Calendar.DAY_OF_WEEK)) {
                                selectList.add(schedule);
                                return selectList;
                            }
                            break;
                        case ScheduleConst.SCHEDULE_REPEAT_WEDNESDAY:
                            if (Calendar.WEDNESDAY == selectCalendar.get(Calendar.DAY_OF_WEEK)) {
                                selectList.add(schedule);
                                return selectList;
                            }
                            break;
                        case ScheduleConst.SCHEDULE_REPEAT_THURSDAY:
                            if (Calendar.THURSDAY == selectCalendar.get(Calendar.DAY_OF_WEEK)) {
                                selectList.add(schedule);
                                return selectList;
                            }
                            break;
                        case ScheduleConst.SCHEDULE_REPEAT_FRIDAY:
                            if (Calendar.FRIDAY == selectCalendar.get(Calendar.DAY_OF_WEEK)) {
                                selectList.add(schedule);
                                return selectList;
                            }
                            break;
                        case ScheduleConst.SCHEDULE_REPEAT_SATURDAY:
                            if (Calendar.SATURDAY == selectCalendar.get(Calendar.DAY_OF_WEEK)) {
                                selectList.add(schedule);
                                return selectList;
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
                        if (sYear<endYear||(sYear==endYear&&sMonth<endMonth)||(sYear==endYear&&sMonth==endMonth&&sDay<=endDay)){
                            selectList.add(schedule);
                            return selectList;
                        }
                        //永不
                        break;
                    case ScheduleConst.SCHEDULE_REPEAT_EVERY_DAY:
                        //每天
                        selectList.add(schedule);
                        return selectList;

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
                        if ((endMonth==month&&selectCalendar.get(Calendar.DAY_OF_MONTH) >= scheduleCalendar.get(Calendar.DAY_OF_MONTH)&&selectCalendar.get(Calendar.DAY_OF_MONTH) <= endScheduleCalendar.get(Calendar.DAY_OF_MONTH)) ||
                                (endMonth>month&&sDay>=day)||(endMonth>month&&sMonth!=month&&sDay<=endDay)){
                            selectList.add(schedule);
                            return selectList;
                        }
                        break;
                    case ScheduleConst.SCHEDULE_REPEAT_EVERY_YEAR:
                        //每年
                        if (sMonth == month && sDay == day||(endMonth==month&&sMonth == month &&sDay>day&&sDay<=endDay)||((endMonth>month&&sMonth==month&&sDay>=day)||(endMonth>month&&sMonth>month&&sMonth<endMonth)||(endMonth>month&&sMonth==endMonth&&sDay<=endDay))) {
                            selectList.add(schedule);
                            return selectList;
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return selectList;
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
}
