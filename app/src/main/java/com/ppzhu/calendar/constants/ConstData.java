package com.ppzhu.calendar.constants;

/**
 * 文  件：ConstData.java
 * 公  司：步步高教育电子
 * 日  期：2016/7/13  10:43
 * 作  者：HeChangPeng
 */

public class ConstData {

    /**
     * 1970-01-01到2037-12-31的黄历数据总数
     * <p> 如果黄历数据库条目数量有更改，该数值也需要更新 </p>
     * */
    public static final int MAX_ALMANAC_COUNT = 24837;

    /**
     * 1970年到2037年周数
     * */
    public static final int MAX_WEEK_COUNT = 3549;
    /**
     * 最大年份
     */
    public static final int MAX_YEAR = 2037;//2037
    public static final int MAX_MONTH = 12;
    public static final int MAX_DAY = 31;
    public static final int MAX_HOUR = 23;
    public static final int MAX_MINUTE = 59;
    public static final int MAX_SECOND = 59;
    public static final String MAX_DATE_STR = "2037年12月";
    /**
     * 最小年份
     */
    public static final int MIN_YEAR = 1970;//1970
    public static final int MIN_MONTH = 1;
    public static final int MIN_DAY = 1;
    public static final int MIN_HOUR = 0;
    public static final int MIN_MINUTE = 0;
    public static final int MIN_SECOND = 0;
    public static final String MIN_DATE_STR = "1970年1月";

    public static final int SOFT_INPUT_DELAY_TIME = 100;

    public static final String CALENDAR_PERMISSION = "Calendar_Permission";
    public static final String FESTIVAL_UPDATETIME = "updatetime";
    public static final String FESTIVAL_IS_UPDATE = "fesitival_is_update";
    public static final String FESTIVAL_KEY = "festival";
    public static final String IS_FIRST = "isFirst";
    public static final String TAG = "hecp";

    public static final String BUNDLE_KEY_RESTORE_TIME = "key_restore_time";
    public static final String BUNDLE_KEY_EVENT_ID = "key_event_id";
    public static final String BUNDLE_KEY_RESTORE_VIEW = "key_restore_view";
    public static final String YEAR = "year";

    public static final String IS_MINI_MONTH = "isMiniMonth";
    public static final String INITIAL_TIME = "initialTime";
    public static final String SELECT_DAY = "mSelectedDay";
    public static final String TIME_MILLIS = "timeInMillis";

    public static final String[] REMINDERSTR = {"无","日程发生时","5分钟前","15分钟前","30分钟前","1小时前","2小时前","1天前","2天前"};
    public static final String[] REPEATSTR = {"永不","每天","每周","每月","每年","自定"};
    public static final String[] CUSTOMREPEATSTR = {"周日","周一","周二","周三","周四","周五","周六"};
    public static final String[] ALLREPEATSTR = {"永不","每天","每周","每月","每年","周日","周一","周二","周三","周四","周五","周六"};

    public static final int repeatCode = 0x2;
    public static final int reminderCode = 0x4;
    public static final String[] LUNAR_MONTH = {"正月初","二月初","三月初","四月初","五月初","六月初","七月初","八月初","九月初","十月初","十一月初","腊月初"};
    public static final String[] LUNAR_DAY = {"一","二","三","四","五","六","七","八","九","十","十一","十二",
           "十三","十四","十五","十六","十七","十八","十九","二十","廿一","廿二","廿三","廿四","廿五",
            "廿六","廿七","廿八","廿九","三十","三十一"};
    public static final String[] LUNAR_MONTHS = {"正月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","腊月"};

    public static final int SCHEDULE_UPDATE = 0x6;
    public static final int SCHEDULE_ADD = 0x8;
    public static final int SCHEDULE_SHOW = 0x10;
    public static final int SCHEDULE_REFRESH = 0x11;
    public static final int CLICK_TODAY = 0x12;
    public static final int SCHEDULE_SEARCH = 0x13;

    public static int POSITION = 0;

    public static int CURRENT_SELECT_MONTH = 11;

    public static final String SCHEDULE_ADD_ACTION = "com.eebbk.studyos.calendar.addschedule.action";

   // public static final String SCHEDULE_EDIT_ACTION = "com.eebbk.studyos.calendar.scheduleshow.action";

    public static final String TODAY_ACTION = "com.eebbk.studyos.calendar.click.today.action";

    public static final int INTENT_SCHEDULE_REFRESH = 0x1001;
    public static final int INTENT_SCHEDULE_SHOW_REFRESH = 0x1002;
    public static final int INTENT_SCHEDULE_SEARCH = 0x1003;
    public static final int INTENT_SCHEDULE_BIRTHDAY_REFRESH = 0x1004;
    public static final String INTENT_SCHEDULE_EDIT_KEY = "schedule_edit_key";
    public static final String INTENT_SCHEDULE_REPEAT_EDIT_KEY = "schedule_repeat_edit_key";
    public static final String INTENT_SCEDULE_REMIND_EDIT_KEY = "schedule_remind_edit_key";
    public static final String INTENT_SCHEDULE_SELECT_DATE_KEY = "schedule_select_date_key";
    public static final String INTENT_SCHEDULE_REPEAT_IS_CUSTOM_REPEAT = "schedule_repeat_is_custom_repeat";
    public static final String INTENT_SCHEDULE_REPEAT_FREE_SET_ITEM = "schedule_repeat_free_set_item";


    public static final String INTENT_SELECT_YEAR_INTENT = "select_year_key";

    public static final String INTENT_ALMANAC_DATA_KEY = "almanac_data";
    public static final String INTENT_ALMANAC_IDX_KEY = "almanac_idx";

}
