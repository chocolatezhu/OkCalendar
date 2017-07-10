package com.ppzhu.calendar.constants;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 日程数据库保存字段
 * @author zzl
 * Created on 2016/8/12.
 */
public class ScheduleColums implements BaseColumns {

    public static final String TABLE_NAME = "schedule";

    public static final String AUTOHORITY = "com.eebbk.calendar";

    public static final String TITLE = "title";

    public static final String LOCATION = "location";

    public static final String REMARK = "remark";

    public static final String STARTTIME = "startTime";

    public static final String ENDTIME = "endTime";

    public static final String REPEATID =  "repeatId";

    public static final String REMINDID =  "remindId";

    public static final String ALLDAY =  "allDay";

    public static final String ALERTTIME = "alertTime";

    public static final String ENDTIMEMILL = "endTimeMill";

    public static final String REPEATMODE = "repeatMode";

    public static final String DATE =  "date";

    public static final int ITEM = 1;

    public static final int ITEM_ID = 2;

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.studyos.calendar";

    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.studyos.calendar";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTOHORITY + "/" + TABLE_NAME);
}
