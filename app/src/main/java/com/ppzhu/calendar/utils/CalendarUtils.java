package com.ppzhu.calendar.utils;


import com.ppzhu.calendar.constants.ConstData;

import java.util.Calendar;

/**
 * Created by Jimmy on 2016/10/6 0006.
 */
public class CalendarUtils {
    public static final int NUM_ROWS = 6;
    /**
     * 通过年份和月份 得到当月的日子
     *
     * @param year
     * @param month
     * @return
     */
    public static int getMonthDays(int year, int month) {
        month++;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
                    return 29;
                } else {
                    return 28;
                }
            default:
                return -1;
        }
    }

    /**
     * 返回当前月份1号位于周几
     *
     * @param year  年份
     * @param month 月份，传入系统获取的，不需要正常的
     * @return 日：1		一：2		二：3		三：4		四：5		五：6		六：7
     */
    public static int getFirstDayWeek(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 返回该日期位于周几
     * */
    public static int getDayWeek(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 返回该日期是否周末
     * */
    public static boolean isWeekend(int year, int month, int day) {
        int dayWeek = getDayWeek(year, month, day);
        return Calendar.SUNDAY == dayWeek || Calendar.SATURDAY == dayWeek;
    }

    /**
     * 获得两个日期距离几周
     * @param lastYear
     * @param lastMonth 月份从0开始
     * @param lastDay
     * @param year
     * @param month 月份从0开始
     * @param day
     *
     * @return
     */
    public static int getWeeksAgo(int lastYear, int lastMonth, int lastDay, int year, int month, int day) {
        Calendar lastClickDay = Calendar.getInstance();
        lastClickDay.set(lastYear, lastMonth, lastDay);
        int week = lastClickDay.get(Calendar.DAY_OF_WEEK) - 1;
        Calendar clickDay = Calendar.getInstance();
        clickDay.set(year, month, day);
        if (clickDay.getTimeInMillis() > lastClickDay.getTimeInMillis()) {
            return (int) ((clickDay.getTimeInMillis() - lastClickDay.getTimeInMillis() + week * 24 * 3600 * 1000) / (7 * 24 * 3600 * 1000));
        } else {
            return (int) ((clickDay.getTimeInMillis() - lastClickDay.getTimeInMillis() + (week - 6) * 24 * 3600 * 1000) / (7 * 24 * 3600 * 1000));
        }
    }

    /**
     * 获得两个日期距离几个月
     *
     * @return
     */
    public static int getMonthsAgo(int lastYear, int lastMonth, int year, int month) {
        return (year - lastYear) * 12 + (month - lastMonth);
    }

    public static int getWeekRow(int year, int month, int day) {
        int week = getFirstDayWeek(year, month);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        int lastWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (lastWeek == 7)
            day--;
        return (day + week - 1) / 7;
    }

    /**
     * 获取该月有几周
     * */
    public static int getWeekRows(int year, int month) {
        int numRows;
        int monthDays = CalendarUtils.getMonthDays(year, month);
        int weekNumber = CalendarUtils.getFirstDayWeek(year, month);
        //上个月占的天数
        int lastMonthDays = weekNumber - 1;
        //下个月占的天数
        int nextMonthDays = 42 - monthDays - weekNumber + 1;
        numRows = NUM_ROWS - (lastMonthDays / 7 + nextMonthDays / 7);

        return numRows;
    }

    public static int getWeekDayCount(int startWeekDay, int endWeekDay) {
        int weekCount;
        if (endWeekDay >= startWeekDay) {
            weekCount = endWeekDay - startWeekDay;
        } else {
            weekCount = 7 + endWeekDay - startWeekDay;
        }
        return weekCount;
    }

    /**
     * 获取日历最小日期
     * */
    public static Calendar getMinCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, ConstData.MIN_YEAR);
        calendar.set(Calendar.MONTH, ConstData.MIN_MONTH - 1);
        calendar.set(Calendar.DAY_OF_MONTH, ConstData.MIN_DAY);
        calendar.set(Calendar.HOUR_OF_DAY, ConstData.MIN_HOUR);
        calendar.set(Calendar.MINUTE, ConstData.MIN_MINUTE);
        calendar.set(Calendar.SECOND, ConstData.MIN_SECOND);
        return calendar;
    }

    /**
     * 获取日历最大日期
     * */
    public static Calendar getMaxCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, ConstData.MAX_YEAR);
        calendar.set(Calendar.MONTH, ConstData.MAX_MONTH - 1);
        calendar.set(Calendar.DAY_OF_MONTH, ConstData.MAX_DAY);
        calendar.set(Calendar.HOUR_OF_DAY, ConstData.MAX_HOUR);
        calendar.set(Calendar.MINUTE, ConstData.MAX_MINUTE);
        calendar.set(Calendar.SECOND, ConstData.MAX_SECOND);
        return calendar;
    }

}

