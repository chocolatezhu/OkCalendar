package com.ppzhu.calendar.constants;

/**
 * 日程使用的常量
 * @author zzl
 * Created on 2016/9/21.
 */
public class ScheduleConst {
    public static final int SCHEDULE_ALERT_NONE = 0;
    public static final int SCHEDULE_ALERT_HAPPEN = 1;
    public static final int SCHEDULE_ALERT_FIVE_MIN_AGO = 2;
    public static final int SCHEDULE_ALERT_FIFTEEN_MIN_AGO = 3;
    public static final int SCHEDULE_ALERT_THIRTY_MIN_AGO = 4;
    public static final int SCHEDULE_ALERT_ONE_HOUR_AGO = 5;
    public static final int SCHEDULE_ALERT_TWO_HOUR_AGO = 6;
    public static final int SCHEDULE_ALERT_ONE_DAY_AGO = 7;
    public static final int SCHEDULE_ALERT_TWO_DAY_AGO = 8;

    public static final String SCHEDULE_REPEAT_NEVER = "0";
    public static final String SCHEDULE_REPEAT_EVERY_DAY = "1";
    public static final String SCHEDULE_REPEAT_EVERY_WEEK= "2";
    public static final String SCHEDULE_REPEAT_EVERY_MONTH = "3";
    public static final String SCHEDULE_REPEAT_EVERY_YEAR = "4";

    public static final String SCHEDULE_REPEAT_SUNDAY = "0";
    public static final String SCHEDULE_REPEAT_MONDAY = "1";
    public static final String SCHEDULE_REPEAT_TUESDAY = "2";
    public static final String SCHEDULE_REPEAT_WEDNESDAY = "3";
    public static final String SCHEDULE_REPEAT_THURSDAY = "4";
    public static final String SCHEDULE_REPEAT_FRIDAY = "5";
    public static final String SCHEDULE_REPEAT_SATURDAY = "6";

    public static final int MAX_TEXT_INPUT_LENGTH = 128;
    public static final int MAX_SCHEDULE_TITLE_INPUT_LENGTH = 50;
    public static final int MAX_SCHEDULE_REMARK_INPUT_LENGTH = 200;

    public static final int MAX_HOUR = 23;
    public static final int MAX_MINUTE = 59;
    public static final int MAX_SECOND = 59;

    //正常情况重复
    public static final int SCHEDULE_NORMAL_REPEAT_MODE = 0;
    //自定义重复
    public static final int SCHEDULE_CUSTOM_REPEAT_MODE = 1;

    public static final int SCHEDULE_NOT_ALL_DAY = 0;
    public static final int SCHEDULE_IS_ALL_DAY = 1;

    public static final String SHUT_DOWN_TIME_KEY = "shut_down_time";

    public static final String SCHEDULE_ALERT_STOP_ACTION = "com.eebbk.schedule.alert.stop.action";

    public static final String INTENT_SCHEDULE_ALERT_KEY = "schedule_alert_key";

    public static final String INTENT_SCHEDULE_ALERT_DIALOG_KEY = "schedule_alert_dialog_key";
    //跳转联系人详情界面Action
    public static final String ACTION_CONTACT_DETAIL = "bbk.provider.action.CONTACT_DETAIL";
}
